package co.com.bancolombia;

import co.com.bancolombia.models.TaskModel;
import co.com.bancolombia.task.ValidateStructureTask;
import co.com.bancolombia.task.annotations.CATask;
import co.com.bancolombia.utils.FileUtils;
import co.com.bancolombia.utils.ReflectionUtils;
import java.util.stream.Stream;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.testing.Test;
import org.jetbrains.annotations.NotNull;

public class PluginClean implements Plugin<Project> {
  private CleanPluginExtension cleanPluginExtension;

  public void apply(Project project) {
    boolean onlyUpdater = false;
    try {
      onlyUpdater = FileUtils.readBooleanProperty("onlyUpdater");
    } catch (Exception e) {
      project.getLogger().debug("Property onlyUpdater not found, using default value false", e);
    }
    if (onlyUpdater) {
      TaskContainer taskContainer = project.getTasks();
      initTasks()
          .filter(t -> "it".equals(t.getShortcut()))
          .forEach(task -> this.appendTask(taskContainer, task));
      return;
    }
    project.getPluginManager().apply("java");
    cleanPluginExtension =
        project.getExtensions().create("cleanPlugin", CleanPluginExtension.class);

    TaskContainer taskContainer = project.getTasks();
    initTasks().forEach(task -> this.appendTask(taskContainer, task));

    project.getSubprojects().forEach(this::listenTest);

    taskContainer
        .getByName("compileJava")
        .getDependsOn()
        .add(taskContainer.getByName("validateStructure"));
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

  private Stream<TaskModel> initTasks() {
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
                builder.action(buildValidateStructureTaskAction());
              }
              return builder.build();
            });
  }

  @NotNull
  private Action<? extends ValidateStructureTask> buildValidateStructureTaskAction() {
    return task ->
        task.getWhitelistedDependencies()
            .set(cleanPluginExtension.getModelProps().getWhitelistedDependencies());
  }

  @SuppressWarnings("unchecked")
  private void appendTask(TaskContainer taskContainer, TaskModel t) {
    if (t.getAction() == null) {
      taskContainer.create(
          t.getName(),
          t.getTaskAction(),
          task -> {
            task.setGroup(t.getGroup());
            task.setDescription(t.getDescription());
          });
      taskContainer.create(
          t.getShortcut(),
          t.getTaskAction(),
          task -> {
            task.setGroup(t.getGroup());
            task.setDescription(t.getDescription());
          });
    } else {
      Task temp = taskContainer.create(t.getName(), t.getTaskAction(), t.getAction());
      temp.setGroup(t.getGroup());
      temp.setDescription(t.getDescription());
      Task temp2 = taskContainer.create(t.getShortcut(), t.getTaskAction(), t.getAction());
      temp2.setGroup(t.getGroup());
      temp2.setDescription(t.getDescription());
    }
  }
}
