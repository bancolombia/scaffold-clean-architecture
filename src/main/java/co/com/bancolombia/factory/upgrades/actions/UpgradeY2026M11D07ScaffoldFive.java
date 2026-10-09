package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.APP_BUILD_GRADLE;
import static co.com.bancolombia.Constants.MainFiles.BUILD_GRADLE;
import static co.com.bancolombia.Constants.MainFiles.GRADLE_PROPERTIES;
import static co.com.bancolombia.Constants.MainFiles.MAIN_GRADLE;
import static co.com.bancolombia.Constants.MainFiles.SETTINGS_GRADLE;
import static co.com.bancolombia.factory.upgrades.actions.UpdateDependencies.FILES_TO_UPDATE;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpdateUtils;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import co.com.bancolombia.models.Release;
import co.com.bancolombia.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
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
          id 'info.solidsoft.pitest'
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
    return applied;
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
          result.append(NEW_PLUGINS_BLOCK).append("\n\n");
          result.append(buildAllProjectsBlock(mainGradleContent));
          if (sonarExclusions != null) {
            result.append("\n").append(buildSonarBlock(sonarExclusions));
          }
          return result.toString();
        });
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

  private String buildAllProjectsBlock(String mainGradleContent) {
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
    if (bomLine != null) {
      sb.append("\n    dependencies {\n        ").append(bomLine).append("\n    }\n");
    }
    sb.append("}\n");
    return sb.toString();
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
              file, content -> cleanModuleContent(content, isAppService, isInfrastructureModule));
    }
    return applied;
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
      String content, boolean isAppService, boolean isInfrastructureModule) {
    String cleaned = content;
    if (isAppService) {
      cleaned = removeProjectDependencies(cleaned, null);
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
    return "4.x.x->5.0.0";
  }

  @Override
  public String description() {
    return "Update scaffold 4.x.x to 5.0.0";
  }
}
