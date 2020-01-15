package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.task.GenerateEntryPointTask;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

import java.io.File;
import java.io.IOException;


public class GenerateEntryPointTaskTest {


    @Test
    public void settersTask() {
        Project project = ProjectBuilder.builder().build();
        project.getTasks().create("test", GenerateEntryPointTask.class);

        GenerateEntryPointTask task = (GenerateEntryPointTask) project.getTasks().getByName("test");

        task.setEntryPoint("1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void generateEntryPointValueNegative() throws IOException, CleanException {
        Project project = ProjectBuilder.builder().build();
        project.getTasks().create("test", GenerateEntryPointTask.class);

        GenerateEntryPointTask task = (GenerateEntryPointTask) project.getTasks().getByName("test");

        task.setEntryPoint("-8");
        task.generateEntryPointTask();
    }

    @Test(expected = IllegalArgumentException.class)
    public void generateEntryPointValueUnExistent() throws IOException, CleanException {
        Project project = ProjectBuilder.builder().build();
        project.getTasks().create("test", GenerateEntryPointTask.class);

        GenerateEntryPointTask task = (GenerateEntryPointTask) project.getTasks().getByName("test");

        task.setEntryPoint("100");
        task.generateEntryPointTask();
    }

    @Test
    public void generateEntryPointApiRest() throws IOException, CleanException {
        Project project = ProjectBuilder.builder().withProjectDir(new File("build/unitTest")).build();
        project.getTasks().create("test", GenerateEntryPointTask.class);

        GenerateEntryPointTask task = (GenerateEntryPointTask) project.getTasks().getByName("test");

        task.setEntryPoint("1");
        task.generateEntryPointTask();
    }
    @Test
    public void generateEntryPointReactiveWeb() throws IOException, CleanException {
        Project project = ProjectBuilder.builder().withProjectDir(new File("build/unitTest")).build();
        project.getTasks().create("test", GenerateEntryPointTask.class);

        GenerateEntryPointTask task = (GenerateEntryPointTask) project.getTasks().getByName("test");

        task.setEntryPoint("2");
        task.generateEntryPointTask();
    }

}
