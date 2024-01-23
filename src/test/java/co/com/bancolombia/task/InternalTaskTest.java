package co.com.bancolombia.task;

import static co.com.bancolombia.TestUtils.createTask;
import static co.com.bancolombia.TestUtils.deleteStructure;
import static co.com.bancolombia.TestUtils.getTask;
import static co.com.bancolombia.TestUtils.getTestDir;
import static co.com.bancolombia.TestUtils.setupProject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.utils.FileUtils;
import co.com.bancolombia.utils.SonarCheck;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class InternalTaskTest {
  private static final String TEST_DIR = getTestDir(InternalTaskTest.class);
  private static InternalTask task;
  private static Project appService;

  @BeforeAll
  public static void setup() throws IOException, CleanException {
    deleteStructure(Path.of(TEST_DIR));
    Project project = setupProject(InternalTaskTest.class, GenerateStructureTask.class);

    GenerateStructureTask taskStructure = getTask(project, GenerateStructureTask.class);
    taskStructure.setType(GenerateStructureTask.ProjectType.REACTIVE);
    taskStructure.setLanguage(GenerateStructureTask.Language.JAVA);
    taskStructure.execute();

    appService =
        ProjectBuilder.builder()
            .withName("app-service")
            .withProjectDir(new File(TEST_DIR + "/applications/app-service"))
            .withParent(project)
            .build();

    task = createTask(project, InternalTask.class);
  }

  @AfterAll
  public static void tearDown() {
    deleteStructure(Path.of(TEST_DIR));
  }

  @Test
  void shouldParseDependencyReport() throws IOException {
    // Arrange
    Files.createDirectories(appService.file(SonarCheck.INPUT).getParentFile().toPath());
    FileUtils.writeString(
        appService, SonarCheck.INPUT, readResourceFile("dependencies-check/report.json"));
    // Act
    task.execute();
    // Assert
    assertTrue(appService.file(SonarCheck.OUTPUT).exists());
  }

  @Test
  void shouldGetOptions() {
    // Arrange
    // Act
    List<InternalTask.Action> options = task.getInputOptions();
    // Assert
    assertEquals(1, options.size());
  }

  private String readResourceFile(String location) throws IOException {
    ClassLoader classLoader = getClass().getClassLoader();
    return Files.readString(
        Path.of(Objects.requireNonNull(classLoader.getResource(location)).getPath()));
  }
}
