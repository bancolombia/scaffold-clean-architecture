package co.com.bancolombia.task;

import static co.com.bancolombia.TestUtils.assertFileContains;
import static co.com.bancolombia.TestUtils.assertFilesExistsInDir;
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
import java.nio.file.Files;
import java.nio.file.Path;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class GeneratePipelineTaskTest {
  private static final String TEST_DIR = getTestDir(GeneratePipelineTaskTest.class);
  private static GeneratePipelineTask task;

  @BeforeAll
  static void setup() throws IOException, CleanException {
    deleteStructure(Path.of(TEST_DIR));
    Project project = setupProject(GeneratePipelineTaskTest.class, GenerateStructureTask.class);

    GenerateStructureTask taskStructure = getTask(project, GenerateStructureTask.class);
    taskStructure.setType(GenerateStructureTask.ProjectType.REACTIVE);
    taskStructure.execute();

    ProjectBuilder.builder()
        .withName("app-service")
        .withProjectDir(new File(TEST_DIR + "/applications/app-service"))
        .withParent(project)
        .build();

    task = createTask(project, GeneratePipelineTask.class);
  }

  @AfterAll
  static void tearDown() {
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
        "sonar.projectKey=$(Build.Repository.Name)",
        "SonarQubePrepare@8");
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
    String jenkinsfile = Files.readString(Path.of(TEST_DIR, "deployment", "Jenkinsfile"));
    assertTrue(jenkinsfile.contains("SONAR_TOKEN=\"$SONAR_AUTH_TOKEN\" ./gradlew sonar "));
    assertFalse(jenkinsfile.contains("sonar.login"));
    assertFalse(jenkinsfile.contains("gradlew sonarqube"));
  }

  @Test
  void generateCircleCIPipelineTest() throws IOException, CleanException {
    // Arrange
    task.setType("CIRCLECI");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(TEST_DIR + "/.circleci/", "config.yml");
    assertFileContains(TEST_DIR + "/.circleci/config.yml", "cimg/openjdk:25.0");
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
    String workflow =
        Files.readString(
            Path.of(
                TEST_DIR, ".github", "workflows", "cleanarchitecture_github_action_gradle.yaml"));
    assertTrue(workflow.contains("actions/checkout@3d3c42e5aac5ba805825da76410c181273ba90b1"));
    assertTrue(workflow.contains("actions/setup-java@77dd5b2ce225ea11469928ac317850245a74ae78"));
    assertFalse(workflow.matches("(?s).*uses: actions/[\\w-]+@v\\d.*"));
  }

  @Test
  void generatePipelineWithoutType() {
    // Arrange
    task.setType(null);
    // Act - Assert
    assertThrows(IllegalArgumentException.class, () -> task.execute());
  }
}
