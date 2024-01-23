package co.com.bancolombia.task;

import static co.com.bancolombia.TestUtils.assertFileContains;
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

public class GeneratePipelineTaskTest {
  private static final String TEST_DIR = getTestDir(GeneratePipelineTaskTest.class);
  private static GeneratePipelineTask task;

  @BeforeAll
  public static void setup() throws IOException, CleanException {
    deleteStructure(Path.of(TEST_DIR));
    Project project = setupProject(GeneratePipelineTaskTest.class, GenerateStructureTask.class);

    GenerateStructureTask taskStructure = getTask(project, GenerateStructureTask.class);
    taskStructure.setType(GenerateStructureTask.ProjectType.REACTIVE);
    taskStructure.setLanguage(GenerateStructureTask.Language.JAVA);
    taskStructure.execute();

    ProjectBuilder.builder()
        .withName("app-service")
        .withProjectDir(new File(TEST_DIR + "/applications/app-service"))
        .withParent(project)
        .build();

    task = createTask(project, GeneratePipelineTask.class);
  }

  @AfterAll
  public static void tearDown() {
    deleteStructure(Path.of(TEST_DIR));
  }

  @Test
  void generateAzureDevOpsPipelineTest() throws IOException, CleanException {
    // Arrange
    task.setType("AZURE");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(TEST_DIR + "/deployment/", "cleanarchitecture_azure_build.yaml");
    assertFileContains(
        TEST_DIR + "/deployment/cleanarchitecture_azure_build.yaml",
        "sonar.projectKey=$(Build.Repository.Name)");
  }

  @Test
  void generateAzureDevOpsPipelineInMonoRepoTest() throws IOException, CleanException {
    // Arrange
    task.setType("AZURE");
    task.setMonoRepo(AbstractCleanArchitectureDefaultTask.BooleanOption.TRUE);
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(TEST_DIR + "/deployment/", "cleanarchitecture_azure_build.yaml");
    assertFileContains(
        TEST_DIR + "/deployment/cleanarchitecture_azure_build.yaml",
        "sonar.projectKey=$(Build.Repository.Name)_$(projectName)");
  }

  @Test
  void generateJenkinsPipelineTest() throws IOException, CleanException {
    // Arrange
    task.setType("JENKINS");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(TEST_DIR + "/deployment/", "Jenkinsfile");
  }

  @Test
  void generateCircleCIPipelineTest() throws IOException, CleanException {
    // Arrange
    task.setType("CIRCLECI");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(TEST_DIR + "/.circleci/", "config.yml");
  }

  @Test
  void generateGithubActionTest() throws IOException, CleanException {
    // Arrange
    task.setType("GITHUB");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/.github/workflows/", "cleanarchitecture_github_action_gradle.yaml");
  }

  @Test
  void generatePipelineWithoutType() {
    // Arrange
    task.setType(null);
    // Act - Assert
    assertThrows(IllegalArgumentException.class, () -> task.execute());
  }
}
