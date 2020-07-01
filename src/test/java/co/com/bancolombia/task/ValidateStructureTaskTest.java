package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.adapters.ModuleFactoryDrivenAdapter;
import co.com.bancolombia.task.ValidateStructureTask;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.internal.artifacts.configurations.DefaultConfiguration;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ValidateStructureTaskTest {

    ValidateStructureTask task;

    @Before
    public void setup() throws IOException, CleanException {
        Project project = ProjectBuilder.builder()
                .withName("cleanArchitecture")

                .withProjectDir(new File("build/unitTest"))
                .build();

        project.getPluginManager().apply(JavaPlugin.class);

         project.getTasks().create("ca", GenerateStructureTask.class);
        GenerateStructureTask generateStructureTask = (GenerateStructureTask) project.getTasks().getByName("ca");
        generateStructureTask.generateStructureTask();

        ProjectBuilder.builder()
                .withName("app-service")
                .withProjectDir(new File("build/unitTest/applications/app-service"))
                .withParent(project)
                .build();

        project.getTasks().create("gda", GenerateDrivenAdapterTask.class);
        GenerateDrivenAdapterTask generateDriven = (GenerateDrivenAdapterTask) project.getTasks().getByName("gda");
        generateDriven.setType(ModuleFactoryDrivenAdapter.DrivenAdapterType.MONGODB);
        generateDriven.generateDrivenAdapterTask();

        // TODO: review the unit test
/*        project.getTasks().create("guc", GenerateUseCaseTask.class);
        GenerateUseCaseTask generateUseCase = (GenerateUseCaseTask) project.getTasks().getByName("guc");
        generateUseCase.setName("business");
        generateUseCase.generateUseCaseTask();

        Project useCaseProject = ProjectBuilder.builder()
                .withName("usecase")
                .withProjectDir(new File("build/unitTest/domain/usecase"))
                .withParent(project)
                .build();
                      useCaseProject.getPluginManager().apply(JavaPlugin.class);

                */

       Project mongoProject = ProjectBuilder.builder()
                .withName("mongo-repository")
                .withProjectDir(new File("build/unitTest/infrastructure/driven-adapters/mongo-repository"))
                .withParent(project)
                .build();

        Project modelProject = ProjectBuilder.builder()
                .withName("model")
                .withProjectDir(new File("build/unitTest/domain/model"))
                .withParent(project)
                .build();
        mongoProject.getConfigurations().create("capsule").defaultDependencies(dependencySet -> {
            dependencySet.add(project.getDependencies().create("co.paralleluniverse:capsule:1.0.3"));
        });
        mongoProject.getPluginManager().apply(JavaPlugin.class);

        modelProject.getPluginManager().apply(JavaPlugin.class);
        Task task2 =  modelProject.getTasks().getByName("clean");
        task2.getActions().get(0).execute(task2);


        assertTrue(new File("build/unitTest/infrastructure/driven-adapters/mongo-repository/build.gradle").exists());

        project.getTasks().create("validate", ValidateStructureTask.class);
        task = (ValidateStructureTask) project.getTasks().getByName("validate");
    }


    @Test
    public void validateStructure() throws IOException, CleanException {
        // Act
        task.validateStructureTask();
        // Assert
    }




}
