package co.com.bancolombia.task;

import static co.com.bancolombia.TestUtils.assertFilesExistsInDir;
import static co.com.bancolombia.TestUtils.deleteStructure;
import static co.com.bancolombia.TestUtils.getTask;
import static co.com.bancolombia.TestUtils.getTestDir;
import static co.com.bancolombia.TestUtils.setupProject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.task.AbstractCleanArchitectureDefaultTask.BooleanOption;
import co.com.bancolombia.task.GenerateStructureTask.JavaVersion;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.gradle.api.Project;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GenerateStructureTaskTest {
  private static final String TEST_DIR = getTestDir(GenerateStructureTaskTest.class);
  private GenerateStructureTask task;
  private Project project;

  @BeforeEach
  public void setup() {
    deleteStructure(Path.of(TEST_DIR));
    project = setupProject(GenerateStructureTaskTest.class, GenerateStructureTask.class);
    task = getTask(project, GenerateStructureTask.class);
  }

  @AfterAll
  public static void tearDown() {
    deleteStructure(Path.of(TEST_DIR));
  }

  @Test
  void shouldReturnProjectTypes() {
    // Arrange
    // Act
    List<GenerateStructureTask.ProjectType> types = task.getAvailableProjectTypes();
    // Assert
    assertEquals(Arrays.asList(GenerateStructureTask.ProjectType.values()), types);
  }

  @Test
  void shouldReturnMetricsOptions() {
    // Arrange
    // Act
    List<BooleanOption> types = task.getMetricsOptions();
    // Assert
    assertEquals(Arrays.asList(AbstractCleanArchitectureDefaultTask.BooleanOption.values()), types);
  }

  @Test
  void shouldReturnForceOptions() {
    // Arrange
    // Act
    List<BooleanOption> types = task.getForceOptions();
    // Assert
    assertEquals(Arrays.asList(AbstractCleanArchitectureDefaultTask.BooleanOption.values()), types);
  }

  @Test
  void shouldReturnJavaVersion() {
    // Arrange
    // Act
    List<JavaVersion> types = task.getJavaVersions();
    // Assert
    assertEquals(Arrays.asList(JavaVersion.values()), types);
  }

  @Test
  void generateStructure() throws IOException, CleanException {
    // Arrange
    String dir = project.getProjectDir().getPath();
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        dir,
        "README.md",
        ".gitignore",
        "build.gradle",
        "lombok.config",
        "main.gradle",
        "settings.gradle",
        "infrastructure/driven-adapters/",
        "infrastructure/entry-points",
        "infrastructure/helpers",
        "domain/model/src/main/java/co/com/bancolombia/model",
        "domain/model/src/test/java/co/com/bancolombia/model",
        "domain/model/build.gradle",
        "domain/usecase/src/main/java/co/com/bancolombia/usecase",
        "domain/usecase/src/test/java/co/com/bancolombia/usecase",
        "domain/usecase/build.gradle",
        "applications/app-service/build.gradle",
        "applications/app-service/src/main/java/co/com/bancolombia/MainApplication.java",
        "applications/app-service/src/main/java/co/com/bancolombia/config/UseCasesConfig.java",
        "applications/app-service/src/main/java/co/com/bancolombia/config",
        "applications/app-service/src/main/resources/application.yaml",
        "applications/app-service/src/main/resources/log4j2.properties",
        "applications/app-service/src/test/java/co/com/bancolombia");
  }

  @Test
  void generateStructureOnExistingProject() throws IOException, CleanException {
    // Arrange
    String dir = project.getProjectDir().getPath();
    task.execute();
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(dir, "build.gradle", "gradle.properties", "main.gradle");
  }

  @Test
  void generateStructureOnExistingProjectNoLombok() throws IOException, CleanException {
    // Arrange
    String dir = project.getProjectDir().getPath();
    task.setStatusLombok(AbstractCleanArchitectureDefaultTask.BooleanOption.FALSE);
    task.execute();
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(dir, "build.gradle", "gradle.properties", "main.gradle");
    assertFalse(new File(dir + "lombok.config").exists());
  }

  @Test
  void shouldGetLombokOptions() {
    // Arrange
    // Act
    List<BooleanOption> options = task.getLombokOptions();
    // Assert
    assertEquals(2, options.size());
  }
}
