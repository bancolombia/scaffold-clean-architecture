package co.com.bancolombia.task;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.TestUtils.createTask;
import static co.com.bancolombia.TestUtils.deleteStructure;
import static co.com.bancolombia.TestUtils.getTask;
import static co.com.bancolombia.TestUtils.getTestDir;
import static co.com.bancolombia.TestUtils.runCleanTask;
import static co.com.bancolombia.TestUtils.setupProject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.com.bancolombia.exceptions.CleanException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ValidateStructureTaskTest {
  private static final String TEST_DIR = getTestDir(ValidateStructureTaskTest.class);
  private ValidateStructureTask task;

  @BeforeEach
  void setup() {
    deleteStructure(Path.of(TEST_DIR));
  }

  @AfterAll
  static void tearDown() {
    deleteStructure(Path.of(TEST_DIR));
  }

  public void setupException() throws IOException, CleanException {
    Project project = setupProject(ValidateStructureTaskTest.class, GenerateStructureTask.class);
    project.getPluginManager().apply(JavaPlugin.class);

    GenerateStructureTask generateStructureTask = getTask(project, GenerateStructureTask.class);
    generateStructureTask.execute();

    ProjectBuilder.builder()
        .withName(APP_SERVICE)
        .withProjectDir(new File(TEST_DIR + "/applications/app-service"))
        .withParent(project)
        .build();

    GenerateUseCaseTask generateUseCase = createTask(project, GenerateUseCaseTask.class);
    generateUseCase.setName("business");
    generateUseCase.execute();

    Project useCaseProject =
        ProjectBuilder.builder()
            .withName("usecase")
            .withProjectDir(new File(TEST_DIR + "/domain/usecase"))
            .withParent(project)
            .build();
    useCaseProject.getPluginManager().apply(JavaPlugin.class);

    Project modelProject =
        ProjectBuilder.builder()
            .withName("model")
            .withProjectDir(new File(TEST_DIR + "/domain/model"))
            .withParent(project)
            .build();

    modelProject.getPluginManager().apply(JavaPlugin.class);
    runCleanTask(modelProject);

    assertTrue(new File(TEST_DIR + "/domain/usecase/build.gradle").exists());

    task = createTask(project, ValidateStructureTask.class);
  }

  public void setupWithoutModelWhitelistDepException() throws IOException, CleanException {
    Project project = setupProject(ValidateStructureTaskTest.class, GenerateStructureTask.class);

    project.getPluginManager().apply(JavaPlugin.class);

    GenerateStructureTask generateStructureTask = getTask(project, GenerateStructureTask.class);
    generateStructureTask.execute();

    ProjectBuilder.builder()
        .withName(APP_SERVICE)
        .withProjectDir(new File(TEST_DIR + "/applications/app-service"))
        .withParent(project)
        .build();

    GenerateUseCaseTask generateUseCase = createTask(project, GenerateUseCaseTask.class);
    generateUseCase.setName("business");
    generateUseCase.execute();

    Project modelProject =
        ProjectBuilder.builder()
            .withName("model")
            .withProjectDir(new File(TEST_DIR + "/domain/model"))
            .withParent(project)
            .build();

    modelProject.getPluginManager().apply(JavaPlugin.class);

    // adding a dependency to the model module, without a whitelist, this would trigger a failure
    modelProject.getDependencies().add("implementation", "org.apache.commons:commons-text:1.10.0");

    runCleanTask(modelProject);

    assertTrue(new File(TEST_DIR + "/domain/usecase/build.gradle").exists());

    task = createTask(project, ValidateStructureTask.class);
  }

  private void prepareImperativeProject() throws IOException, CleanException {
    Project project = setupProject(ValidateStructureTaskTest.class, GenerateStructureTask.class);

    project.getPluginManager().apply(JavaPlugin.class);

    GenerateStructureTask generateStructureTask = getTask(project, GenerateStructureTask.class);
    generateStructureTask.execute();

    ProjectBuilder.builder()
        .withName(APP_SERVICE)
        .withProjectDir(new File(TEST_DIR + "/applications/app-service"))
        .withParent(project)
        .build();

    GenerateDrivenAdapterTask generateDriven = createTask(project, GenerateDrivenAdapterTask.class);
    generateDriven.setType("MONGODB");
    generateDriven.execute();

    GenerateUseCaseTask generateUseCase = createTask(project, GenerateUseCaseTask.class);
    generateUseCase.setName("business");
    generateUseCase.execute();

    Project mongoProject =
        ProjectBuilder.builder()
            .withName("mongo-repository")
            .withProjectDir(new File(TEST_DIR + "/infrastructure/driven-adapters/mongo-repository"))
            .withParent(project)
            .build();

    Project modelProject =
        ProjectBuilder.builder()
            .withName("model")
            .withProjectDir(new File(TEST_DIR + "/domain/model"))
            .withParent(project)
            .build();

    mongoProject.getPluginManager().apply(JavaPlugin.class);
    mongoProject.getDependencies().add("implementation", "org.apache.commons:commons-text:1.10.0");

    modelProject.getPluginManager().apply(JavaPlugin.class);
    runCleanTask(modelProject);

    assertTrue(
        new File(TEST_DIR + "/infrastructure/driven-adapters/mongo-repository/build.gradle")
            .exists());

    project.getTasks().register("validate", ValidateStructureTask.class);
    task = (ValidateStructureTask) project.getTasks().getByName("validate");
  }

  private void prepareReactiveProject() throws IOException, CleanException {
    Project project = setupProject(ValidateStructureTaskTest.class, GenerateStructureTask.class);

    project.getPluginManager().apply(JavaPlugin.class);

    GenerateStructureTask generateStructureTask = getTask(project, GenerateStructureTask.class);
    generateStructureTask.setType(GenerateStructureTask.ProjectType.REACTIVE);
    generateStructureTask.execute();

    ProjectBuilder.builder()
        .withName(APP_SERVICE)
        .withProjectDir(new File(TEST_DIR + "/applications/app-service"))
        .withParent(project)
        .build();

    GenerateDrivenAdapterTask generateDriven = createTask(project, GenerateDrivenAdapterTask.class);
    generateDriven.setType("MONGODB");
    generateDriven.execute();

    GenerateUseCaseTask generateUseCase = createTask(project, GenerateUseCaseTask.class);
    generateUseCase.setName("business");
    generateUseCase.execute();

    Project mongoProject =
        ProjectBuilder.builder()
            .withName("mongo-repository")
            .withProjectDir(new File(TEST_DIR + "/infrastructure/driven-adapters/mongo-repository"))
            .withParent(project)
            .build();

    Project modelProject =
        ProjectBuilder.builder()
            .withName("model")
            .withProjectDir(new File(TEST_DIR + "/domain/model"))
            .withParent(project)
            .build();

    mongoProject
        .getConfigurations()
        .create("capsule")
        .defaultDependencies(
            dependencySet ->
                dependencySet.add(
                    project.getDependencies().create("co.paralleluniverse:capsule:1.0.3")));
    mongoProject.getPluginManager().apply(JavaPlugin.class);

    modelProject.getPluginManager().apply(JavaPlugin.class);
    runCleanTask(modelProject);

    assertTrue(
        new File(TEST_DIR + "/infrastructure/driven-adapters/mongo-repository/build.gradle")
            .exists());

    task = createTask(project, ValidateStructureTask.class);
  }

  @Test
  void validateStructureException() throws IOException, CleanException {
    // Act
    this.setupException();
    // Assert
    assertThrows(CleanException.class, () -> task.execute());
  }

  @Test
  void validateStructureModelException() throws IOException, CleanException {
    // Act
    this.setupWithoutModelWhitelistDepException();
    // Assert
    assertThrows(CleanException.class, () -> task.execute());
  }

  @Test
  void validateStructureImperativeProject() throws IOException, CleanException {
    // Act
    this.prepareImperativeProject();
    task.execute();
    // Assert
  }

  @Test
  void validateStructureReactiveProject() throws IOException, CleanException {
    // Act
    this.prepareReactiveProject();
    task.execute();
    // Assert
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void validateStructureIgnoresPlatformsInSubprojects(boolean enforced)
      throws IOException, CleanException {
    setupException();
    var project = task.getProject();
    project
        .project(":usecase")
        .getDependencies()
        .add("implementation", project.getDependencies().project(Map.of("path", ":model")));
    project.subprojects(
        subproject -> {
          subproject.getPluginManager().apply(JavaPlugin.class);
          var dependencies = subproject.getDependencies();
          var platform =
              enforced
                  ? dependencies.enforcedPlatform("some:dep:1.2.3")
                  : dependencies.platform("some:dep:1.2.3");
          dependencies.add("implementation", platform);
        });

    assertEquals(Set.of(), task.getModuleDependencies().get().get("model"));
    assertEquals(Set.of("model"), task.getModuleDependencies().get().get("usecase"));
    task.execute();
  }

  @ParameterizedTest
  @CsvSource({"model,false", "model,true", "usecase,false", "usecase,true"})
  void validateStructureRejectsOrdinaryDependencyAlongsidePlatform(String module, boolean enforced)
      throws IOException, CleanException {
    setupException();
    var project = task.getProject();
    var usecase = project.project(":usecase");
    usecase
        .getDependencies()
        .add("implementation", project.getDependencies().project(Map.of("path", ":model")));
    var dependencies = project.project(":" + module).getDependencies();
    var platform =
        enforced
            ? dependencies.enforcedPlatform("some:dep:1.2.3")
            : dependencies.platform("some:dep:1.2.3");
    dependencies.add("implementation", platform);
    dependencies.add("implementation", "some:dep:1.2.3");

    var exception = assertThrows(CleanException.class, () -> task.execute());
    assertEquals(
        module.equals("model") ? "Model module is invalid" : "Use case module is invalid",
        exception.getMessage());
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void validateStructureStillRequiresModelDependencyWithPlatform(boolean enforced)
      throws IOException, CleanException {
    setupException();
    var dependencies = task.getProject().project(":usecase").getDependencies();
    dependencies.add(
        "implementation",
        enforced
            ? dependencies.enforcedPlatform("some:dep:1.2.3")
            : dependencies.platform("some:dep:1.2.3"));

    var exception = assertThrows(CleanException.class, () -> task.execute());
    assertEquals("Use case module is invalid", exception.getMessage());
  }
}
