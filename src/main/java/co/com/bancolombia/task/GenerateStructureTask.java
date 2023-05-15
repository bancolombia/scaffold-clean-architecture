package co.com.bancolombia.task;

import static co.com.bancolombia.Constants.MainFiles.MAIN_GRADLE;

import co.com.bancolombia.Constants.BooleanOption;
import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.utils.FileUtils;
import co.com.bancolombia.utils.Utils;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.options.OptionValues;

public class GenerateStructureTask extends CleanArchitectureDefaultTask {
  private static final String REACTIVE = "reactive";
  private String packageName = "co.com.bancolombia";
  private ProjectType type = ProjectType.REACTIVE;
  private CoveragePlugin coverage = CoveragePlugin.JACOCO;
  private String name = "cleanArchitecture";
  private BooleanOption lombok = BooleanOption.TRUE;
  private BooleanOption metrics = BooleanOption.TRUE;
  private BooleanOption force = BooleanOption.FALSE;
  private BooleanOption withExample = BooleanOption.FALSE;
  private Language language = Language.JAVA;
  private JavaVersion javaVersion = JavaVersion.VERSION_17;

  @Option(option = "package", description = "Set principal package to use in the project")
  public void setPackage(String packageName) {
    this.packageName = packageName;
  }

  @Option(option = "type", description = "Set project type")
  public void setType(ProjectType type) {
    this.type = type;
  }

  @Option(option = "language", description = "Set project lang")
  public void setLanguage(Language language) {
    this.language = language;
  }

  @Option(option = "name", description = "Set project name, by default is cleanArchitecture ")
  public void setName(String projectName) {
    this.name = projectName;
  }

  @Option(option = "coverage", description = "Set project coverage plugin")
  public void setCoveragePlugin(CoveragePlugin coverage) {
    this.coverage = coverage;
  }

  @Option(option = "lombok", description = "Switch the status of lombok in this project")
  public void setStatusLombok(BooleanOption lombok) {
    this.lombok = lombok;
  }

  @Option(option = "metrics", description = "Set if metrics will be enabled in this project")
  public void setMetrics(BooleanOption metrics) {
    this.metrics = metrics;
  }

  @Option(option = "javaVersion", description = "Set Java version")
  public void setJavaVersion(JavaVersion javaVersion) {
    this.javaVersion = javaVersion;
  }

  @Option(option = "force", description = "Force regenerates all files")
  public void setForce(BooleanOption force) {
    this.force = force;
  }

  @Option(option = "example", description = "Generate locally for example")
  public void setWithExample(BooleanOption withExample) {
    this.withExample = withExample;
  }

  @OptionValues("type")
  public List<ProjectType> getAvailableProjectTypes() {
    return Arrays.asList(ProjectType.values());
  }

  @OptionValues("coverage")
  public List<CoveragePlugin> getCoveragePlugins() {
    return Arrays.asList(CoveragePlugin.values());
  }

  @OptionValues("lombok")
  public List<BooleanOption> getLombokOptions() {
    return Arrays.asList(BooleanOption.values());
  }

  @OptionValues("metrics")
  public List<BooleanOption> getMetricsOptions() {
    return Arrays.asList(BooleanOption.values());
  }

  @OptionValues("force")
  public List<BooleanOption> getForceOptions() {
    return Arrays.asList(BooleanOption.values());
  }

  @OptionValues("javaVersion")
  public List<JavaVersion> getJavaVersions() {
    return Arrays.asList(JavaVersion.values());
  }

  @TaskAction
  public void generateStructureTask() throws IOException, CleanException {
    long start = System.currentTimeMillis();
    logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
    logger.lifecycle("Package: {}", packageName);
    logger.lifecycle("Project Type: {}", type);
    logger.lifecycle("Java Version: {}", javaVersion);
    logger.lifecycle("Project Name: {}", name);
    builder.addParamPackage(packageName);
    builder.addParam("projectName", name);
    builder.addParam(REACTIVE, type == ProjectType.REACTIVE);
    builder.addParam("jacoco", coverage == CoveragePlugin.JACOCO);
    builder.addParam("cobertura", coverage == CoveragePlugin.COBERTURA);
    builder.addParam("lombok", lombok == BooleanOption.TRUE);
    builder.addParam("metrics", metrics == BooleanOption.TRUE);
    builder.addParam("example", withExample == BooleanOption.TRUE);
    builder.addParam("language", language.name().toLowerCase());
    builder.addParam("javaVersion", javaVersion);
    builder.addParam("java8", javaVersion == JavaVersion.VERSION_1_8);
    builder.addParam("java11", javaVersion == JavaVersion.VERSION_11);
    builder.addParam("java17", javaVersion == JavaVersion.VERSION_17);

    boolean exists = FileUtils.exists(builder.getProject().getProjectDir().getPath(), MAIN_GRADLE);
    if (exists && force == BooleanOption.FALSE) {
      logger.lifecycle(
          "Existing project detected, regenerating main.gradle, build.gradle and gradle.properties");
      loadProperty("package");
      loadProperty("language");
      builder.addParam(REACTIVE, builder.isReactive());
      builder.addParam("lombok", builder.isEnableLombok());
      builder.addParam("metrics", builder.withMetrics());
      if (builder.isEnableLombok()) {
        builder.setupFromTemplate("structure/restructure");
      } else {
        builder.setupFromTemplate("structure/restructure/without-lombok");
      }
    } else {
      if (lombok == BooleanOption.TRUE) {
        builder.setupFromTemplate("structure");
      } else {
        builder.setupFromTemplate("structure/without-lombok");
      }
    }

    builder.persist();
    sendAnalytics(
        builder.getBooleanParam(REACTIVE) ? REACTIVE : "imperative",
        System.currentTimeMillis() - start);
  }

  private void loadProperty(String property) {
    try {
      String propertyValue = FileUtils.readProperties(".", property);
      if (propertyValue != null && !propertyValue.isEmpty()) {
        builder.addParam(property, propertyValue);
      }
    } catch (IOException ignored) {
      logger.debug("Error reading property {} from gradle.properties", property);
    }
  }

  public enum ProjectType {
    REACTIVE,
    IMPERATIVE
  }

  public enum CoveragePlugin {
    JACOCO,
    COBERTURA
  }

  public enum Language {
    JAVA,
    KOTLIN
  }

  public enum JavaVersion {
    @Deprecated
    VERSION_1_8,
    VERSION_11,
    VERSION_17
  }
}
