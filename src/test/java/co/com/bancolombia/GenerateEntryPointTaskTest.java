package co.com.bancolombia;

import co.com.bancolombia.task.GenerateEntryPointTask;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

public class GenerateEntryPointTaskTest {
    @Test
    public void settersTask() {
        Project project = ProjectBuilder.builder().build();
        project.getTasks().create("test", GenerateEntryPointTask.class);

        GenerateEntryPointTask task = (GenerateEntryPointTask) project.getTasks().getByName("test");

        task.setEntryPoint("1");
    }
}
