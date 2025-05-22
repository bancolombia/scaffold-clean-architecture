package co.com.bancolombia.task;

import static co.com.bancolombia.TestUtils.assertFilesExistsInDir;
import static co.com.bancolombia.TestUtils.createTask;
import static co.com.bancolombia.TestUtils.deleteStructure;
import static co.com.bancolombia.TestUtils.getTask;
import static co.com.bancolombia.TestUtils.getTestDir;
import static co.com.bancolombia.TestUtils.setupProject;

import co.com.bancolombia.exceptions.CleanException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GenerateTestTaskTest {
  private static final String TEST_DIR = getTestDir(GenerateTestTaskTest.class);

  private static GenerateAcceptanceTestTask task;
  private static Project project;

  @BeforeEach
  void setup() throws IOException, CleanException {
    deleteStructure(Path.of(TEST_DIR));
    project = setupProject(GenerateTestTaskTest.class, GenerateStructureTask.class);

    GenerateStructureTask taskStructure = getTask(project, GenerateStructureTask.class);
    taskStructure.setType(GenerateStructureTask.ProjectType.REACTIVE);
    taskStructure.execute();
  }

  @AfterEach
  void tearDown() {
    deleteStructure(Path.of(TEST_DIR));
  }

  @Test
  void generateAcceptanceTest() throws IOException, CleanException {
    // Arrange
    ProjectBuilder.builder()
        .withName("app-service")
        .withProjectDir(new File(TEST_DIR + "/applications/app-service"))
        .withParent(project)
        .build();

    task = createTask(project, GenerateAcceptanceTestTask.class);
    task.setName("acceptance-test");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/deployment/acceptance-test/",
        "src/test/java/co/com/bancolombia/TestParallel.java",
        "src/test/java/co/com/bancolombia/utils/ValidatorTestUtils.java",
        "src/test/resources/logback-test.xml",
        "src/test/resources/karate-config.js",
        "src/test/resources/co/com/bancolombia/demo/demo.feature",
        "src/test/resources/co/com/bancolombia/demo/addPet.json",
        "src/test/resources/co/com/bancolombia/pet-store.yaml",
        "settings.gradle",
        "build.gradle",
        "README.md");
  }

  @Test
  void generateEntryPointAcceptanceTest() throws IOException, CleanException {
    // Arrange
    ProjectBuilder.builder()
        .withName("reactive-web")
        .withProjectDir(new File(TEST_DIR + "/infrastructure/entry-points/reactive-web"))
        .withParent(project)
        .build();

    task = createTask(project, GenerateAcceptanceTestTask.class);
    task.setName("acceptance-test");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/deployment/acceptance-test/",
        "src/test/java/co/com/bancolombia/TestParallel.java",
        "src/test/java/co/com/bancolombia/utils/ValidatorTestUtils.java",
        "src/test/resources/logback-test.xml",
        "src/test/resources/karate-config.js",
        "src/test/resources/co/com/bancolombia/myapp.feature",
        "settings.gradle",
        "build.gradle",
        "README.md");
  }
}
