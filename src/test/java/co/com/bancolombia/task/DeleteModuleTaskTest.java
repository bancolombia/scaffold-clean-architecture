package co.com.bancolombia.task;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.FileUtilsTest.deleteStructure;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import co.com.bancolombia.exceptions.CleanException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;

public class DeleteModuleTaskTest {
  private DeleteModuleTask task;

  @Before
  public void setup() throws IOException, CleanException {
    deleteStructure(Path.of("build/unitTest"));
    Project project =
        ProjectBuilder.builder()
            .withName("cleanArchitecture")
            .withProjectDir(new File("build/unitTest"))
            .build();

    project.getTasks().create("ca", GenerateStructureTask.class);
    GenerateStructureTask generateStructureTask =
        (GenerateStructureTask) project.getTasks().getByName("ca");
    generateStructureTask.execute();

    ProjectBuilder.builder()
        .withName(APP_SERVICE)
        .withProjectDir(new File("build/unitTest/applications/app-service"))
        .withParent(project)
        .build();

    project.getTasks().create("gda", GenerateDrivenAdapterTask.class);
    GenerateDrivenAdapterTask generateDriven =
        (GenerateDrivenAdapterTask) project.getTasks().getByName("gda");
    generateDriven.setType("MONGODB");
    generateDriven.execute();

    ProjectBuilder.builder()
        .withName("mongo-repository")
        .withProjectDir(new File("build/unitTest/infrastructure/driven-adapters/mongo-repository"))
        .withParent(project)
        .build();

    assertTrue(
        new File("build/unitTest/infrastructure/driven-adapters/mongo-repository/build.gradle")
            .exists());

    project.getTasks().create("test", DeleteModuleTask.class);
    task = (DeleteModuleTask) project.getTasks().getByName("test");
  }

  // Assert
  @Test(expected = IllegalArgumentException.class)
  public void deleteNullModule() throws IOException, CleanException {
    // Arrange
    // Act
    task.execute();
  }

  // Assert
  @Test(expected = IllegalArgumentException.class)
  public void deleteNonExistentModule() throws IOException, CleanException {
    // Arrange
    task.setModule("non-existent");
    // Act
    task.execute();
  }

  @Test
  public void generateEntryPoint() throws IOException, CleanException {
    // Arrange
    task.setModule("mongo-repository");
    // Act
    task.execute();
    // Assert
    assertFalse(
        new File("build/unitTest/infrastructure/driven-adapters/mongo-repository/build.gradle")
            .exists());
  }
}
