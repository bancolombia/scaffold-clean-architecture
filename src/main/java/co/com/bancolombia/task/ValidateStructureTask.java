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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.UnknownConfigurationException;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;

@CATask(
    name = "validateStructure",
    shortcut = "vs",
    description = "Validate that project references are not violated")
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class ValidateStructureTask
    extends AbstractCleanArchitectureDefaultTask { // NOSONAR
  private static final String MODEL_MODULE = "model";
  private static final String USE_CASE_MODULE = "usecase";
  private static final String REACTOR_CORE = "reactor-core";
  private static final String REACTOR_EXTRA = "reactor-extra";
  private static final String SPRING_DEPENDENCIES = "spring-boot-dependencies";
  private static final String AWS_BOM = "bom";
  private static final String DEPENDENCY = "--- Dependency: ";

  @Internal
  public abstract Property<String> getProjectPath();

  @Internal
  public abstract SetProperty<String> getModuleNames();

  @Internal
  public abstract Property<Boolean> getHasSpringWeb();

  @Internal
  public abstract MapProperty<String, Set> getModuleDependencies();

  @Input
  public abstract SetProperty<File> getProjectDirectories();

  @Input
  @Optional
  public abstract Property<String> getWhitelistedDependencies();

  public ValidateStructureTask() {
    getProjectPath().set(projectDir.getAbsolutePath());
    getModuleNames().set(getProject().getChildProjects().keySet());
    getHasSpringWeb().set(getProject().provider(this::checkForSpringWebDependency));
    getModuleDependencies().set(getProject().provider(this::collectModuleDependencies));
    getProjectDirectories().set(getProject().provider(this::collectAllProjectDirectories));
  }

  @Override
  protected void doExecute() throws IOException, CleanException {
    String packageName = FileUtils.readProperties(getProjectPath().get(), "package");
    logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
    getModuleNames().get().forEach(name -> logger.lifecycle("Submodules: " + name));
    logger.lifecycle("Project Package: {}", packageName);

    logger.lifecycle("has spring-web dependency to run validations: {}", getHasSpringWeb().get());
    builder.addParam("hasSpringWeb", getHasSpringWeb().get());

    ArchitectureValidation.inject(builder, getLogger(), getProjectDirectories().get());
    boolean isValidateModelLayer = validateModelLayer();
    boolean isValidateUseCaseLayer = validateUseCaseLayer();
    boolean isValidateInfrastructureLayer = validateInfrastructureLayer();

    if (!isValidateModelLayer) {
      throw new CleanException("Model module is invalid");
    }
    if (!isValidateUseCaseLayer) {
      throw new CleanException("Use case module is invalid");
    }
    if (!isValidateInfrastructureLayer) {
      throw new CleanException("Infrastructure layer is invalid");
    }
    logger.lifecycle("The project is valid");
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
          getProject()
              .getChildProjects()
              .get(APP_SERVICE)
              .getConfigurations()
              .getByName("testImplementation")
              .getDependencies()
              .stream()
              .anyMatch(d -> d.getName().equals("spring-web"));
    } catch (UnknownConfigurationException e) {
      logger.warn("configuration testImplementation not present");
    }
    return springWebDependencyPresent;
  }

  private Map<String, Set<String>> collectModuleDependencies() {
    return getProject().getChildProjects().entrySet().stream()
        .collect(
            Collectors.toMap(
                Map.Entry::getKey,
                entry -> {
                  try {
                    return entry
                        .getValue()
                        .getConfigurations()
                        .getByName(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME)
                        .getDependencies()
                        .stream()
                        .map(Dependency::getName)
                        .collect(Collectors.toSet());
                  } catch (UnknownConfigurationException e) {
                    return Set.of();
                  }
                }));
  }

  private boolean validateModelLayer() {
    if (validateExistingModule(MODEL_MODULE)) {
      logger.lifecycle("Validating Model Module");
      Set<String> dependencies = getModuleDependencies().get().get(MODEL_MODULE);
      if (dependencies != null) {
        dependencies.forEach(dep -> logger.lifecycle(DEPENDENCY + dep));
        return dependencies.stream()
            .noneMatch(
                dependency -> {
                  boolean isExcluded = filterExcludedDependencies(dependency);
                  if (isExcluded) {
                    logger.error("--- Dependency {} is not allowed in Model Layer", dependency);
                  }
                  return isExcluded;
                });
      }
    } else {
      logger.warn("Model module not found");
    }
    return true;
  }

  private boolean validateUseCaseLayer() {
    boolean isValid = true;
    if (validateExistingModule(USE_CASE_MODULE)) {
      logger.lifecycle("Validating Use Case Module");
      Set<String> dependencies = getModuleDependencies().get().get(USE_CASE_MODULE);
      if (dependencies != null) {
        dependencies.forEach(dep -> logger.lifecycle(DEPENDENCY + dep));
        Set<String> filteredDeps =
            dependencies.stream()
                .filter(this::filterExcludedDependencies)
                .collect(Collectors.toSet());

        if (filteredDeps.size() != 1) {
          logger.error(
              "--- Use Case Module contains no dependencies or more dependencies than allowed. "
                  + "Use Case Module should depend on the Model module ONLY.");
          isValid = false;
        } else {
          isValid = filteredDeps.iterator().next().contains(MODEL_MODULE);
          if (!isValid) {
            logger.error("--- Use Case Module only dependency should be the Model module.");
          }
        }
      }
    } else {
      logger.warn("Use case module not found");
    }
    return isValid;
  }

  private boolean filterExcludedDependencies(String dependencyName) {
    List<String> deps =
        Arrays.stream(this.getWhitelistedDependencies().getOrElse("").split(","))
            .map(String::trim)
            .filter(e -> !e.isEmpty())
            .collect(Collectors.toList());
    deps.addAll(Arrays.asList(REACTOR_EXTRA, REACTOR_CORE, SPRING_DEPENDENCIES, AWS_BOM));
    return !deps.contains(dependencyName);
  }

  private boolean validateInfrastructureLayer() {
    logger.lifecycle("Validating Infrastructure Layer");
    List<String> modulesExcludes = Arrays.asList(MODEL_MODULE, APP_SERVICE, USE_CASE_MODULE);
    AtomicBoolean valid = new AtomicBoolean(true);

    getModuleNames().get().stream()
        .filter(moduleName -> !modulesExcludes.contains(moduleName))
        .forEach(moduleName -> validateModule(valid, moduleName));
    return valid.get();
  }

  private void validateModule(AtomicBoolean valid, String moduleFiltered) {
    logger.lifecycle("Validating {} Module", moduleFiltered);
    Set<String> dependencies = getModuleDependencies().get().get(moduleFiltered);
    if (dependencies != null) {
      dependencies.forEach(dep -> logger.lifecycle(DEPENDENCY + dep));
      boolean hasInvalidDep =
          dependencies.stream()
              .anyMatch(
                  dependency -> {
                    boolean crossRefDep = APP_SERVICE.contains(dependency);
                    if (crossRefDep) {
                      logger.error(
                          "--- {} should not be listed as dependency in infrastructure layer modules",
                          dependency);
                    }
                    return crossRefDep
                        && !Arrays.asList(MODEL_MODULE, USE_CASE_MODULE).contains(dependency);
                  });
      if (hasInvalidDep) {
        valid.set(false);
        logger.error("--- {} is violating a rule", moduleFiltered);
      }
    }
  }

  private boolean validateExistingModule(String module) {
    return getModuleNames().get().contains(module);
  }
}
