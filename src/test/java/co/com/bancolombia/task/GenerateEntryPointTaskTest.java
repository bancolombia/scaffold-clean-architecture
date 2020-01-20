package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.CleanException;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;


public class GenerateEntryPointTaskTest {

    @Test(expected = IllegalArgumentException.class)
    public void generateEntryPointValueNegative() throws IOException, CleanException {
        Project project = ProjectBuilder.builder().build();
        project.getTasks().create("test", GenerateEntryPointTask.class);

        GenerateEntryPointTask task = (GenerateEntryPointTask) project.getTasks().getByName("test");

        task.setCodeEntryPoint("-8");
        task.generateEntryPointTask();
    }

    @Test(expected = IllegalArgumentException.class)
    public void generateEntryPointValueUnExistent() throws IOException, CleanException {
        Project project = ProjectBuilder.builder().build();
        project.getTasks().create("test", GenerateEntryPointTask.class);

        GenerateEntryPointTask task = (GenerateEntryPointTask) project.getTasks().getByName("test");

        task.setCodeEntryPoint("100");
        task.generateEntryPointTask();
    }

    @Test
    public void generateEntryPointApiRest() throws IOException, CleanException {
        Project project = ProjectBuilder.builder().withProjectDir(new File("build/unitTest")).build();
        project.getTasks().create("test", GenerateEntryPointTask.class);

        GenerateEntryPointTask task = (GenerateEntryPointTask) project.getTasks().getByName("test");

        task.setCodeEntryPoint("1");
        task.generateEntryPointTask();
        assertTrue(new File("build/unitTest/infrastructure/entry-points/api-rest/build.gradle").exists());
        assertTrue(new File("build/unitTest/infrastructure/entry-points/api-rest/src/main/java/co/com/bancolombia/api/ApiRest.java").exists());
        assertTrue(new File("build/unitTest/infrastructure/entry-points/api-rest/src/test/java/co/com/bancolombia/api").exists());

    }
    @Test
    public void generateEntryPointReactiveWeb() throws IOException, CleanException {
        Project project = ProjectBuilder.builder().withProjectDir(new File("build/unitTest")).build();
        project.getTasks().create("test", GenerateEntryPointTask.class);

        GenerateEntryPointTask task = (GenerateEntryPointTask) project.getTasks().getByName("test");

        task.setCodeEntryPoint("2");
        task.generateEntryPointTask();
        assertTrue(new File("build/unitTest/infrastructure/entry-points/reactive-web/build.gradle").exists());
        assertTrue(new File("build/unitTest/infrastructure/entry-points/reactive-web/src/main/java/co/com/bancolombia/api/ApiRest.java").exists());
        assertTrue(new File("build/unitTest/infrastructure/entry-points/reactive-web/src/test/java/co/com/bancolombia/api").exists());

    }

}
