package co.com.bancolombia;

import co.com.bancolombia.task.GenerateUseCaseTask;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

public class GenerateUseCaseTaskTest {
    @Test
    public void settersTask() {
        Project project = ProjectBuilder.builder().build();
        project.getTasks().create("test", GenerateUseCaseTask.class);

        GenerateUseCaseTask task = (GenerateUseCaseTask) project.getTasks().getByName("test");

        task.setNameProject("testName");
    }
}
