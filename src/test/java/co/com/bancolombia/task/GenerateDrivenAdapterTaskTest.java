package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.task.GenerateDrivenAdapterTask;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;

import static org.junit.Assert.assertTrue;

public class GenerateDrivenAdapterTaskTest {
    GenerateDrivenAdapterTask task;

    @Before
    public void init() throws IOException {
        File projectDir = new File("build/unitTest");
        Files.createDirectories(projectDir.toPath());
        writeString(new File(projectDir, "settings.gradle"), "");
        writeString(new File(projectDir, "build.gradle"),
                "plugins {" +
                        "  id('co.com.bancolombia.cleanArchitecture')" +
                        "}");
        Project project = ProjectBuilder.builder().withProjectDir(new File("build/unitTest")).build();
        project.getTasks().create("test", GenerateDrivenAdapterTask.class);

        task = (GenerateDrivenAdapterTask) project.getTasks().getByName("test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void generateDrivenAdapterValueNegative() throws IOException, CleanException {

        task.setDrivenAdapter("-8");
        task.generateDrivenAdapterTask();
    }

    @Test(expected = CleanException.class)
    public void generateDrivenAdapterValueUnExistent() throws IOException, CleanException {


        task.setDrivenAdapter("100");
        task.generateDrivenAdapterTask();
    }

    @Test
    public void generateDrivenAdapterJPARepository() throws IOException, CleanException {

        task.setDrivenAdapter("1");
        task.generateDrivenAdapterTask();

        assertTrue(new File("build/unitTest/infrastructure/driven-adapters/jpa-repository/build.gradle").exists());
        assertTrue(new File("build/unitTest/infrastructure/driven-adapters/jpa-repository/src/main/java/co/com/bancolombia/jpa/JPARepository.java").exists());
        assertTrue(new File("build/unitTest/infrastructure/driven-adapters/jpa-repository/src/main/java/co/com/bancolombia/jpa/JPARepositoryAdapter.java").exists());

        assertTrue(new File("build/unitTest/infrastructure//helpers/jpa-repository-commons/build.gradle").exists());
        assertTrue(new File("build/unitTest/infrastructure/helpers/jpa-repository-commons/src/main/java/co/com/bancolombia/jpa/AdapterOperations.java").exists());

        assertTrue(new File("build/unitTest/applications/app-service/src/main/java/co/com/bancolombia/config/jpa/JpaConfig.java").exists());
        assertTrue(new File("build/unitTest/applications/app-service/src/main/resources/application-jpaAdapter.yaml").exists());
        assertTrue(new File("build/unitTest/domain/model/src/main/java/co/com/bancolombia/model/secret/Secret.java").exists());
    }

    @Test
    public void generateDrivenAdapterMongoRepository() throws IOException, CleanException {

        task.setDrivenAdapter("2");
        task.generateDrivenAdapterTask();

        assertTrue(new File("build/unitTest/infrastructure/driven-adapters/mongo-repository/build.gradle").exists());
        assertTrue(new File("build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/java/co/com/bancolombia/mongo/MongoRepository.java").exists());
        assertTrue(new File("build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/java/co/com/bancolombia/mongo/MongoRepositoryAdapter.java").exists());

        assertTrue(new File("build/unitTest/infrastructure//helpers/mongo-repository-commons/build.gradle").exists());
        assertTrue(new File("build/unitTest/infrastructure/helpers/mongo-repository-commons/src/main/java/co/com/bancolombia/mongo/AdapterOperations.java").exists());

    }

    @Test
    public void generateDrivenAdapterEventBus() throws IOException, CleanException {

        task.setDrivenAdapter("3");
        task.generateDrivenAdapterTask();
        assertTrue(new File("build/unitTest/infrastructure/driven-adapters/async-event-bus/build.gradle").exists());
        assertTrue(new File("build/unitTest/infrastructure/driven-adapters/async-event-bus/src/main/java/co/com/bancolombia/events/ReactiveEventsGateway.java").exists());
        assertTrue(new File("build/functionalTest/domain/model/src/main/java/co/com/bancolombia/model/event/gateways/EventRepository.java").exists());

    }

    private void writeString(File file, String string) throws IOException {
        try (Writer writer = new FileWriter(file)) {
            writer.write(string);
        }
    }
}
