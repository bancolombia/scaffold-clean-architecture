package co.com.bancolombia.task;

import co.com.bancolombia.task.GenerateStructureTask;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class GenerateStructureTaskTest {



    @Test
    public void generateStructure() throws IOException {


        Project project = ProjectBuilder.builder().withProjectDir(new File("build/unitTest")).build();
        project.getTasks().create("test", GenerateStructureTask.class);

        GenerateStructureTask task = (GenerateStructureTask) project.getTasks().getByName("test");
        
        task.generateStructureTask();
        // Verify the result
        assertTrue(new File("build/unitTest/Readme.md").exists());
        assertTrue(new File("build/unitTest/.gitignore").exists());
        assertTrue(new File("build/unitTest/build.gradle").exists());
        assertTrue(new File("build/unitTest/lombok.config").exists());
        assertTrue(new File("build/unitTest/main.gradle").exists());
        assertTrue(new File("build/unitTest/settings.gradle").exists());

        assertTrue(new File("build/unitTest/infrastructure/driven-adapters/").exists());
        assertTrue(new File("build/unitTest/infrastructure/entry-points").exists());
        assertTrue(new File("build/unitTest/infrastructure/helpers").exists());

        assertTrue(new File("build/unitTest/domain/model/src/main/java/co/com/bancolombia/model").exists());
        assertTrue(new File("build/unitTest/domain/model/src/test/java/co/com/bancolombia/model").exists());
        assertTrue(new File("build/unitTest/domain/model/build.gradle").exists());
        assertTrue(new File("build/unitTest/domain/usecase/src/main/java/co/com/bancolombia/usecase").exists());
        assertTrue(new File("build/unitTest/domain/usecase/src/test/java/co/com/bancolombia/usecase").exists());
        assertTrue(new File("build/unitTest/domain/usecase/build.gradle").exists());

        assertTrue(new File("build/unitTest/applications/app-service/build.gradle").exists());
        assertTrue(new File("build/unitTest/applications/app-service/src/main/java/co/com/bancolombia/MainApplication.java").exists());
        assertTrue(new File("build/unitTest/applications/app-service/src/main/java/co/com/bancolombia/config").exists());
        assertTrue(new File("build/unitTest/applications/app-service/src/main/resources/application.yaml").exists());
        assertTrue(new File("build/unitTest/applications/app-service/src/main/resources/log4j2.properties").exists());
        assertTrue(new File("build/unitTest/applications/app-service/src/test/java/co/com/bancolombia").exists());

    }

    @Test
    public void generateStructureReactive() throws IOException {


        Project project = ProjectBuilder.builder().withProjectDir(new File("build/unitTest")).build();
        project.getTasks().create("test", GenerateStructureTask.class);

        GenerateStructureTask task = (GenerateStructureTask) project.getTasks().getByName("test");

        task.setPackage("test");
        task.setProjectName("projectTest");
        task.setType("reactive");
        task.generateStructureTask();

        assertTrue(new File("build/unitTest/Readme.md").exists());
        assertTrue(new File("build/unitTest/.gitignore").exists());
        assertTrue(new File("build/unitTest/build.gradle").exists());
        assertTrue(new File("build/unitTest/lombok.config").exists());
        assertTrue(new File("build/unitTest/main.gradle").exists());
        assertTrue(new File("build/unitTest/settings.gradle").exists());

        assertTrue(new File("build/unitTest/infrastructure/driven-adapters/").exists());
        assertTrue(new File("build/unitTest/infrastructure/entry-points").exists());
        assertTrue(new File("build/unitTest/infrastructure/helpers").exists());

        assertTrue(new File("build/unitTest/domain/model/src/main/java/test/model").exists());
        assertTrue(new File("build/unitTest/domain/model/src/test/java/test/model").exists());
        assertTrue(new File("build/unitTest/domain/model/build.gradle").exists());
        assertTrue(new File("build/unitTest/domain/usecase/src/main/java/test/usecase").exists());
        assertTrue(new File("build/unitTest/domain/usecase/src/test/java/test/usecase").exists());
        assertTrue(new File("build/unitTest/domain/usecase/build.gradle").exists());

        assertTrue(new File("build/unitTest/applications/app-service/build.gradle").exists());
        assertTrue(new File("build/unitTest/applications/app-service/src/main/java/test/MainApplication.java").exists());
        assertTrue(new File("build/unitTest/applications/app-service/src/main/java/test/config").exists());
        assertTrue(new File("build/unitTest/applications/app-service/src/main/resources/application.yaml").exists());
        assertTrue(new File("build/unitTest/applications/app-service/src/main/resources/log4j2.properties").exists());
        assertTrue(new File("build/unitTest/applications/app-service/src/test/java/test").exists());


    }

}
