package co.com.bancolombia.task;

import co.com.bancolombia.Utils;
import co.com.bancolombia.templates.ModelTemplate;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class GenerateUnitTestModelTaskTest {

    @Test(expected = IllegalArgumentException.class)
    public void generateUnitTestModelValueUnExistent() throws IOException {
        Project project = ProjectBuilder.builder().build();
        project.getTasks().create("testp", GenerateUnitTestModelTask.class);

        GenerateUnitTestModelTask task = (GenerateUnitTestModelTask) project.getTasks().getByName("testp");

        task.generateUnitTestModelTask();
    }

    @Test
    public void generateUnitTestModelWithoutPackage() throws IOException {
        Project project = ProjectBuilder.builder().withProjectDir(new File("build/unitTest")).build();
        String packageName = Utils.readProperties("package").replaceAll("\\.", "\\/");
        //creo el archivo
        project.mkdir("domain/model/src/main/java/co/com/bancolombia/model/testModel");
        Utils.writeString(project, "domain/model/src/main/java/co/com/bancolombia/model/testModel/TestModel.java", ModelTemplate.getModel("testModel", packageName, ""));

        project.getTasks().create("test", GenerateUnitTestModelTask.class);

        GenerateUnitTestModelTask task = (GenerateUnitTestModelTask) project.getTasks().getByName("test");

        task.setNameProject("testModel");
        task.generateUnitTestModelTask();
        assertTrue(new File("build/unitTest/domain/model/src/test/java/co/com/bancolombia/model/testModel/TestModelTest.java").exists());
    }


    @Test
    public void generateUnitTestModelWithPackage() throws IOException {
        Project project = ProjectBuilder.builder().withProjectDir(new File("build/unitTest")).build();
        String packageName = Utils.readProperties("package").replaceAll("\\.", "\\/");
        //creo el archivo
        project.mkdir("domain/model/src/main/java/co/com/bancolombia/model/portfolio/details");
        Utils.writeString(project, "domain/model/src/main/java/co/com/bancolombia/model/portfolio/details/TestModel.java", ModelTemplate.getModel("testModel", packageName, "portfolio/details"));

        project.getTasks().create("test", GenerateUnitTestModelTask.class);

        GenerateUnitTestModelTask task = (GenerateUnitTestModelTask) project.getTasks().getByName("test");

        task.setNameProject("testModel");
        task.setPackageProject("portfolio.details");
        task.generateUnitTestModelTask();
        assertTrue(new File("build/unitTest/domain/model/src/test/java/co/com/bancolombia/model/portfolio/details/TestModelTest.java").exists());

    }

}
