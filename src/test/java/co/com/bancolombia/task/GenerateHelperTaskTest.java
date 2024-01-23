package co.com.bancolombia.task;

import static co.com.bancolombia.TestUtils.assertFilesExistsInDir;
import static co.com.bancolombia.TestUtils.createTask;
import static co.com.bancolombia.TestUtils.deleteStructure;
import static co.com.bancolombia.TestUtils.getTask;
import static co.com.bancolombia.TestUtils.getTestDir;
import static co.com.bancolombia.TestUtils.setupProject;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.com.bancolombia.exceptions.CleanException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class GenerateHelperTaskTest {
  private static final String TEST_DIR = getTestDir(GenerateHelperTaskTest.class);
  private static GenerateHelperTask task;

  @BeforeAll
  public static void setup() throws IOException, CleanException {
    deleteStructure(Path.of(TEST_DIR));
    Project project = setupProject(GenerateHelperTaskTest.class, GenerateStructureTask.class);

    GenerateStructureTask taskStructure = getTask(project, GenerateStructureTask.class);
    taskStructure.setType(GenerateStructureTask.ProjectType.REACTIVE);
    taskStructure.setLanguage(GenerateStructureTask.Language.JAVA);
    taskStructure.execute();

    ProjectBuilder.builder()
        .withName("app-service")
        .withProjectDir(new File(TEST_DIR + "/applications/app-service"))
        .withParent(project)
        .build();

    task = createTask(project, GenerateHelperTask.class);
  }

  @AfterAll
  public static void tearDown() {
    deleteStructure(Path.of(TEST_DIR));
  }

  // Assert
  @Test
  void shouldHandleErrorWhenNoName() {

    // Act
    assertThrows(IllegalArgumentException.class, () -> task.execute());
  }

  // Assert
  @Test
  void shouldHandleErrorWhenEmptyName() {
    // Arrange
    task.setName("");
    // Act
    assertThrows(IllegalArgumentException.class, () -> task.execute());
  }

  @Test
  void generateHelperGeneric() throws IOException, CleanException {
    // Arrange
    task.setName("MyHelper");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/helpers/my-helper/",
        "build.gradle",
        "src/main/java/co/com/bancolombia/myhelper",
        "src/test/java/co/com/bancolombia/myhelper");
  }
}
