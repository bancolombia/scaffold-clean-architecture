package co.com.bancolombia.task;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.TestUtils.createTask;
import static co.com.bancolombia.TestUtils.deleteStructure;
import static co.com.bancolombia.TestUtils.getTask;
import static co.com.bancolombia.TestUtils.getTestDir;
import static co.com.bancolombia.TestUtils.setupProject;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.com.bancolombia.exceptions.CleanException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class DeleteModuleTaskTest {
  private static final String TEST_DIR = getTestDir(DeleteModuleTaskTest.class);
  private static DeleteModuleTask task;

  @BeforeAll
  public static void setup() throws IOException, CleanException {
    deleteStructure(Path.of(TEST_DIR));
    Project project = setupProject(DeleteModuleTaskTest.class, GenerateStructureTask.class);

    GenerateStructureTask generateStructureTask = getTask(project, GenerateStructureTask.class);
    generateStructureTask.execute();

    ProjectBuilder.builder()
        .withName(APP_SERVICE)
        .withProjectDir(new File(TEST_DIR + "/applications/app-service"))
        .withParent(project)
        .build();

    GenerateDrivenAdapterTask generateDriven = createTask(project, GenerateDrivenAdapterTask.class);
    generateDriven.setType("MONGODB");
    generateDriven.execute();

    ProjectBuilder.builder()
        .withName("mongo-repository")
        .withProjectDir(new File(TEST_DIR + "/infrastructure/driven-adapters/mongo-repository"))
        .withParent(project)
        .build();

    assertTrue(
        new File(TEST_DIR + "/infrastructure/driven-adapters/mongo-repository/build.gradle")
            .exists());

    task = createTask(project, DeleteModuleTask.class);
  }

  @AfterAll
  public static void tearDown() {
    deleteStructure(Path.of(TEST_DIR));
  }

  // Assert
  @Test
  void deleteNullModule() {
    // Arrange
    // Act
    assertThrows(IllegalArgumentException.class, () -> task.execute());
  }

  // Assert
  @Test
  void deleteNonExistentModule() {
    // Arrange
    task.setModule("non-existent");
    // Act
    assertThrows(IllegalArgumentException.class, () -> task.execute());
  }

  @Test
  void generateEntryPoint() throws IOException, CleanException {
    // Arrange
    task.setModule("mongo-repository");
    // Act
    task.execute();
    // Assert
    assertFalse(
        new File(TEST_DIR + "/infrastructure/driven-adapters/mongo-repository/build.gradle")
            .exists());
  }
}
