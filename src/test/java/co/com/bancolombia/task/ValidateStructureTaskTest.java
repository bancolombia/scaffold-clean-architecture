package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.adapters.ModuleFactoryDrivenAdapter;
import co.com.bancolombia.task.ValidateStructureTask;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;

import static org.junit.Assert.assertTrue;

public class ValidateStructureTaskTest {

    ValidateStructureTask task;

    @Before
    public void setup() throws IOException, CleanException {
        Project project = ProjectBuilder.builder()
                .withName("cleanArchitecture")
                .withProjectDir(new File("build/unitTest"))
                .build();

        project.getTasks().create("ca", GenerateStructureTask.class);
        GenerateStructureTask generateStructureTask = (GenerateStructureTask) project.getTasks().getByName("ca");
        generateStructureTask.generateStructureTask();

        ProjectBuilder.builder()
                .withName("app-service")
                .withProjectDir(new File("build/unitTest/applications/app-service"))
                .withParent(project)
                .build();

        project.getTasks().create("gda", GenerateDrivenAdapterTask.class);
        GenerateDrivenAdapterTask generateDriven = (GenerateDrivenAdapterTask) project.getTasks().getByName("gda");
        generateDriven.setType(ModuleFactoryDrivenAdapter.DrivenAdapterType.MONGODB);
        generateDriven.generateDrivenAdapterTask();

        ProjectBuilder.builder()
                .withName("mongo-repository")
                .withProjectDir(new File("build/unitTest/infrastructure/driven-adapters/mongo-repository"))
                .withParent(project)
                .build();

        ProjectBuilder.builder()
                .withName("model")
                .withProjectDir(new File("build/unitTest/domain/model"))
                .withParent(project)
                .build();

        assertTrue(new File("build/unitTest/infrastructure/driven-adapters/mongo-repository/build.gradle").exists());

        project.getTasks().create("test", ValidateStructureTask.class);
        task = (ValidateStructureTask) project.getTasks().getByName("test");
    }


    @Test
    public void validateStructure() throws IOException, CleanException {
        // Act
        task.validateStructureTask();
        // Assert
    }




}
