package co.com.bancolombia.task;

import static co.com.bancolombia.Constants.APP_SERVICE;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.validations.architecture.ArchitectureValidation;
import co.com.bancolombia.task.annotations.CATask;
import co.com.bancolombia.utils.FileUtils;
import co.com.bancolombia.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.Getter;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.artifacts.UnknownConfigurationException;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;

@CATask(
    name = "validateStructure",
    shortcut = "vs",
    description = "Validate that project references are not violated")
public abstract class ValidateStructureTask extends AbstractCleanArchitectureDefaultTask {
  private static final String MODEL_MODULE = "model";
  private static final String USE_CASE_MODULE = "usecase";
  private static final String REACTOR_CORE = "reactor-core";
  private static final String REACTOR_EXTRA = "reactor-extra";
  private static final String SPRING_DEPENDENCIES = "spring-boot-dependencies";
  private static final String AWS_BOM = "bom";

  @Input @Getter private final Property<String> projectPath;
  @Input @Getter private final SetProperty<String> moduleNames;
  @Input @Getter private final Property<Boolean> hasSpringWeb;
  @Input @Getter private final SetProperty<File> projectDirectories;
  @Input @Getter private final Property<Boolean> isValidateModelLayer;
  @Input @Getter private final Property<Boolean> isValidateUseCaseLayer;
  @Input @Getter private final Property<Boolean> isValidateInfrastructureLayer;

  @Input
  @Optional
  public abstract Property<String> getWhitelistedDependencies();

  @Inject
  public ValidateStructureTask() throws IOException {
    notCompatibleWithConfigurationCache("This task performs validations that should always run");
    this.projectPath = getProject().getObjects().property(String.class);
    this.projectPath.set(getProject().getProjectDir().getPath());

    this.moduleNames = getProject().getObjects().setProperty(String.class);
    this.moduleNames.set(getProject().getChildProjects().keySet());

    this.projectDirectories = getProject().getObjects().setProperty(File.class);
    this.projectDirectories.set(getProject().provider(this::collectAllProjectDirectories));

    setupArchitectureValidation();

    this.hasSpringWeb = getProject().getObjects().property(Boolean.class);
    this.hasSpringWeb.set(getProject().provider(this::checkForSpringWebDependency));

    this.isValidateModelLayer = getProject().getObjects().property(Boolean.class);
    this.isValidateModelLayer.set(getProject().provider(this::validateModelLayer));

    this.isValidateUseCaseLayer = getProject().getObjects().property(Boolean.class);
    this.isValidateUseCaseLayer.set(getProject().provider(this::validateUseCaseLayer));

    this.isValidateInfrastructureLayer = getProject().getObjects().property(Boolean.class);
    this.isValidateInfrastructureLayer.set(
        getProject().provider(this::validateInfrastructureLayer));
  }

  @Override
  public void execute() throws IOException, CleanException {
    if (!isValidateModelLayer.get()) {
      throw new CleanException("Model module is invalid");
    }
    if (!isValidateUseCaseLayer.get()) {
      throw new CleanException("Use case module is invalid");
    }
    if (!isValidateInfrastructureLayer.get()) {
      throw new CleanException("Infrastructure layer is invalid");
    }
    logger.lifecycle("The project is valid");
  }

  private void setupArchitectureValidation() throws IOException {
    String packageName = FileUtils.readProperties(projectPath.get(), "package");
    logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
    moduleNames.get().forEach(name -> logger.lifecycle("Submodules: " + name));
    logger.lifecycle("Project Package: {}", packageName);
    ArchitectureValidation.inject(builder, getLogger(), projectDirectories.get());
  }

  private Set<File> collectAllProjectDirectories() {
    return getProject().getAllprojects().stream()
        .map(Project::getProjectDir)
        .collect(Collectors.toSet());
  }

  private boolean checkForSpringWebDependency() {
    boolean springWebDependencyPresent = false;
    try {
      springWebDependencyPresent =
          getProject().getChildProjects().get(APP_SERVICE).getConfigurations()
              .getByName("testImplementation").getDependencies().stream()
              .anyMatch(d -> d.getName().equals("spring-web"));
    } catch (UnknownConfigurationException e) {
      logger.warn("configuration testImplementation not present");
    }

    logger.lifecycle(
        "has spring-web dependency to run validations: {}", springWebDependencyPresent);
    builder.addParam("hasSpringWeb", springWebDependencyPresent);
    return springWebDependencyPresent;
  }

  private boolean validateModelLayer() {
    if (validateExistingModule(MODEL_MODULE)) {
      logger.lifecycle("Validating Model Module");
      Configuration configuration = getConfiguration(MODEL_MODULE);
      return configuration.getAllDependencies().stream()
          .noneMatch(
              dependency -> {
                boolean isExcluded = filterExcludedDependencies(dependency);
                if (isExcluded) {
                  logger.error(
                      "--- Dependency {}:{} is not allowed in Model Layer",
                      dependency.getGroup(),
                      dependency.getName());
                }
                return isExcluded;
              });
    } else {
      logger.warn("Model module not found");
    }
    return true;
  }

  private boolean validateUseCaseLayer() {
    boolean isValid = true;
    if (validateExistingModule(USE_CASE_MODULE)) {
      logger.lifecycle("Validating Use Case Module");
      Configuration configuration = getConfiguration(USE_CASE_MODULE);
      DependencySet dependencies = configuration.getAllDependencies();
      if (dependencies.stream().filter(this::filterExcludedDependencies).count() != 1) {
        logger.error(
            "--- Use Case Module contains no dependencies or more dependencies than allowed. "
                + "Use Case Module should depend on the Model module ONLY.");
        isValid = false;
      } else {
        isValid =
            dependencies.stream()
                .filter(this::filterExcludedDependencies)
                .iterator()
                .next()
                .getName()
                .contains((MODEL_MODULE));
        if (!isValid) {
          logger.error("--- Use Case Module only dependency should be the Model module.");
        }
      }
    } else {
      logger.warn("Use case module not found");
    }
    return isValid;
  }

  private boolean filterExcludedDependencies(Dependency dependency) {
    List<String> deps =
        Arrays.stream(this.getWhitelistedDependencies().getOrElse("").split(","))
            .map(String::trim)
            .filter(e -> !e.isEmpty())
            .collect(Collectors.toList());
    deps.addAll(Arrays.asList(REACTOR_EXTRA, REACTOR_CORE, SPRING_DEPENDENCIES, AWS_BOM));
    return !deps.contains(dependency.getName());
  }

  private boolean validateInfrastructureLayer() {
    logger.lifecycle("Validating Infrastructure Layer");
    List<String> modulesExcludes = Arrays.asList(MODEL_MODULE, APP_SERVICE, USE_CASE_MODULE);
    AtomicBoolean valid = new AtomicBoolean(true);

    moduleNames.get().stream()
        .filter(moduleName -> !modulesExcludes.contains(moduleName))
        .forEach(moduleName -> validateModule(valid, moduleName));
    return valid.get();
  }

  private void validateModule(AtomicBoolean valid, String moduleFiltered) {
    logger.lifecycle("Validating {} Module", moduleFiltered);
    validateDependencies(valid, moduleFiltered);
    if (!valid.get()) {
      logger.error("--- {} is violating a rule", moduleFiltered);
    }
  }

  private boolean validateExistingModule(String module) {
    return moduleNames.get().contains(module);
  }

  private Configuration getConfiguration(String moduleName) {
    printDependenciesByModule(moduleName);

    return getProject()
        .getChildProjects()
        .get(moduleName)
        .getConfigurations()
        .getByName(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME);
  }

  private void printDependenciesByModule(String moduleName) {
    getProject()
        .getChildProjects()
        .get(moduleName)
        .getConfigurations()
        .getByName(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME)
        .getDependencies()
        .forEach(dependency -> logger.lifecycle("--- Dependency: " + dependency.getName()));
  }

  private void validateDependencies(AtomicBoolean valid, String dependency) {
    Configuration configuration = getConfiguration(dependency);
    if (configuration.getDependencies().stream().anyMatch(filterDependenciesInfrastructure())) {
      valid.set(false);
    }
  }

  private Predicate<Dependency> filterDependenciesInfrastructure() {
    return dependency -> {
      boolean crossRefDep = APP_SERVICE.contains(dependency.getName());
      if (crossRefDep) {
        logger.error(
            "--- {} should not be listed as dependency in infrastructure layer modules",
            dependency.getName());
      }
      return crossRefDep
          && !Arrays.asList(MODEL_MODULE, USE_CASE_MODULE).contains(dependency.getName());
    };
  }
}
