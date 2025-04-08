package co.com.bancolombia.task;

import static co.com.bancolombia.TestUtils.createTask;
import static co.com.bancolombia.TestUtils.deleteStructure;
import static co.com.bancolombia.TestUtils.getTask;
import static co.com.bancolombia.TestUtils.getTestDir;
import static co.com.bancolombia.TestUtils.setupProject;
import static org.junit.jupiter.api.Assertions.assertNull;

import co.com.bancolombia.exceptions.CleanException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class UpdateProjectTaskTest {
  private static final String TEST_DIR = getTestDir(UpdateProjectTaskTest.class);
  private static UpdateProjectTask task;

  @BeforeAll
  static void setup() throws IOException, CleanException {
    deleteStructure(Path.of(TEST_DIR));
    Project project = setupProject(UpdateProjectTaskTest.class, GenerateStructureTask.class);

    GenerateStructureTask taskStructure = getTask(project, GenerateStructureTask.class);
    taskStructure.setType(GenerateStructureTask.ProjectType.REACTIVE);
    taskStructure.execute();

    ProjectBuilder.builder()
        .withName("app-service")
        .withProjectDir(new File(TEST_DIR + "/applications/app-service"))
        .withParent(project)
        .build();

    task = createTask(project, UpdateProjectTask.class);
    task.setGit(AbstractCleanArchitectureDefaultTask.BooleanOption.FALSE);
  }

  @AfterAll
  static void tearDown() {
    deleteStructure(Path.of(TEST_DIR));
  }

  @Test
  void shouldUpdateProject() throws IOException, CleanException {
    // Arrange
    // Act
    task.execute();
    // Assert
    assertNull(task.getState().getOutcome());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "org.mockito:mockito-core org.projectlombok:lombok",
        "does_dependency:not_exist",
        "does_dependency"
      })
  void shouldUpdateProjectAndSomeDependencies(String dependency)
      throws CleanException, IOException {
    // Arrange
    task.setDependencies(dependency);
    // Act
    task.execute();
    // Assert
    assertNull(task.getState().getOutcome());
  }
}
