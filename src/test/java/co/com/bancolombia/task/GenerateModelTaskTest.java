package co.com.bancolombia.task;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;


public class GenerateModelTaskTest {



    @Test(expected = IllegalArgumentException.class)
    public void generateEntryPointValueUnExistent() throws IOException {
        Project project = ProjectBuilder.builder().build();
        project.getTasks().create("test", GenerateModelTask.class);

        GenerateModelTask task = (GenerateModelTask) project.getTasks().getByName("test");

        task.generateModelTask();
    }

    @Test
    public void generateModelBasic() throws IOException {
        Project project = ProjectBuilder.builder().withProjectDir(new File("build/unitTest")).build();
        project.getTasks().create("test", GenerateModelTask.class);

        GenerateModelTask task = (GenerateModelTask) project.getTasks().getByName("test");

        task.setNameProject("testModel");
        task.generateModelTask();
        assertTrue(new File("build/unitTest/domain/model/src/main/java/co/com/bancolombia/model/testModel/gateways/TestModelRepository.java").exists());
        assertTrue(new File("build/unitTest/domain/model/src/main/java/co/com/bancolombia/model/testModel/TestModel.java").exists());

    }

    @Test
    public void generateModelWithPackage() throws IOException {
        Project project = ProjectBuilder.builder().withProjectDir(new File("build/unitTest")).build();
        project.getTasks().create("test", GenerateModelTask.class);

        GenerateModelTask task = (GenerateModelTask) project.getTasks().getByName("test");

        task.setNameProject("testModel");
        task.setPackageProject("portfolio.details");
        task.generateModelTask();
        assertTrue(new File("build/unitTest/domain/model/src/main/java/co/com/bancolombia/model/portfolio/details/gateways/TestModelRepository.java").exists());
        assertTrue(new File("build/unitTest/domain/model/src/main/java/co/com/bancolombia/model/portfolio/details/TestModel.java").exists());

    }

}
