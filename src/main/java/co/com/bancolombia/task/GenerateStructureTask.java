package co.com.bancolombia.task;

import co.com.bancolombia.Constants.BooleanOption;
import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.utils.Utils;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.options.OptionValues;

public class GenerateStructureTask extends CleanArchitectureDefaultTask {
  private String packageName = "co.com.bancolombia";
  private ProjectType type = ProjectType.IMPERATIVE;
  private CoveragePlugin coverage = CoveragePlugin.JACOCO;
  private String name = "cleanArchitecture";
  private BooleanOption lombok = BooleanOption.TRUE;
  private Language language = Language.JAVA;

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

  @Option(option = "lombok", description = "Switch the satus of lombok in this project")
  public void setStatusLombok(BooleanOption lombok) {
    this.lombok = lombok;
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

  @TaskAction
  public void generateStructureTask() throws IOException, CleanException {
    logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
    logger.lifecycle("Package: {}", packageName);
    logger.lifecycle("Project Type: {}", type);
    logger.lifecycle("Project Name: {}", name);
    builder.addParamPackage(packageName);
    builder.addParam("projectName", name);
    builder.addParam("reactive", type == ProjectType.REACTIVE);
    builder.addParam("jacoco", coverage == CoveragePlugin.JACOCO);
    builder.addParam("cobertura", coverage == CoveragePlugin.COBERTURA);
    builder.addParam("lombok", lombok == BooleanOption.TRUE);
    builder.addParam("language", language.name());
    if (language == Language.KOTLIN) {
      builder.setupFromTemplate("kotlin");
    } else if (lombok == BooleanOption.TRUE) {
      builder.setupFromTemplate("structure");
    } else {
      builder.setupFromTemplate("structure/without-lombok");
    }
    builder.persist();
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
}
