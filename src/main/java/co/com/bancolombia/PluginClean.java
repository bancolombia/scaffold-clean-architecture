package co.com.bancolombia;

import co.com.bancolombia.models.TaskModel;
import co.com.bancolombia.task.*;
import java.util.ArrayList;
import java.util.List;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskContainer;

public class PluginClean implements Plugin<Project> {
  private TaskContainer taskContainer;
  private CleanPluginExtension cleanPluginExtension;

  public void apply(Project project) {

    cleanPluginExtension =
        project.getExtensions().create("cleanPlugin", CleanPluginExtension.class);

    taskContainer = project.getTasks();
    List<TaskModel> tasks = initTasks();

    tasks.forEach(this::appendTask);
  }

  private List<TaskModel> initTasks() {
    List<TaskModel> tasksModels = new ArrayList<>();

    tasksModels.add(
        TaskModel.builder()
            .name("cleanArchitecture")
            .shortcut("ca")
            .description("Scaffolding clean architecture project")
            .group(Constants.PLUGIN_TASK_GROUP)
            .taskAction(GenerateStructureTask.class)
            .build());

    tasksModels.add(
        TaskModel.builder()
            .name("generateModel")
            .shortcut("gm")
            .description("Generate model in domain layer")
            .group(Constants.PLUGIN_TASK_GROUP)
            .taskAction(GenerateModelTask.class)
            .build());

    tasksModels.add(
        TaskModel.builder()
            .name("generateUseCase")
            .shortcut("guc")
            .description("Generate use case in domain layer")
            .group(Constants.PLUGIN_TASK_GROUP)
            .taskAction(GenerateUseCaseTask.class)
            .build());

    tasksModels.add(
        TaskModel.builder()
            .name("generateEntryPoint")
            .shortcut("gep")
            .description("Generate entry point in infrastructure layer")
            .group(Constants.PLUGIN_TASK_GROUP)
            .taskAction(GenerateEntryPointTask.class)
            .build());

    tasksModels.add(
        TaskModel.builder()
            .name("generateDrivenAdapter")
            .shortcut("gda")
            .description("Generate driven adapter in infrastructure layer")
            .group(Constants.PLUGIN_TASK_GROUP)
            .taskAction(GenerateDrivenAdapterTask.class)
            .build());

    tasksModels.add(
        TaskModel.builder()
            .name("validateStructure")
            .shortcut("vs")
            .description("Validate that project references are not violated")
            .group(Constants.PLUGIN_TASK_GROUP)
            .taskAction(ValidateStructureTask.class)
            .action(
                (Action<? extends ValidateStructureTask>)
                    task ->
                        task.getWhitelistedDependencies()
                            .set(cleanPluginExtension.getModelProps().getWhitelistedDependencies()))
            .build());

    tasksModels.add(
        TaskModel.builder()
            .name("generatePipeline")
            .shortcut("gpl")
            .description("Generate CI pipeline as a code in deployment layer")
            .group(Constants.PLUGIN_TASK_GROUP)
            .taskAction(GeneratePipelineTask.class)
            .build());

    tasksModels.add(
        TaskModel.builder()
            .name("deleteModule")
            .shortcut("dm")
            .description("Delete gradle module")
            .group(Constants.PLUGIN_TASK_GROUP)
            .taskAction(DeleteModuleTask.class)
            .build());
    tasksModels.add(
        TaskModel.builder()
            .name("generateAcceptanceTest")
            .shortcut("gat")
            .description("Generate subproject by karate framework in deployment layer")
            .group(Constants.PLUGIN_TASK_GROUP)
            .taskAction(GenerateAcceptanceTestTask.class)
            .build());
    tasksModels.add(
        TaskModel.builder()
            .name("generateHelper")
            .shortcut("gh")
            .description("Generate helper in infrastructure layer")
            .group(Constants.PLUGIN_TASK_GROUP)
            .taskAction(GenerateHelperTask.class)
            .build());
    tasksModels.add(
        TaskModel.builder()
            .name("updateCleanArchitecture")
            .shortcut("u")
            .description("Update project dependencies")
            .group(Constants.PLUGIN_TASK_GROUP)
            .taskAction(UpdateProjectTask.class)
            .build());
    return tasksModels;
  }

  @SuppressWarnings("unchecked")
  private void appendTask(TaskModel t) {
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
