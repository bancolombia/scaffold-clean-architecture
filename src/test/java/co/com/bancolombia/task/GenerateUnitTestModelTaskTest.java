package co.com.bancolombia.task;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class GenerateUnitTestModelTaskTest {

    @Test(expected = IllegalArgumentException.class)
    public void generateUnitTestModelValueUnExistent() throws IOException {
        Project project = ProjectBuilder.builder().build();
        project.getTasks().create("test", GenerateUnitTestModelTask.class);

        GenerateUnitTestModelTask task = (GenerateUnitTestModelTask) project.getTasks().getByName("test");

        task.generateUnitTestModelTask();
    }

    @Test
    public void generateUnitTestBasic() throws IOException {
        Project project = ProjectBuilder.builder().withProjectDir(new File("build/unitTest")).build();
        project.getTasks().create("test", GenerateUnitTestModelTask.class);

        GenerateUnitTestModelTask task = (GenerateUnitTestModelTask) project.getTasks().getByName("test");

        task.setNameProject("testModel");
        task.generateUnitTestModelTask();
        assertTrue(new File("build/unitTest/domain/model/src/test/java/co/com/bancolombia/model/testModel/TestModelTest.java").exists());

    }

    @Test
    public void generateUnitTestModelWithPackage() throws IOException {
        Project project = ProjectBuilder.builder().withProjectDir(new File("build/unitTest")).build();
        project.getTasks().create("test", GenerateUnitTestModelTask.class);

        GenerateUnitTestModelTask task = (GenerateUnitTestModelTask) project.getTasks().getByName("test");

        task.setNameProject("testModel");
        task.setPackageProject("portfolio.details");
        task.generateUnitTestModelTask();
        assertTrue(new File("build/unitTest/domain/model/src/test/java/co/com/bancolombia/model/portfolio/details/TestModelTest.java").exists());

    }

}
