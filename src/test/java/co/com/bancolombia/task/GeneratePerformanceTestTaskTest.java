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
import org.junit.jupiter.api.*;

class GeneratePerformanceTestTaskTest {
  private static final String TEST_DIR = getTestDir(GeneratePerformanceTestTaskTest.class);
  private static Project project;

  private static GeneratePerformanceTestTask task;

  @BeforeEach
  public void setup() throws IOException, CleanException {
    deleteStructure(Path.of(TEST_DIR));
    project = setupProject(GeneratePerformanceTestTaskTest.class, GenerateStructureTask.class);

    GenerateStructureTask taskStructure = getTask(project, GenerateStructureTask.class);
    taskStructure.setType(GenerateStructureTask.ProjectType.REACTIVE);
    taskStructure.execute();

    ProjectBuilder.builder()
        .withName("app-service")
        .withProjectDir(new File(TEST_DIR + "/applications/app-service"))
        .withParent(project)
        .build();

    task = createTask(project, GeneratePerformanceTestTask.class);
  }

  @AfterEach
  public void tearDown() {
    deleteStructure(Path.of(TEST_DIR));
  }

  @Test
  void generatePerformanceTest() throws IOException, CleanException {
    task.setType("JMETER");
    task.execute();
    assertFilesExistsInDir(TEST_DIR + "/performance-test/", "Jmeter", "README.md");
  }

  @Test
  void generateApiPerformanceTest() throws IOException, CleanException {
    // Arrange
    ProjectBuilder.builder()
        .withName("reactive-web")
        .withProjectDir(new File(TEST_DIR + "/infrastructure/entry-points/reactive-web"))
        .withParent(project)
        .build();

    task.setType("JMETER");

    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/deployment/performance-test/Jmeter/Api/",
        "README.md",
        "SC_Template_UDP_Service.jmx",
        "SC_Template_HTTP_Service.jmx");
  }
}
