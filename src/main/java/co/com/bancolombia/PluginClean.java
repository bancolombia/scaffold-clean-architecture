package co.com.bancolombia;

import co.com.bancolombia.models.TaskModel;
import co.com.bancolombia.plugins.ChildConventionPlugin;
import co.com.bancolombia.plugins.DependencyOverrideConventions;
import co.com.bancolombia.plugins.RootModuleDependencyConventions;
import co.com.bancolombia.plugins.RootQualityConventions;
import co.com.bancolombia.task.ValidateStructureTask;
import co.com.bancolombia.task.annotations.CATask;
import co.com.bancolombia.utils.FileUtils;
import co.com.bancolombia.utils.ReflectionUtils;
import java.util.stream.Stream;
import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.UnknownPluginException;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.testing.Test;
import org.gradle.api.tasks.wrapper.Wrapper;
import org.jetbrains.annotations.NotNull;

public class PluginClean implements Plugin<Project> {
  private static final String SONAR_PLUGIN_ID = "org.sonarqube";
  private static final String JACOCO_PLUGIN_ID = "jacoco";
  private final RootModuleDependencyConventions rootModuleDependencyConventions =
      new RootModuleDependencyConventions();
  private final DependencyOverrideConventions dependencyOverrideConventions =
      new DependencyOverrideConventions();
  private final RootQualityConventions rootQualityConventions = new RootQualityConventions();

  public void apply(Project project) {
    boolean onlyUpdater = false;
    try {
      onlyUpdater = FileUtils.readBooleanProperty("onlyUpdater");
    } catch (Exception e) {
      project.getLogger().debug("Property onlyUpdater not found, using default value false", e);
    }
    if (onlyUpdater) {
      CleanPluginExtension cleanPluginExtension =
          project.getExtensions().create("cleanPlugin", CleanPluginExtension.class);
      TaskContainer taskContainer = project.getTasks();
      initTasks(cleanPluginExtension)
          .filter(t -> "it".equals(t.getShortcut()))
          .forEach(task -> this.appendTask(taskContainer, task));
      return;
    }
    if (project.file(Constants.MainFiles.MAIN_GRADLE).isFile()) {
      printLegacyProjectMigrationNotice(project);
      throw new GradleException(
          "Scaffold 5 cannot be applied to this Scaffold 4 project until it is migrated.");
    }

    project.getPluginManager().apply("java");
    project.getPluginManager().apply(JACOCO_PLUGIN_ID);
    applyOptionalPlugin(project, SONAR_PLUGIN_ID);

    CleanPluginExtension cleanPluginExtension =
        project.getExtensions().create("cleanPlugin", CleanPluginExtension.class);

    TaskContainer taskContainer = project.getTasks();
    initTasks(cleanPluginExtension).forEach(task -> this.appendTask(taskContainer, task));
    configureWrapper(project);
    applyChildConventions(project);
    rootModuleDependencyConventions.configure(project);
    rootQualityConventions.configure(project);
    dependencyOverrideConventions.configure(project);

    project.getSubprojects().forEach(this::listenTest);

    taskContainer
        .named("compileJava")
        .configure(task -> task.getDependsOn().add(taskContainer.named("validateStructure")));
  }

  private void printLegacyProjectMigrationNotice(Project project) {
    project.getLogger().lifecycle("+==========================================================+");
    project.getLogger().lifecycle("|                    MIGRATION REQUIRED                    |");
    project.getLogger().lifecycle("+==========================================================+");
    project.getLogger().lifecycle("You are using Scaffold 5 on a Scaffold 4 project");
    project
        .getLogger()
        .lifecycle(
            "You may follow the next migration guide "
                + "https://bancolombia.github.io/scaffold-clean-architecture/docs/migrations/v4-v5");
  }

  private void applyOptionalPlugin(Project project, String pluginId) {
    try {
      project.getPluginManager().apply(pluginId);
    } catch (UnknownPluginException exception) {
      project
          .getLogger()
          .debug("Skipping optional plugin {} because it is not available", pluginId);
    }
  }

  private void configureWrapper(Project project) {
    project
        .getTasks()
        .withType(Wrapper.class)
        .configureEach(wrapper -> wrapper.setGradleVersion(Constants.GRADLE_WRAPPER_VERSION));
  }

  private void applyChildConventions(Project project) {
    project
        .getSubprojects()
        .forEach(subproject -> subproject.getPluginManager().apply(ChildConventionPlugin.class));
  }

  private void listenTest(Project project) {
    project.getLogger().info("Injecting test logger");
    project
        .getTasks()
        .withType(Test.class)
        .configureEach(
            test ->
                test.addTestOutputListener(
                    (testDescriptor, testOutputEvent) -> {
                      if (!testOutputEvent.getMessage().contains("DEBUG")) {
                        test.getLogger().lifecycle(testOutputEvent.getMessage().replace('\n', ' '));
                      }
                    }));
  }

  private Stream<TaskModel> initTasks(CleanPluginExtension cleanPluginExtension) {
    return ReflectionUtils.getTasks()
        .map(
            clazz -> {
              TaskModel.TaskModelBuilder builder =
                  TaskModel.builder()
                      .name(clazz.getAnnotation(CATask.class).name())
                      .shortcut(clazz.getAnnotation(CATask.class).shortcut())
                      .description(clazz.getAnnotation(CATask.class).description())
                      .group(Constants.PLUGIN_TASK_GROUP)
                      .taskAction(clazz);
              if (clazz == ValidateStructureTask.class) {
                builder.action(buildValidateStructureTaskAction(cleanPluginExtension));
              }
              return builder.build();
            });
  }

  @NotNull
  private Action<? extends ValidateStructureTask> buildValidateStructureTaskAction(
      CleanPluginExtension cleanPluginExtension) {
    return task ->
        task.getWhitelistedDependencies()
            .set(cleanPluginExtension.getModelProps().getWhitelistedDependencies());
  }

  @SuppressWarnings("unchecked")
  private void appendTask(TaskContainer taskContainer, TaskModel t) {
    if (t.getAction() == null) {
      taskContainer.register(
          t.getName(),
          t.getTaskAction(),
          task -> {
            task.setGroup(t.getGroup());
            task.setDescription(t.getDescription());
          });
      taskContainer.register(
          t.getShortcut(),
          t.getTaskAction(),
          task -> {
            task.setGroup(t.getGroup());
            task.setDescription(t.getDescription());
          });
    } else {
      taskContainer.register(
          t.getName(),
          t.getTaskAction(),
          task -> {
            t.getAction().execute(task);
            task.setGroup(t.getGroup());
            task.setDescription(t.getDescription());
          });
      taskContainer.register(
          t.getShortcut(),
          t.getTaskAction(),
          task -> {
            t.getAction().execute(task);
            task.setGroup(t.getGroup());
            task.setDescription(t.getDescription());
          });
    }
  }
}
