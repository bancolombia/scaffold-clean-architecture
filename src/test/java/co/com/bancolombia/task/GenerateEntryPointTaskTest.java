package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.entrypoints.ModuleFactoryEntryPoint;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;


public class GenerateEntryPointTaskTest {
    private GenerateEntryPointTask task;

    @Before
    public void setup() throws IOException, CleanException {
        Project project = ProjectBuilder.builder().withProjectDir(new File("build/unitTest")).build();
        project.getTasks().create("ca", GenerateStructureTask.class);
        GenerateStructureTask caTask = (GenerateStructureTask) project.getTasks().getByName("ca");
        caTask.generateStructureTask();

        ProjectBuilder.builder()
                .withProjectDir(new File("build/unitTest/applications/app-service"))
                .withName("app-service")
                .withParent(project)
                .build();

        project.getTasks().create("test", GenerateEntryPointTask.class);
        task = (GenerateEntryPointTask) project.getTasks().getByName("test");
    }

    // Assert
    @Test(expected = IllegalArgumentException.class)
    public void shouldHandleErrorWhenNoType() throws IOException, CleanException {
        // Arrange
        task.setType(null);
        // Act
        task.generateEntryPointTask();
    }

    // Assert
    @Test(expected = IllegalArgumentException.class)
    public void shouldHandleErrorWhenNoName() throws IOException, CleanException {
        // Arrange
        task.setType(ModuleFactoryEntryPoint.EntryPointType.GENERIC);
        // Act
        task.generateEntryPointTask();
    }

    // Assert
    @Test(expected = IllegalArgumentException.class)
    public void shouldHandleErrorWhenEmptyName() throws IOException, CleanException {
        // Arrange
        task.setType(ModuleFactoryEntryPoint.EntryPointType.GENERIC);
        task.setName("");
        // Act
        task.generateEntryPointTask();
    }

    @Test
    public void generateEntryPointGeneric() throws IOException, CleanException {
        // Arrange
        task.setType(ModuleFactoryEntryPoint.EntryPointType.GENERIC);
        task.setName("MyEntryPoint");
        // Act
        task.generateEntryPointTask();
        // Assert
        assertTrue(new File("build/unitTest/infrastructure/entry-points/my-entry-point/build.gradle").exists());
        assertTrue(new File("build/unitTest/infrastructure/entry-points/my-entry-point/src/main/java/co/com/bancolombia/myentrypoint").exists());
        assertTrue(new File("build/unitTest/infrastructure/entry-points/my-entry-point/src/test/java/co/com/bancolombia/myentrypoint").exists());
    }

    @Test
    public void generateEntryPointApiRest() throws IOException, CleanException {
        // Arrange
        task.setType(ModuleFactoryEntryPoint.EntryPointType.RESTMVC);
        // Act
        task.generateEntryPointTask();
        // Assert
        assertTrue(new File("build/unitTest/infrastructure/entry-points/api-rest/build.gradle").exists());
        assertTrue(new File("build/unitTest/infrastructure/entry-points/api-rest/src/main/java/co/com/bancolombia/api/ApiRest.java").exists());
        assertTrue(new File("build/unitTest/infrastructure/entry-points/api-rest/src/test/java/co/com/bancolombia/api").exists());
    }

    @Test
    public void generateEntryPointReactiveWeb() throws IOException, CleanException {
        // Arrange
        task.setType(ModuleFactoryEntryPoint.EntryPointType.WEBFLUX);
        // Act
        task.generateEntryPointTask();
        // Assert
        assertTrue(new File("build/unitTest/infrastructure/entry-points/reactive-web/build.gradle").exists());
        assertTrue(new File("build/unitTest/infrastructure/entry-points/reactive-web/src/main/java/co/com/bancolombia/api/ApiRest.java").exists());
        assertTrue(new File("build/unitTest/infrastructure/entry-points/reactive-web/src/test/java/co/com/bancolombia/api").exists());
    }
}
