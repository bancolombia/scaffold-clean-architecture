package co.com.bancolombia.plugins;

import co.com.bancolombia.Constants;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ProjectDependency;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.Jar;

public class RootModuleDependencyConventions {
  private static final String IMPLEMENTATION_CONFIGURATION = "implementation";
  private static final String TEST_IMPLEMENTATION_CONFIGURATION = "testImplementation";
  private static final String MODEL_PATH = ":model";
  private static final String USECASE_PATH = ":usecase";
  private static final String EXCLUDE_MODULE_FROM_APP_PROPERTY = "excludeModuleFromApp";
  private static final String ARCH_UNIT_GROUP = "com.tngtech.archunit";
  private static final String ARCH_UNIT_ARTIFACT = "archunit";
  private static final String SPRING_BOOT_PLUGIN_ID = "org.springframework.boot";

  public void configure(Project rootProject) {
    Set<String> excludedFromApp = resolveExcludedModulesFromApp(rootProject);
    rootProject
        .getSubprojects()
        .forEach(subproject -> applyDefaultDependencies(subproject, rootProject, excludedFromApp));
  }

  private Set<String> resolveExcludedModulesFromApp(Project rootProject) {
    Object rawValue = rootProject.findProperty(EXCLUDE_MODULE_FROM_APP_PROPERTY);
    if (rawValue == null) {
      return Set.of();
    }
    return Arrays.stream(String.valueOf(rawValue).split(","))
        .map(String::trim)
        .filter(module -> !module.isEmpty())
        .collect(Collectors.toSet());
  }

  private void applyDefaultDependencies(
      Project subproject, Project rootProject, Set<String> excludedFromApp) {
    String moduleType = resolveModuleType(subproject);
    switch (moduleType) {
      case "APP_SERVICE":
        configureAppServiceDependencies(subproject);
        injectAllModulesIntoAppService(subproject, rootProject, excludedFromApp);
        break;
      case "ENTRY_POINT":
        addSpringContextDependency(subproject);
        addProjectDependency(subproject, MODEL_PATH);
        addProjectDependency(subproject, USECASE_PATH);
        break;
      case "USECASE":
        addProjectDependency(subproject, MODEL_PATH);
        break;
      case "DRIVEN_ADAPTER", "HELPER":
        addSpringContextDependency(subproject);
        addProjectDependency(subproject, MODEL_PATH);
        break;
      default:
        break;
    }
  }

  private void addSpringContextDependency(Project subproject) {
    addDependencyIfMissing(
        subproject,
        IMPLEMENTATION_CONFIGURATION,
        "org.springframework",
        "spring-context",
        "org.springframework:spring-context");
  }

  private void configureAppServiceDependencies(Project appService) {
    appService.afterEvaluate(
        ignored -> {
          if (!appService.getPluginManager().hasPlugin(SPRING_BOOT_PLUGIN_ID)) {
            appService.getPluginManager().apply(SPRING_BOOT_PLUGIN_ID);
          }
          configureAppServicePackaging(appService);
          addDependencyIfMissing(
              appService,
              IMPLEMENTATION_CONFIGURATION,
              SPRING_BOOT_PLUGIN_ID,
              "spring-boot-starter",
              "org.springframework.boot:spring-boot-starter");
          addDependencyIfMissing(
              appService,
              "runtimeOnly",
              SPRING_BOOT_PLUGIN_ID,
              "spring-boot-devtools",
              "org.springframework.boot:spring-boot-devtools");
          addDependencyIfMissing(
              appService,
              TEST_IMPLEMENTATION_CONFIGURATION,
              "tools.jackson.core",
              "jackson-databind",
              "tools.jackson.core:jackson-databind");
          addArchUnitDependencyIfMissing(appService);
        });
  }

  private void configureAppServicePackaging(Project appService) {
    TaskProvider<Jar> jarTask = appService.getTasks().named("jar", Jar.class);
    jarTask.configure(task -> task.setEnabled(false));

    appService
        .getTasks()
        .register(
            "explodedJar",
            Copy.class,
            task -> {
              task.with(jarTask.get());
              task.into(appService.getLayout().getBuildDirectory().dir("exploded"));
            });

    appService
        .getTasks()
        .named("bootJar", Jar.class)
        .configure(
            task ->
                task.getArchiveFileName()
                    .set(
                        appService.getParent().getName() + "." + task.getArchiveExtension().get()));
  }

  private void addDependencyIfMissing(
      Project project, String configurationName, String group, String name, String notation) {
    Configuration configuration = project.getConfigurations().findByName(configurationName);
    if (configuration == null || !containsDependency(configuration, group, name)) {
      project.getDependencies().add(configurationName, notation);
    }
  }

  private void addArchUnitDependencyIfMissing(Project appService) {
    boolean alreadyDeclared =
        appService.getConfigurations().stream()
            .anyMatch(
                configuration ->
                    containsDependency(configuration, ARCH_UNIT_GROUP, ARCH_UNIT_ARTIFACT));
    if (!alreadyDeclared) {
      appService
          .getDependencies()
          .add(
              TEST_IMPLEMENTATION_CONFIGURATION,
              ARCH_UNIT_GROUP + ":" + ARCH_UNIT_ARTIFACT + ":" + Constants.ARCH_UNIT_VERSION);
    }
  }

  private boolean containsDependency(Configuration configuration, String group, String name) {
    return configuration.getDependencies().stream()
        .anyMatch(
            dependency -> group.equals(dependency.getGroup()) && name.equals(dependency.getName()));
  }

  private void injectAllModulesIntoAppService(
      Project appService, Project rootProject, Set<String> excludedFromApp) {
    rootProject.getSubprojects().stream()
        .filter(candidate -> !candidate.equals(appService))
        .filter(candidate -> !excludedFromApp.contains(candidate.getName()))
        .forEach(
            candidate -> {
              addProjectDependency(appService, candidate.getPath());
              if (isInfrastructureModule(candidate)) {
                candidate.afterEvaluate(
                    ignored -> mirrorImplementationDependencies(candidate, appService));
              }
            });
  }

  private boolean isInfrastructureModule(Project project) {
    String normalizedPath = project.getProjectDir().getPath().replace('\\', '/');
    return normalizedPath.contains("/infrastructure/");
  }

  private void mirrorImplementationDependencies(Project source, Project appService) {
    Configuration sourceImplementation =
        source.getConfigurations().findByName(IMPLEMENTATION_CONFIGURATION);
    Configuration appServiceImplementation =
        appService.getConfigurations().findByName(IMPLEMENTATION_CONFIGURATION);
    if (sourceImplementation == null || appServiceImplementation == null) {
      return;
    }

    for (Dependency dependency : sourceImplementation.getDependencies()) {
      if (!appServiceImplementation.getDependencies().contains(dependency)) {
        appService.getDependencies().add(IMPLEMENTATION_CONFIGURATION, dependency.copy());
      }
    }
  }

  private String resolveModuleType(Project subproject) {
    String normalizedPath = subproject.getProjectDir().getPath().replace('\\', '/');
    if (normalizedPath.contains("/applications/app-service")
        || "app-service".equals(subproject.getName())) {
      return "APP_SERVICE";
    }
    if (normalizedPath.contains("/domain/usecase") || "usecase".equals(subproject.getName())) {
      return "USECASE";
    }
    if (normalizedPath.contains("/infrastructure/entry-points/")) {
      return "ENTRY_POINT";
    }
    if (normalizedPath.contains("/infrastructure/driven-adapters/")) {
      return "DRIVEN_ADAPTER";
    }
    if (normalizedPath.contains("/infrastructure/helpers/")) {
      return "HELPER";
    }
    return "OTHER";
  }

  private void addProjectDependency(Project subproject, String targetProjectPath) {
    if (!targetProjectExists(subproject, targetProjectPath)
        || subproject.getPath().equals(targetProjectPath)
        || hasProjectDependency(subproject, targetProjectPath)) {
      return;
    }
    subproject
        .getDependencies()
        .add(
            IMPLEMENTATION_CONFIGURATION,
            subproject.getDependencies().project(Map.of("path", targetProjectPath)));
  }

  private boolean targetProjectExists(Project subproject, String targetProjectPath) {
    return subproject.getRootProject().findProject(targetProjectPath) != null;
  }

  private boolean hasProjectDependency(Project subproject, String targetProjectPath) {
    Configuration implementationConfiguration =
        subproject.getConfigurations().findByName(IMPLEMENTATION_CONFIGURATION);
    if (implementationConfiguration == null) {
      return false;
    }
    return implementationConfiguration.getDependencies().stream()
        .filter(ProjectDependency.class::isInstance)
        .map(ProjectDependency.class::cast)
        .anyMatch(projectDependency -> projectDependency.getPath().equals(targetProjectPath));
  }
}
