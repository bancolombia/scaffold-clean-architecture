
package co.com.bancolombia;

import co.com.bancolombia.models.TaskModel;
import co.com.bancolombia.task.*;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskContainer;

import java.util.ArrayList;
import java.util.List;

public class PluginClean implements Plugin<Project> {
    private static final String TASK_GROUP = "Clean Architecture";
    private TaskContainer taskContainer;

    public void apply(Project project) {
        List<TaskModel> tasks = initTasks();
        taskContainer = project.getTasks();
        tasks.forEach(this::appendTask);
    }

    private List<TaskModel> initTasks() {
        List<TaskModel> tasksModels = new ArrayList<>();

        tasksModels.add(TaskModel.builder().name("cleanArchitecture").shortcut("ca")
                .description("Scaffolding clean architecture project").group(TASK_GROUP)
                .taskAction(GenerateStructureTask.class).build());

        tasksModels.add(TaskModel.builder().name("generateModel").shortcut("gm")
                .description("Generate model in domain layer").group(TASK_GROUP)
                .taskAction(GenerateModelTask.class).build());

        tasksModels.add(TaskModel.builder().name("generateUseCase").shortcut("guc")
                .description("Generate use case in domain layer").group(TASK_GROUP)
                .taskAction(GenerateUseCaseTask.class).build());

        tasksModels.add(TaskModel.builder().name("generateEntryPoint").shortcut("gep")
                .description("Generate entry point in infrastructure layer").group(TASK_GROUP)
                .taskAction(GenerateEntryPointTask.class).build());

        tasksModels.add(TaskModel.builder().name("generateDrivenAdapter").shortcut("gda")
                .description("Generate driven adapter in infrastructure layer").group(TASK_GROUP)
                .taskAction(GenerateDrivenAdapterTask.class).build());

        tasksModels.add(TaskModel.builder().name("validateStructure").shortcut("vs")
                .description("Validate that project references are not violated").group(TASK_GROUP)
                .taskAction(ValidateStructureTask.class).build());

        tasksModels.add(TaskModel.builder().name("generatePipeline").shortcut("gpl")
                .description("Generate CI pipeline as a code in deployment layer").group(TASK_GROUP)
                .taskAction(GeneratePipelineTask.class).build());

        return tasksModels;
    }

    @SuppressWarnings("unchecked")
    private void appendTask(TaskModel t) {
        Task temp = taskContainer.create(t.getName(), t.getTaskAction());
        taskContainer.create(t.getShortcut(), t.getTaskAction());
        temp.setGroup(t.getGroup());
        temp.setDescription(t.getDescription());
    }
}
