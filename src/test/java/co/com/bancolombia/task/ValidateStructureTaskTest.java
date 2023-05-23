package co.com.bancolombia.task;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.FileUtilsTest.deleteStructure;
import static org.junit.Assert.*;

import co.com.bancolombia.exceptions.CleanException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ValidateStructureTaskTest {

  ValidateStructureTask task;

  public void setupException() throws IOException, CleanException {
    deleteStructure(Path.of("build/unitTest"));
    Project project =
        ProjectBuilder.builder()
            .withName("cleanArchitecture")
            .withProjectDir(new File("build/unitTest"))
            .build();

    project.getPluginManager().apply(JavaPlugin.class);

    project.getTasks().create("ca", GenerateStructureTask.class);
    GenerateStructureTask generateStructureTask =
        (GenerateStructureTask) project.getTasks().getByName("ca");
    generateStructureTask.generateStructureTask();

    ProjectBuilder.builder()
        .withName(APP_SERVICE)
        .withProjectDir(new File("build/unitTest/applications/app-service"))
        .withParent(project)
        .build();

    project.getTasks().create("guc", GenerateUseCaseTask.class);
    GenerateUseCaseTask generateUseCase = (GenerateUseCaseTask) project.getTasks().getByName("guc");
    generateUseCase.setName("business");
    generateUseCase.generateUseCaseTask();

    Project useCaseProject =
        ProjectBuilder.builder()
            .withName("usecase")
            .withProjectDir(new File("build/unitTest/domain/usecase"))
            .withParent(project)
            .build();
    useCaseProject.getPluginManager().apply(JavaPlugin.class);

    Project modelProject =
        ProjectBuilder.builder()
            .withName("model")
            .withProjectDir(new File("build/unitTest/domain/model"))
            .withParent(project)
            .build();

    modelProject.getPluginManager().apply(JavaPlugin.class);
    Task task2 = modelProject.getTasks().getByName("clean");
    task2.getActions().get(0).execute(task2);

    assertTrue(new File("build/unitTest/domain/usecase/build.gradle").exists());

    project.getTasks().create("validate", ValidateStructureTask.class);
    task = (ValidateStructureTask) project.getTasks().getByName("validate");
  }

  public void setupWithoutModelWhitelistDepException() throws IOException, CleanException {
    deleteStructure(Path.of("build/unitTest"));
    Project project =
        ProjectBuilder.builder()
            .withName("cleanArchitecture")
            .withProjectDir(new File("build/unitTest"))
            .build();

    project.getPluginManager().apply(JavaPlugin.class);

    project.getTasks().create("ca", GenerateStructureTask.class);
    GenerateStructureTask generateStructureTask =
        (GenerateStructureTask) project.getTasks().getByName("ca");
    generateStructureTask.generateStructureTask();

    ProjectBuilder.builder()
        .withName(APP_SERVICE)
        .withProjectDir(new File("build/unitTest/applications/app-service"))
        .withParent(project)
        .build();

    project.getTasks().create("guc", GenerateUseCaseTask.class);
    GenerateUseCaseTask generateUseCase = (GenerateUseCaseTask) project.getTasks().getByName("guc");
    generateUseCase.setName("business");
    generateUseCase.generateUseCaseTask();

    Project modelProject =
        ProjectBuilder.builder()
            .withName("model")
            .withProjectDir(new File("build/unitTest/domain/model"))
            .withParent(project)
            .build();

    modelProject.getPluginManager().apply(JavaPlugin.class);

    // adding a dependency to the model module, without a whitelist, this would trigger a failure
    modelProject.getDependencies().add("implementation", "org.apache.commons:commons-text:1.10.0");

    Task task2 = modelProject.getTasks().getByName("clean");
    task2.getActions().get(0).execute(task2);

    assertTrue(new File("build/unitTest/domain/usecase/build.gradle").exists());

    project.getTasks().create("validate", ValidateStructureTask.class);
    task = (ValidateStructureTask) project.getTasks().getByName("validate");
  }

  @Test(expected = CleanException.class)
  public void validateStructureException() throws IOException, CleanException {
    // Act
    this.setupException();
    task.validateStructureTask();
    // Assert
  }

  @Test(expected = CleanException.class)
  public void validateStructureModelException() throws IOException, CleanException {
    // Act
    this.setupWithoutModelWhitelistDepException();
    task.validateStructureTask();
    // Assert
  }

  private void prepareImperativeProject() throws IOException, CleanException {
    Project project =
        ProjectBuilder.builder()
            .withName("cleanArchitecture")
            .withProjectDir(new File("build/unitTest"))
            .build();

    project.getPluginManager().apply(JavaPlugin.class);

    project.getTasks().create("ca", GenerateStructureTask.class);
    GenerateStructureTask generateStructureTask =
        (GenerateStructureTask) project.getTasks().getByName("ca");
    generateStructureTask.generateStructureTask();

    ProjectBuilder.builder()
        .withName(APP_SERVICE)
        .withProjectDir(new File("build/unitTest/applications/app-service"))
        .withParent(project)
        .build();

    project.getTasks().create("gda", GenerateDrivenAdapterTask.class);
    GenerateDrivenAdapterTask generateDriven =
        (GenerateDrivenAdapterTask) project.getTasks().getByName("gda");
    generateDriven.setType("MONGODB");
    generateDriven.generateDrivenAdapterTask();

    project.getTasks().create("guc", GenerateUseCaseTask.class);
    GenerateUseCaseTask generateUseCase = (GenerateUseCaseTask) project.getTasks().getByName("guc");
    generateUseCase.setName("business");
    generateUseCase.generateUseCaseTask();

    Project mongoProject =
        ProjectBuilder.builder()
            .withName("mongo-repository")
            .withProjectDir(
                new File("build/unitTest/infrastructure/driven-adapters/mongo-repository"))
            .withParent(project)
            .build();

    Project modelProject =
        ProjectBuilder.builder()
            .withName("model")
            .withProjectDir(new File("build/unitTest/domain/model"))
            .withParent(project)
            .build();

    mongoProject.getPluginManager().apply(JavaPlugin.class);
    mongoProject.getDependencies().add("implementation", "org.apache.commons:commons-text:1.10.0");

    modelProject.getPluginManager().apply(JavaPlugin.class);
    Task task2 = modelProject.getTasks().getByName("clean");
    task2.getActions().get(0).execute(task2);

    assertTrue(
        new File("build/unitTest/infrastructure/driven-adapters/mongo-repository/build.gradle")
            .exists());

    project.getTasks().create("validate", ValidateStructureTask.class);
    task = (ValidateStructureTask) project.getTasks().getByName("validate");
  }

  private void prepareReactiveProject() throws IOException, CleanException {
    Project project =
        ProjectBuilder.builder()
            .withName("cleanArchitecture")
            .withProjectDir(new File("build/unitTest"))
            .build();

    project.getPluginManager().apply(JavaPlugin.class);

    project.getTasks().create("ca", GenerateStructureTask.class);
    GenerateStructureTask generateStructureTask =
        (GenerateStructureTask) project.getTasks().getByName("ca");
    generateStructureTask.setType(GenerateStructureTask.ProjectType.REACTIVE);
    generateStructureTask.generateStructureTask();

    ProjectBuilder.builder()
        .withName(APP_SERVICE)
        .withProjectDir(new File("build/unitTest/applications/app-service"))
        .withParent(project)
        .build();

    project.getTasks().create("gda", GenerateDrivenAdapterTask.class);
    GenerateDrivenAdapterTask generateDriven =
        (GenerateDrivenAdapterTask) project.getTasks().getByName("gda");
    generateDriven.setType("MONGODB");
    generateDriven.generateDrivenAdapterTask();

    project.getTasks().create("guc", GenerateUseCaseTask.class);
    GenerateUseCaseTask generateUseCase = (GenerateUseCaseTask) project.getTasks().getByName("guc");
    generateUseCase.setName("business");
    generateUseCase.generateUseCaseTask();

    Project mongoProject =
        ProjectBuilder.builder()
            .withName("mongo-repository")
            .withProjectDir(
                new File("build/unitTest/infrastructure/driven-adapters/mongo-repository"))
            .withParent(project)
            .build();

    Project modelProject =
        ProjectBuilder.builder()
            .withName("model")
            .withProjectDir(new File("build/unitTest/domain/model"))
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
    Task task2 = modelProject.getTasks().getByName("clean");
    task2.getActions().get(0).execute(task2);

    assertTrue(
        new File("build/unitTest/infrastructure/driven-adapters/mongo-repository/build.gradle")
            .exists());

    project.getTasks().create("validate", ValidateStructureTask.class);
    task = (ValidateStructureTask) project.getTasks().getByName("validate");
  }

  @Test
  public void validateStructureImperativeProject() throws IOException, CleanException {
    // Act
    this.prepareImperativeProject();
    task.validateStructureTask();
    // Assert
  }

  @Test
  public void validateStructureReactiveProject() throws IOException, CleanException {
    // Act
    this.prepareReactiveProject();
    task.validateStructureTask();
    // Assert
  }
}
