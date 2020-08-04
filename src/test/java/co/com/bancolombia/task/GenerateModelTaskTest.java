package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.exceptions.ParamNotFoundException;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;


public class GenerateModelTaskTest {


    @Test(expected = IllegalArgumentException.class)
    public void generateEntryPointValueUnExistent() throws IOException, CleanException {
        Project project = ProjectBuilder.builder().build();
        project.getTasks().create("test", GenerateModelTask.class);
        project.getTasks().create("ca", GenerateStructureTask.class);
        GenerateStructureTask caTask = (GenerateStructureTask) project.getTasks().getByName("ca");
        caTask.generateStructureTask();

        GenerateModelTask task = (GenerateModelTask) project.getTasks().getByName("test");

        task.generateModelTask();
    }

    @Test
    public void generateEntryPoint() throws IOException, ParamNotFoundException {
        Project project = ProjectBuilder.builder().withProjectDir(new File("build/unitTest")).build();
        project.getTasks().create("test", GenerateModelTask.class);

        GenerateModelTask task = (GenerateModelTask) project.getTasks().getByName("test");

        task.setName("testModel");
        task.generateModelTask();
        assertTrue(new File("build/unitTest/domain/model/src/main/java/co/com/bancolombia/model/testmodel/gateways/TestModelRepository.java").exists());
        assertTrue(new File("build/unitTest/domain/model/src/main/java/co/com/bancolombia/model/testmodel/TestModel.java").exists());

    }

}
