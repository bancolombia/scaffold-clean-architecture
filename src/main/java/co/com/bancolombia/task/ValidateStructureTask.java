package co.com.bancolombia.task;

import static co.com.bancolombia.Constants.APP_SERVICE;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.task.annotations.CATask;
import co.com.bancolombia.utils.FileUtils;
import co.com.bancolombia.utils.Utils;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.logging.Logger;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;

@CATask(
    name = "validateStructure",
    shortcut = "vs",
    description = "Validate that project references are not violated")
public abstract class ValidateStructureTask extends DefaultTask {
  private final Logger logger = getProject().getLogger();
  private static final String MODEL_MODULE = "model";
  private static final String USE_CASE_MODULE = "usecase";
  private static final String REACTOR_CORE = "reactor-core";
  private static final String REACTOR_EXTRA = "reactor-extra";
  private static final String SPRING_DEPENDENCIES = "spring-boot-dependencies";
  private static final String AWS_BOM = "bom";

  //    @Inject
  //    public ValidateStructureTask(Object some) {
  //        if (some instanceof Lambda) {
  //            getLogger().lifecycle("Lambda");
  //        }
  //        getLogger().lifecycle("VALIDATE STRUCTUREEEEEE");
  //        getLogger().lifecycle(some.getClass().toString());
  //        getLogger().lifecycle(this.getWhitelistedDependencies().getOrElse("Notthing"));
  //    }

  @Input
  @Optional
  //    @Inject
  public abstract Property<String> getWhitelistedDependencies();

  @TaskAction
  public void validateStructureTask() throws IOException, CleanException {
    String packageName =
        FileUtils.readProperties(getProject().getProjectDir().getPath(), "package");
    logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
    getModules().forEach(d -> logger.lifecycle("Submodules: " + d.getKey()));
    logger.lifecycle("Project Package: {}", packageName);

    if (!validateModelLayer()) {
      throw new CleanException("Model module is invalid");
    }
    if (!validateUseCaseLayer()) {
      throw new CleanException("Use case module is invalid");
    }
    if (!validateInfrastructureLayer()) {
      throw new CleanException("Infrastructure layer is invalid");
    }
    logger.lifecycle("The project is valid");
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
    Set<Map.Entry<String, Project>> modules = getModules();

    modules.stream()
        .filter(module -> !modulesExcludes.contains(module.getKey()))
        .forEach(moduleFiltered -> validateModule(valid, moduleFiltered));

    return valid.get();
  }

  private void validateModule(AtomicBoolean valid, Map.Entry<String, Project> moduleFiltered) {
    logger.lifecycle("Validating {} Module", moduleFiltered.getKey());
    validateDependencies(valid, moduleFiltered);
    if (!valid.get()) {
      logger.error("--- {} is violating a rule", moduleFiltered.getKey());
    }
  }

  private boolean validateExistingModule(String module) {
    return (getProject().getChildProjects().containsKey(module));
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

  private void validateDependencies(AtomicBoolean valid, Map.Entry<String, Project> dependency) {
    Configuration configuration = getConfiguration(dependency.getKey());
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

  private Set<Map.Entry<String, Project>> getModules() {
    return getProject().getChildProjects().entrySet();
  }
}
