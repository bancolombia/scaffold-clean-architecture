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
import org.junit.jupiter.api.BeforeEach;
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

  @BeforeEach
  void resetTaskOptions() {
    task.getOtel().set(false);
    task.setMonoRepo(AbstractCleanArchitectureDefaultTask.BooleanOption.FALSE);
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
        "SonarQubePrepare@");
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
    assertFileContains(TEST_DIR + "/.circleci/config.yml", "cimg/openjdk");
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
    assertTrue(workflow.contains("actions/checkout@"));
    assertTrue(workflow.contains("actions/setup-java@"));
    assertFalse(workflow.matches("(?s).*uses: actions/[\\w-]+@v\\d.*"));
  }

  @Test
  void generatePipelineWithoutType() {
    // Arrange
    task.setType(null);
    // Act - Assert
    assertThrows(IllegalArgumentException.class, () -> task.execute());
  }

  private static String generated(String path) throws IOException {
    return Files.readString(Path.of(TEST_DIR, path));
  }

  @Test
  void pipelinesPublishTheLibsFolderThatHoldsTheJarAndTheOtelAgent()
      throws IOException, CleanException {
    task.getOtel().set(true);
    for (String type : new String[] {"AZURE", "CIRCLECI", "JENKINS"}) {
      task.setType(type);
      task.execute();
    }

    String azure = generated("deployment/cleanarchitecture_azure_build.yaml");
    String circleCi = generated(".circleci/config.yml");
    String jenkins = generated("deployment/Jenkinsfile");

    assertTrue(
        azure.contains(
            "PathtoPublish: '$(System.DefaultWorkingDirectory)/applications/app-service/build/libs/'"));
    assertTrue(circleCi.contains("path: applications/app-service/build/libs/"));
    assertTrue(
        jenkins.contains(
            "archiveArtifacts artifacts: 'applications/app-service/build/libs/*.jar'"));
    for (String pipeline : new String[] {azure, circleCi, jenkins}) {
      assertFalse(pipeline.contains("otel"));
    }
  }

  @Test
  void githubWorkflowUploadsTheLibsFolderOnlyWhenOtelIsEnabled()
      throws IOException, CleanException {
    String path = ".github/workflows/cleanarchitecture_github_action_gradle.yaml";
    task.setType("GITHUB");
    task.execute();

    assertFalse(generated(path).contains("actions/upload-artifact@"));

    task.getOtel().set(true);
    task.execute();
    String workflow = generated(path);

    assertTrue(workflow.contains("actions/upload-artifact@"));
    assertTrue(workflow.contains("path: applications/app-service/build/libs/*.jar"));
    assertFalse(workflow.contains("otel"));
  }
}
