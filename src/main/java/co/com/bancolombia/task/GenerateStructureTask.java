package co.com.bancolombia.task;

import static co.com.bancolombia.Constants.MainFiles.MAIN_GRADLE;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.task.annotations.CATask;
import co.com.bancolombia.utils.FileUtils;
import co.com.bancolombia.utils.Utils;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.options.OptionValues;

@CATask(
    name = "cleanArchitecture",
    shortcut = "ca",
    description = "Scaffolding clean architecture project")
public class GenerateStructureTask extends AbstractCleanArchitectureDefaultTask {
  private static final String REACTIVE = "reactive";
  private String packageName = "co.com.bancolombia";
  private ProjectType type = ProjectType.REACTIVE;
  private String name = "cleanArchitecture";
  private BooleanOption lombok = BooleanOption.TRUE;
  private BooleanOption metrics = BooleanOption.TRUE;
  private BooleanOption mutation = BooleanOption.TRUE;
  private BooleanOption force = BooleanOption.FALSE;
  private BooleanOption withExample = BooleanOption.FALSE;
  private JavaVersion javaVersion = JavaVersion.VERSION_21;

  public GenerateStructureTask() {
    notCompatibleWithConfigurationCache("This task performs validations that should always run");
  }

  @Option(option = "package", description = "Set principal package to use in the project")
  public void setPackage(String packageName) {
    this.packageName = packageName;
  }

  @Option(option = "type", description = "Set project type")
  public void setType(ProjectType type) {
    this.type = type;
  }

  @Option(option = "name", description = "Set project name, by default is cleanArchitecture ")
  public void setName(String projectName) {
    this.name = projectName;
  }

  @Option(option = "lombok", description = "Switch the status of lombok in this project")
  public void setStatusLombok(BooleanOption lombok) {
    this.lombok = lombok;
  }

  @Option(option = "metrics", description = "Set if metrics will be enabled in this project")
  public void setMetrics(BooleanOption metrics) {
    this.metrics = metrics;
  }

  @Option(
      option = "mutation",
      description = "Set if this project should include mutation tests configuration")
  public void setMutation(BooleanOption mutation) {
    this.mutation = mutation;
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

  @Override
  public void execute() throws IOException, CleanException {
    logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
    logger.lifecycle("Package: {}", packageName);
    logger.lifecycle("Project Type: {}", type);
    logger.lifecycle("Java Version: {}", javaVersion);
    logger.lifecycle("Project Name: {}", name);
    builder.addParamPackage(packageName);
    builder.addParam("projectName", name);
    builder.addParam(REACTIVE, type == ProjectType.REACTIVE);
    builder.addParam("lombok", lombok == BooleanOption.TRUE);
    builder.addParam("metrics", metrics == BooleanOption.TRUE);
    builder.addParam("example", withExample == BooleanOption.TRUE);
    builder.addParam("mutation", mutation == BooleanOption.TRUE);
    builder.addParam("javaVersion", javaVersion);
    builder.addParam("java17", javaVersion == JavaVersion.VERSION_17);
    builder.addParam("java21", javaVersion == JavaVersion.VERSION_21);

    boolean exists = FileUtils.exists(builder.getProject().getProjectDir().getPath(), MAIN_GRADLE);
    if (exists && force == BooleanOption.FALSE) {
      logger.lifecycle(
          "Existing project detected, regenerating main.gradle, build.gradle and gradle.properties");
      loadProperty("package");
      loadProperty("language");
      builder.addParam(REACTIVE, builder.isReactive());
      builder.addParam("lombok", builder.isEnableLombok());
      builder.addParam("metrics", builder.withMetrics());
      builder.addParam("mutation", builder.withMutation());
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
    builder.runTask("wrapper");
  }

  @Override
  protected Optional<String> resolveAnalyticsType() {
    return Optional.of(builder.getBooleanParam(REACTIVE) ? REACTIVE : "imperative");
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

  public enum JavaVersion {
    VERSION_17,
    VERSION_21
  }
}
