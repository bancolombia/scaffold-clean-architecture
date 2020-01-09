package co.com.bancolombia;

import co.com.bancolombia.task.GenerateDrivenAdapterTask;
import co.com.bancolombia.task.GenerateUseCaseTask;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

public class GenerateDrivenAdapterTaskTest {
    @Test
    public void settersTask() {
        Project project = ProjectBuilder.builder().build();
        project.getTasks().create("test", GenerateDrivenAdapterTask.class);

        GenerateDrivenAdapterTask task = (GenerateDrivenAdapterTask) project.getTasks().getByName("test");

        task.setDrivenAdapter("1");
    }
}
