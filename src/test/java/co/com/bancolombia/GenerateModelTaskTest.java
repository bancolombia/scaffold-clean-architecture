package co.com.bancolombia;

import co.com.bancolombia.task.GenerateModelTask;
import co.com.bancolombia.task.GenerateStructureTask;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

public class GenerateModelTaskTest {
    @Test
    public void settersTask() {
        Project project = ProjectBuilder.builder().build();
        project.getTasks().create("test", GenerateModelTask.class);

        GenerateModelTask task = (GenerateModelTask) project.getTasks().getByName("test");

        task.setNameProject("testName");
    }
}
