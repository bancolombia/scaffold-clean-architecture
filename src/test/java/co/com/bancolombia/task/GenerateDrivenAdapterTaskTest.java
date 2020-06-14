package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.adapters.ModuleFactoryDrivenAdapter;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class GenerateDrivenAdapterTaskTest {
    private GenerateDrivenAdapterTask task;

    @Before
    public void init() throws IOException, CleanException {
        Project project = ProjectBuilder.builder().withProjectDir(new File("build/unitTest")).build();

        ProjectBuilder.builder()
                .withName("app-service")
                .withProjectDir(new File("build/unitTest/applications/app-service"))
                .withParent(project)
                .build();

        project.getTasks().create("ca", GenerateStructureTask.class);
        GenerateStructureTask taskStructure = (GenerateStructureTask) project.getTasks().getByName("ca");
        taskStructure.generateStructureTask();

        project.getTasks().create("test", GenerateDrivenAdapterTask.class);
        task = (GenerateDrivenAdapterTask) project.getTasks().getByName("test");
    }

    // Assert
    @Test(expected = IllegalArgumentException.class)
    public void shouldHandleErrorWhenNoType() throws IOException, CleanException {
        // Arrange
        task.setType(null);
        // Act
        task.generateDrivenAdapterTask();
    }

    // Assert
    @Test(expected = IllegalArgumentException.class)
    public void shouldHandleErrorWhenNoName() throws IOException, CleanException {
        // Arrange
        task.setType(ModuleFactoryDrivenAdapter.DrivenAdapterType.GENERIC);
        // Act
        task.generateDrivenAdapterTask();
    }

    // Assert
    @Test(expected = IllegalArgumentException.class)
    public void shouldHandleErrorWhenEmptyName() throws IOException, CleanException {
        // Arrange
        task.setType(ModuleFactoryDrivenAdapter.DrivenAdapterType.GENERIC);
        task.setName("");
        // Act
        task.generateDrivenAdapterTask();
    }

    @Test
    public void generateEntryPointGeneric() throws IOException, CleanException {
        // Arrange
        task.setType(ModuleFactoryDrivenAdapter.DrivenAdapterType.GENERIC);
        task.setName("MyDrivenAdapter");
        // Act
        task.generateDrivenAdapterTask();
        // Assert
        assertTrue(new File("build/unitTest/infrastructure/driven-adapters/my-driven-adapter/build.gradle").exists());
        assertTrue(new File("build/unitTest/infrastructure/driven-adapters/my-driven-adapter/src/main/java/co/com/bancolombia/mydrivenadapter").exists());
        assertTrue(new File("build/unitTest/infrastructure/driven-adapters/my-driven-adapter/src/test/java/co/com/bancolombia/mydrivenadapter").exists());
    }

    @Test
    public void generateDrivenAdapterJPARepository() throws IOException, CleanException {
        // Arrange
        task.setType(ModuleFactoryDrivenAdapter.DrivenAdapterType.JPA);
        // Act
        task.generateDrivenAdapterTask();
        // Assert
        assertTrue(new File("build/unitTest/infrastructure/driven-adapters/jpa-repository/build.gradle").exists());
        assertTrue(new File("build/unitTest/infrastructure/driven-adapters/jpa-repository/src/main/java/co/com/bancolombia/jpa/JPARepository.java").exists());
        assertTrue(new File("build/unitTest/infrastructure/driven-adapters/jpa-repository/src/main/java/co/com/bancolombia/jpa/JPARepositoryAdapter.java").exists());
        assertTrue(new File("build/unitTest/infrastructure/driven-adapters/jpa-repository/src/main/java/co/com/bancolombia/jpa/helper/AdapterOperations.java").exists());
        assertTrue(new File("build/unitTest/applications/app-service/src/main/java/co/com/bancolombia/config/JpaConfig.java").exists());
    }

    @Test
    public void generateDrivenAdapterMongoRepository() throws IOException, CleanException {
        // Arrange
        task.setType(ModuleFactoryDrivenAdapter.DrivenAdapterType.MONGODB);
        // Act
        task.generateDrivenAdapterTask();
        // Assert
        assertTrue(new File("build/unitTest/infrastructure/driven-adapters/mongo-repository/build.gradle").exists());
        assertTrue(new File("build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/java/co/com/bancolombia/mongo/MongoDBRepository.java").exists());
        assertTrue(new File("build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/java/co/com/bancolombia/mongo/MongoRepositoryAdapter.java").exists());
        assertTrue(new File("build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/java/co/com/bancolombia/mongo/helper/AdapterOperations.java").exists());
    }

    @Test
    public void generateDrivenAdapterEventBus() throws IOException, CleanException {
        // Arrange
        task.setType(ModuleFactoryDrivenAdapter.DrivenAdapterType.ASYNCEVENTBUS);
        // Act
        task.generateDrivenAdapterTask();
        // Assert
        assertTrue(new File("build/unitTest/infrastructure/driven-adapters/async-event-bus/build.gradle").exists());
        assertTrue(new File("build/unitTest/infrastructure/driven-adapters/async-event-bus/src/main/java/co/com/bancolombia/event/ReactiveEventsGateway.java").exists());
        assertTrue(new File("build/unitTest/domain/model/src/main/java/co/com/bancolombia/model/event/gateways/EventsGateway.java").exists());
    }
}
