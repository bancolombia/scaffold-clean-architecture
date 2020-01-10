package co.com.bancolombia;

import co.com.bancolombia.task.GenerateModelTask;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

import java.io.File;
import java.io.IOException;


public class GenerateModelTaskTest {

    @Test
    public void settersTask() {
        Project project = ProjectBuilder.builder().build();
        project.getTasks().create("test", GenerateModelTask.class);

        GenerateModelTask task = (GenerateModelTask) project.getTasks().getByName("test");

        task.setNameProject("testName");
    }
    @Test(expected = IllegalArgumentException.class)
    public void generateEntryPointValueUnExistent() throws IOException {
        Project project = ProjectBuilder.builder().build();
        project.getTasks().create("test", GenerateModelTask.class);

        GenerateModelTask task = (GenerateModelTask) project.getTasks().getByName("test");

        task.generateModel();
    }

    @Test
    public void generateEntryPoint() throws IOException {
        Project project = ProjectBuilder.builder().withProjectDir(new File("build/unitTest")).build();
        project.getTasks().create("test", GenerateModelTask.class);

        GenerateModelTask task = (GenerateModelTask) project.getTasks().getByName("test");

        task.setNameProject("nameModel");
        task.generateModel();
    }

}
