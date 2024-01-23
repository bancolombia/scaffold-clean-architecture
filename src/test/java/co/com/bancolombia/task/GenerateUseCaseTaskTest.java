package co.com.bancolombia.task;

import static co.com.bancolombia.TestUtils.assertFilesExistsInDir;
import static co.com.bancolombia.TestUtils.createTask;
import static co.com.bancolombia.TestUtils.deleteStructure;
import static co.com.bancolombia.TestUtils.getTask;
import static co.com.bancolombia.TestUtils.getTestDir;
import static co.com.bancolombia.TestUtils.setupProject;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.exceptions.ParamNotFoundException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class GenerateUseCaseTaskTest {
  private static final String TEST_DIR = getTestDir(GenerateUseCaseTaskTest.class);

  private static GenerateUseCaseTask task;

  @BeforeAll
  public static void setup() throws IOException, CleanException {
    deleteStructure(Path.of(TEST_DIR));
    Project project = setupProject(GenerateUseCaseTaskTest.class, GenerateStructureTask.class);

    GenerateStructureTask taskStructure = getTask(project, GenerateStructureTask.class);
    taskStructure.setType(GenerateStructureTask.ProjectType.REACTIVE);
    taskStructure.setLanguage(GenerateStructureTask.Language.JAVA);
    taskStructure.execute();

    ProjectBuilder.builder()
        .withName("app-service")
        .withProjectDir(new File(TEST_DIR + "/applications/app-service"))
        .withParent(project)
        .build();

    task = createTask(project, GenerateUseCaseTask.class);
  }

  @AfterAll
  public static void tearDown() {
    deleteStructure(Path.of(TEST_DIR));
  }

  @Test
  void generateUseCaseException() {
    // Arrange
    task.setName(null);
    // Act - Assert
    assertThrows(IllegalArgumentException.class, () -> task.execute());
  }

  @Test
  void generateUseCase() throws IOException, ParamNotFoundException {
    // Arrange
    task.setName("business");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/domain/usecase/",
        "src/main/java/co/com/bancolombia/usecase/business/BusinessUseCase.java",
        "build.gradle");
  }

  @Test
  void generateUseCaseWithCorrectName() throws IOException, ParamNotFoundException {
    // Arrange
    task.setName("MyUseCase");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/domain/usecase/",
        "src/main/java/co/com/bancolombia/usecase/my/MyUseCase.java",
        "build.gradle");
  }
}
