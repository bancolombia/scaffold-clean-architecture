package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.APP_BUILD_GRADLE;
import static co.com.bancolombia.Constants.MainFiles.BUILD_GRADLE;
import static co.com.bancolombia.Constants.MainFiles.GRADLE_PROPERTIES;
import static co.com.bancolombia.Constants.MainFiles.MAIN_GRADLE;
import static co.com.bancolombia.Constants.MainFiles.SETTINGS_GRADLE;
import static co.com.bancolombia.factory.upgrades.actions.ZUpdateDependencies.FILES_TO_UPDATE;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpdateUtils;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import co.com.bancolombia.models.Release;
import co.com.bancolombia.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.SneakyThrows;

/** Migrates a scaffold-clean-architecture 4.x consumer project to the 5.0.0 layout. */
public class UpgradeY2026M11D07ScaffoldFive implements UpgradeAction {

  private static final Pattern STABLE_FIVE_VERSION = Pattern.compile("5\\.\\d+\\.\\d+");
  private static final Pattern BETA5_FLAG = Pattern.compile("(?m)^beta5\\s*=\\s*true\\s*$");
  private static final String BETA5_VERSION = "5.0.0-beta";
  private static final String SETTINGS_PLUGIN_ID = "co.com.bancolombia.cleanArchitecture.settings";

  private static final String NEW_PLUGINS_BLOCK =
      """
      plugins {
          id 'co.com.bancolombia.cleanArchitecture'
          id 'org.springframework.boot' apply false
          id 'org.sonarqube'
          id 'info.solidsoft.pitest' apply false
      }""";

  private static final Pattern ROOT_PROJECT_NAME = Pattern.compile("rootProject\\.name\\s*=\\s*.+");
  private static final Pattern SONAR_EXCLUSIONS =
      Pattern.compile("property\\s+\"sonar\\.exclusions\",\\s*\"([^\"]*)\"");
  private static final Set<String> SONAR_EXCLUSIONS_SET =
      Set.of("**/MainApplication.java", "**/MainApplication.java,**/**.bin,**/deployment/**");
  private static final Pattern AWS_BOM_LINE =
      Pattern.compile(
          "(?m)^\\s*(\\w+\\s+platform\\(['\"]software\\.amazon\\.awssdk:bom[^'\"]*['\"]\\))");
  private static final Pattern JAVA_TOOLCHAIN_VERSION =
      Pattern.compile("JavaLanguageVersion\\.of\\((\\d+)\\)");
  private static final Pattern PROJECT_DEPENDENCY =
      Pattern.compile("project\\(\\s*['\"]:([^'\"]+)['\"]\\s*\\)");
  private static final Pattern IMPLEMENTATION_COORDINATE =
      Pattern.compile("(?m)^\\s*implementation\\s+['\"]([^'\"]+)['\"]");
  private static final Pattern PLUGIN_V5_APP_SERVICE_DEPENDENCY =
      Pattern.compile(
          "^\\s*(implementation\\s+['\"]org\\.springframework\\.boot:spring-boot-starter['\"]"
              + "|runtimeOnly\\s*(?:\\(\\s*)?['\"]org\\.springframework\\.boot:spring-boot-devtools['\"]\\s*\\)?)\\s*$");
  private static final Pattern PITEST_BLOCK = Pattern.compile("(?m)^\\s*pitest\\s*\\{");
  private static final Pattern PITEST_ARRAY_PROPERTY =
      Pattern.compile("(?m)^\\s*(excluded(?:Test)?Classes\\s*=\\s*(\\[[^]]*\\]))\\s*$");
  private static final Pattern EMPTY_ARRAY = Pattern.compile("\\[\\s*\\]");
  public static final String SPACES = "        ";
  private static final String CONFIGURATIONS_ALL = "configurations.all";
  private static final String DEPENDENCY_MGMT_GRADLE = "./dependencyMgmt.gradle";
  private static final String BUILDSCRIPT = "buildscript";
  private static final String EXT = "ext";
  private static final Set<String> PLUGIN_V5_MANAGED_EXT_VERSIONS =
      Set.of(
          "pitestVersion",
          "cleanArchitectureVersion",
          "springBootVersion",
          "sonarVersion",
          "jacocoVersion",
          "lombokVersion",
          "pitestHistoryVersion",
          "blockHoundVersion");
  private static final Pattern EXT_PROPERTY_LINE = Pattern.compile("^\\s*(\\w+)\\s*=\\s*.+$");

  @Override
  @SneakyThrows
  public boolean up(ModuleBuilder builder) {
    String targetVersion = resolveTargetVersion(builder);
    if (targetVersion == null) {
      builder
          .getLogger()
          .lifecycle("No stable 5.x.x release found yet, skipping scaffold 5 migration");
      return false;
    }
    String mainGradleContent = readMainGradle(builder);
    boolean applied = upgradeSettingsGradle(builder, targetVersion);
    applied |= upgradeBuildGradle(builder, mainGradleContent);
    applied |= upgradeGradleProperties(builder, mainGradleContent, targetVersion);
    applied |= cleanChildModules(builder);
    if (mainGradleContent != null) {
      builder.removeDir(MAIN_GRADLE);
      applied = true;
    }
    applied |= removeDependencyMgmtGradle(builder);
    return applied;
  }

  private boolean removeDependencyMgmtGradle(ModuleBuilder builder) {
    if (!dependencyMgmtGradleExists(builder)) {
      return false;
    }
    builder.removeDir(DEPENDENCY_MGMT_GRADLE);
    return true;
  }

  private boolean dependencyMgmtGradleExists(ModuleBuilder builder) {
    try {
      builder.readFile(DEPENDENCY_MGMT_GRADLE);
      return true;
    } catch (IOException e) {
      return false;
    }
  }

  /**
   * Opting in via {@code beta5=true} in gradle.properties forces the 5.0.0-beta version; otherwise
   * only a stable (non -beta/-alpha/-RC/-M) 5.x.x plugin release is a valid migration target.
   */
  private String resolveTargetVersion(ModuleBuilder builder) {
    if (isBeta5Requested(builder)) {
      return BETA5_VERSION;
    }
    Release release = builder.getLatestRelease();
    if (release == null || release.getTagName() == null) {
      return null;
    }
    String version = release.getTagName();
    return STABLE_FIVE_VERSION.matcher(version).matches() ? version : null;
  }

  private boolean isBeta5Requested(ModuleBuilder builder) {
    try {
      return BETA5_FLAG.matcher(builder.readFile(GRADLE_PROPERTIES)).find();
    } catch (IOException e) {
      return false;
    }
  }

  private String readMainGradle(ModuleBuilder builder) {
    try {
      return builder.readFile(MAIN_GRADLE);
    } catch (IOException e) {
      return null;
    }
  }

  private boolean upgradeSettingsGradle(ModuleBuilder builder, String targetVersion)
      throws IOException {
    return builder.updateFile(
        SETTINGS_GRADLE,
        content -> {
          if (content.contains(SETTINGS_PLUGIN_ID)) {
            return content;
          }
          String pluginManagement = UpdateUtils.extractBlock(content, "pluginManagement");
          if (pluginManagement == null) {
            return content;
          }
          Matcher rootProjectName = ROOT_PROJECT_NAME.matcher(content);
          String rootProjectLine =
              rootProjectName.find() ? rootProjectName.group() : "rootProject.name = 'app'";
          return pluginManagement
              + "\n\nplugins {\n    id '"
              + SETTINGS_PLUGIN_ID
              + "' version '"
              + targetVersion
              + "'\n}\n\n"
              + rootProjectLine
              + "\n";
        });
  }

  private boolean upgradeBuildGradle(ModuleBuilder builder, String mainGradleContent)
      throws IOException {
    return builder.updateFile(
        BUILD_GRADLE,
        content -> {
          if (content.contains(SETTINGS_PLUGIN_ID) || content.strip().equals(NEW_PLUGINS_BLOCK)) {
            return content;
          }
          String sonarExclusions = extractSonarExclusions(content);
          StringBuilder result = new StringBuilder();
          String preservedBuildscript = buildPreservedBuildscript(content);
          if (!preservedBuildscript.isEmpty()) {
            result.append(preservedBuildscript).append("\n\n");
          }
          result.append(NEW_PLUGINS_BLOCK).append("\n\n");
          result.append(buildRootProjectsBlocks(mainGradleContent));
          if (sonarExclusions != null) {
            result.append("\n").append(buildSonarBlock(sonarExclusions));
          }
          return result.toString();
        });
  }

  private String buildPreservedBuildscript(String content) {
    String buildscriptBlock = UpdateUtils.extractBlock(content, BUILDSCRIPT);
    if (buildscriptBlock == null) {
      return "";
    }
    String extBlock = UpdateUtils.extractBlock(buildscriptBlock, EXT);
    if (extBlock == null) {
      return buildscriptBlock;
    }
    int extBodyStart = extBlock.indexOf('{');
    int extBodyEnd = extBlock.lastIndexOf('}');
    if (extBodyStart == -1 || extBodyEnd <= extBodyStart) {
      return buildscriptBlock;
    }
    String extPrefix = extBlock.substring(0, extBodyStart + 1);
    String extSuffix = extBlock.substring(extBodyEnd);
    String prunedExtBody =
        Arrays.stream(extBlock.substring(extBodyStart + 1, extBodyEnd).split("\n"))
            .filter(line -> line.isBlank() || !isPluginManagedExtVersionLine(line))
            .collect(Collectors.joining("\n"));
    if (!extBlockHasProperties(prunedExtBody)) {
      return "";
    }
    String prunedExtBlock = extPrefix + prunedExtBody + extSuffix;
    return buildscriptBlock.replace(extBlock, prunedExtBlock);
  }

  private boolean extBlockHasProperties(String extBody) {
    return Arrays.stream(extBody.split("\n"))
        .anyMatch(line -> EXT_PROPERTY_LINE.matcher(line).matches());
  }

  private boolean isPluginManagedExtVersionLine(String line) {
    Matcher matcher = EXT_PROPERTY_LINE.matcher(line);
    return matcher.matches() && PLUGIN_V5_MANAGED_EXT_VERSIONS.contains(matcher.group(1));
  }

  private String extractSonarExclusions(String content) {
    Matcher matcher = SONAR_EXCLUSIONS.matcher(content);
    if (matcher.find()) {
      var match = matcher.group(1);
      if (SONAR_EXCLUSIONS_SET.contains(match)) {
        return null;
      }
      return match;
    }
    return null;
  }

  private String buildRootProjectsBlocks(String mainGradleContent) {
    String repositoriesBlock = null;
    String bomLine = null;
    if (mainGradleContent != null) {
      String allprojectsBlock = UpdateUtils.extractBlock(mainGradleContent, "allprojects");
      if (allprojectsBlock != null) {
        repositoriesBlock = UpdateUtils.extractBlock(allprojectsBlock, "repositories");
      }
      Matcher bomMatcher = AWS_BOM_LINE.matcher(mainGradleContent);
      if (bomMatcher.find()) {
        bomLine = bomMatcher.group(1).trim();
      }
    }
    StringBuilder sb = new StringBuilder("allprojects {\n    ");
    sb.append(repositoriesBlock != null ? repositoriesBlock : "repositories {\n    }").append("\n");
    sb.append("}\n\nsubprojects {\n    dependencies {\n");
    if (bomLine != null) {
      sb.append(SPACES).append(bomLine).append("\n");
    }
    sb.append("    }\n");
    String pitestExclusions = buildPitestExclusionsBlock(mainGradleContent);
    if (pitestExclusions != null) {
      sb.append(pitestExclusions);
    }
    sb.append("}\n");
    return sb.toString();
  }

  private String buildPitestExclusionsBlock(String mainGradleContent) {
    if (mainGradleContent == null) {
      return null;
    }
    String subprojectsBlock = UpdateUtils.extractBlock(mainGradleContent, "subprojects");
    String pitestScope = subprojectsBlock != null ? subprojectsBlock : mainGradleContent;
    String pitestBlock = extractPitestConfigurationBlock(pitestScope);
    if (pitestBlock == null) {
      return null;
    }
    String excludedClasses = extractNonEmptyPitestArrayProperty(pitestBlock, "excludedClasses");
    String excludedTestClasses =
        extractNonEmptyPitestArrayProperty(pitestBlock, "excludedTestClasses");
    if (excludedClasses == null && excludedTestClasses == null) {
      return null;
    }
    StringBuilder pitest = new StringBuilder("    pitest {\n");
    if (excludedClasses != null) {
      pitest.append(SPACES).append(excludedClasses).append("\n");
    }
    if (excludedTestClasses != null) {
      pitest.append(SPACES).append(excludedTestClasses).append("\n");
    }
    pitest.append("    }\n");
    return pitest.toString();
  }

  /**
   * Resolves the {@code pitest { ... }} block, not plugin ids such as {@code
   * info.solidsoft.pitest}.
   */
  private String extractPitestConfigurationBlock(String content) {
    Matcher matcher = PITEST_BLOCK.matcher(content);
    if (!matcher.find()) {
      return null;
    }
    return UpdateUtils.extractBlock(content.substring(matcher.start()), "pitest");
  }

  private String extractNonEmptyPitestArrayProperty(String pitestBlock, String propertyName) {
    Matcher matcher = PITEST_ARRAY_PROPERTY.matcher(pitestBlock);
    while (matcher.find()) {
      String assignment = matcher.group(1).trim();
      if (!assignment.startsWith(propertyName)) {
        continue;
      }
      if (EMPTY_ARRAY.matcher(matcher.group(2)).matches()) {
        return null;
      }
      return assignment;
    }
    return null;
  }

  private String buildSonarBlock(String sonarExclusions) {
    return "sonar {\n"
        + "    properties {\n"
        + "        property \"sonar.exclusions\", \""
        + sonarExclusions
        + "\"\n"
        + "    }\n"
        + "}\n";
  }

  private boolean upgradeGradleProperties(
      ModuleBuilder builder, String mainGradleContent, String targetVersion) throws IOException {
    return builder.updateFile(
        GRADLE_PROPERTIES,
        content -> {
          String updated =
              content.replaceAll(
                  "(?m)^systemProp\\.version=.*$", "systemProp.version=" + targetVersion);
          if (!updated.contains("javaVersion=")) {
            String javaVersion = extractJavaVersion(mainGradleContent);
            updated = updated.stripTrailing() + "\njavaVersion=" + javaVersion + "\n";
          }
          return updated;
        });
  }

  private String extractJavaVersion(String mainGradleContent) {
    if (mainGradleContent == null) {
      return "17";
    }
    Matcher matcher = JAVA_TOOLCHAIN_VERSION.matcher(mainGradleContent);
    return matcher.find() ? matcher.group(1) : "17";
  }

  @SuppressWarnings("unchecked")
  private boolean cleanChildModules(ModuleBuilder builder) throws IOException {
    List<String> gradleFiles = (List<String>) builder.getParam(FILES_TO_UPDATE);
    if (gradleFiles == null) {
      return false;
    }
    Path rootBuildFile = normalize(builder.resolveFile(BUILD_GRADLE));
    Path mainGradleFile = normalize(builder.resolveFile(MAIN_GRADLE));
    Path appServiceFile = normalize(builder.resolveFile(APP_BUILD_GRADLE));
    Set<String> infrastructureImplementationArtifacts =
        collectInfrastructureImplementationArtifacts(builder, gradleFiles);
    boolean applied = false;
    for (String file : gradleFiles) {
      Path current = normalize(builder.resolveFile(file));
      if (current.equals(rootBuildFile) || current.equals(mainGradleFile)) {
        continue;
      }
      boolean isAppService = current.equals(appServiceFile);
      boolean isInfrastructureModule = isUnderInfrastructure(file);
      applied |=
          builder.updateFile(
              file,
              content ->
                  cleanModuleContent(
                      content,
                      isAppService,
                      isInfrastructureModule,
                      infrastructureImplementationArtifacts));
    }
    return applied;
  }

  private Set<String> collectInfrastructureImplementationArtifacts(
      ModuleBuilder builder, List<String> gradleFiles) throws IOException {
    Set<String> groupArtifacts = new HashSet<>();
    for (String file : gradleFiles) {
      if (!isUnderInfrastructure(file)) {
        continue;
      }
      groupArtifacts.addAll(
          extractInfrastructureImplementationGroupArtifacts(builder.readFile(file)));
    }
    return groupArtifacts;
  }

  private Set<String> extractInfrastructureImplementationGroupArtifacts(String content) {
    Set<String> groupArtifacts = new HashSet<>();
    Matcher matcher = IMPLEMENTATION_COORDINATE.matcher(content);
    while (matcher.find()) {
      String coordinate = matcher.group(1);
      if (coordinate.contains(":")) {
        groupArtifacts.add(normalizeToGroupArtifact(coordinate));
      }
    }
    return groupArtifacts;
  }

  private String normalizeToGroupArtifact(String coordinate) {
    return coordinate.chars().filter(ch -> ch == ':').count() >= 2
        ? coordinate.substring(0, coordinate.lastIndexOf(':'))
        : coordinate;
  }

  private boolean isUnderInfrastructure(String file) {
    return file.replace('\\', '/').contains("/infrastructure/");
  }

  private Path normalize(File file) {
    return file.getAbsoluteFile().toPath().normalize();
  }

  /**
   * In app-service every module dependency is auto-wired, so all {@code project(':x')} lines are
   * removed. In infrastructure modules only {@code model}/{@code usecase} are auto-wired; a
   * dependency on another infrastructure module (e.g. {@code project(':rest-consumer')}) must stay.
   */
  private String cleanModuleContent(
      String content,
      boolean isAppService,
      boolean isInfrastructureModule,
      Set<String> infrastructureImplementationArtifacts) {
    String cleaned = removeResolutionStrategyConfigurationsAllBlocks(content);
    if (isAppService) {
      cleaned = removeProjectDependencies(cleaned, null);
      cleaned =
          removeInfrastructureImplementationDuplicates(
              cleaned, infrastructureImplementationArtifacts);
      cleaned = removePluginV5AppServiceDependencies(cleaned);
    } else if (isInfrastructureModule) {
      cleaned = removeProjectDependencies(cleaned, Set.of("model", "usecase"));
    }
    cleaned = Utils.removeLinesIncludes(cleaned, "'org.springframework:spring-context'");
    cleaned = Utils.removeLinesIncludes(cleaned, "com.tngtech.archunit:archunit");
    cleaned =
        Utils.removeLinesIncludes(
            cleaned, "testImplementation 'tools.jackson.core:jackson-databind'");
    if (isAppService) {
      String dependenciesBlock = UpdateUtils.extractBlock(cleaned, "dependencies");
      cleaned = dependenciesBlock != null ? dependenciesBlock + "\n" : cleaned;
    }
    return cleaned;
  }

  private String removeResolutionStrategyConfigurationsAllBlocks(String content) {
    int cursor = 0;
    StringBuilder result = new StringBuilder();
    while (cursor < content.length()) {
      int markerIndex = content.indexOf(CONFIGURATIONS_ALL, cursor);
      if (markerIndex == -1) {
        result.append(content.substring(cursor));
        break;
      }
      result.append(content.substring(cursor, markerIndex));
      String remainder = content.substring(markerIndex);
      String block = UpdateUtils.extractBlock(remainder, CONFIGURATIONS_ALL);
      if (block == null) {
        result.append(content.substring(markerIndex));
        break;
      }
      int blockEnd = markerIndex + block.length();
      if (shouldRemoveConfigurationsAllBlock(block)) {
        cursor = blockEnd;
        if (cursor < content.length() && content.charAt(cursor) == '\n') {
          cursor++;
        }
      } else {
        result.append(block);
        cursor = blockEnd;
      }
    }
    return result.toString();
  }

  private boolean shouldRemoveConfigurationsAllBlock(String block) {
    if (block.contains("exclude")) {
      return false;
    }
    int bodyStart = block.indexOf('{');
    int bodyEnd = block.lastIndexOf('}');
    if (bodyStart == -1 || bodyEnd <= bodyStart) {
      return false;
    }
    return block.substring(bodyStart + 1, bodyEnd).contains("resolutionStrategy");
  }

  private String removePluginV5AppServiceDependencies(String content) {
    return Arrays.stream(content.split("\n"))
        .filter(line -> !PLUGIN_V5_APP_SERVICE_DEPENDENCY.matcher(line).matches())
        .collect(Collectors.joining("\n"));
  }

  private String removeInfrastructureImplementationDuplicates(
      String content, Set<String> infrastructureImplementationArtifacts) {
    if (infrastructureImplementationArtifacts.isEmpty()) {
      return content;
    }
    return Arrays.stream(content.split("\n"))
        .filter(
            line ->
                !matchesInfrastructureImplementationDuplicate(
                    line, infrastructureImplementationArtifacts))
        .collect(Collectors.joining("\n"));
  }

  private boolean matchesInfrastructureImplementationDuplicate(
      String line, Set<String> infrastructureImplementationArtifacts) {
    Matcher matcher = IMPLEMENTATION_COORDINATE.matcher(line);
    if (!matcher.find()) {
      return false;
    }
    String coordinate = matcher.group(1);
    if (!coordinate.contains(":")) {
      return false;
    }
    return infrastructureImplementationArtifacts.contains(normalizeToGroupArtifact(coordinate));
  }

  /** Removes lines declaring {@code project(':module')}; if onlyModules is null, removes all. */
  private String removeProjectDependencies(String content, Set<String> onlyModules) {
    return Arrays.stream(content.split("\n"))
        .filter(
            line -> {
              Matcher matcher = PROJECT_DEPENDENCY.matcher(line);
              if (!matcher.find()) {
                return true;
              }
              return onlyModules != null && !onlyModules.contains(matcher.group(1));
            })
        .collect(Collectors.joining("\n"));
  }

  @Override
  public String name() {
    return "4.x.x->5.x.x";
  }

  @Override
  public String description() {
    return "Update scaffold 4.x.x to 5.x.x";
  }
}
