package co.com.bancolombia.task;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.TestUtils.assertFileContains;
import static co.com.bancolombia.TestUtils.createTask;
import static co.com.bancolombia.TestUtils.deleteStructure;
import static co.com.bancolombia.TestUtils.getTask;
import static co.com.bancolombia.TestUtils.getTestDir;
import static co.com.bancolombia.TestUtils.setupProject;
import static org.junit.jupiter.api.Assertions.assertEquals;

import co.com.bancolombia.exceptions.CleanException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class AnalyticsTaskTest {
  private static final String TEST_DIR = getTestDir(AnalyticsTaskTest.class);
  private static AnalyticsTask task;

  @BeforeAll
  static void setup() throws IOException, CleanException {
    deleteStructure(Path.of(TEST_DIR));
    Project project = setupProject(AnalyticsTaskTest.class, GenerateStructureTask.class);
    GenerateStructureTask generateStructureTask = getTask(project, GenerateStructureTask.class);
    generateStructureTask.execute();

    ProjectBuilder.builder()
        .withName(APP_SERVICE)
        .withProjectDir(new File(TEST_DIR + "/applications/app-service"))
        .withParent(project)
        .build();

    task = createTask(project, AnalyticsTask.class);
  }

  @AfterAll
  static void tearDown() {
    deleteStructure(Path.of(TEST_DIR));
  }

  @Test
  void shouldEnableAnalytics() throws IOException, CleanException {
    // Arrange
    task.setAnalyticsState(AbstractCleanArchitectureDefaultTask.BooleanOption.TRUE);
    // Act
    task.execute();
    // Assert
    assertFileContains(TEST_DIR + "/gradle.properties", "analytics=true");
  }

  @Test
  void shouldDisableAnalytics() throws IOException, CleanException {
    // Arrange
    task.setAnalyticsState(AbstractCleanArchitectureDefaultTask.BooleanOption.FALSE);
    // Act
    task.execute();
    // Assert
    assertFileContains(TEST_DIR + "/gradle.properties", "analytics=false");
  }

  @Test
  void shouldGetOptions() {
    // Arrange
    // Act
    List<AbstractCleanArchitectureDefaultTask.BooleanOption> options = task.getInputOptions();
    // Assert
    assertEquals(2, options.size());
  }
}
