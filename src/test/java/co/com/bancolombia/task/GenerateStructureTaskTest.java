package co.com.bancolombia.task;

import co.com.bancolombia.task.GenerateStructureTask;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class GenerateStructureTaskTest {

    @Test
    public void settersTask() {
        Project project = ProjectBuilder.builder().build();
        project.getTasks().create("test", GenerateStructureTask.class);

        GenerateStructureTask task = (GenerateStructureTask) project.getTasks().getByName("test");

        task.setPackage("test");
        task.setProjectName("projectTest");
        task.setType("reactive");
    }

    @Test
    public void generateStructure() throws IOException {


        Project project = ProjectBuilder.builder().withProjectDir(new File("build/unitTest")).build();
        project.getTasks().create("test", GenerateStructureTask.class);

        GenerateStructureTask task = (GenerateStructureTask) project.getTasks().getByName("test");

        task.setPackage("test");
        task.setProjectName("projectTest");
        task.generateStructure();
    }

    @Test
    public void generateStructureReactive() throws IOException {


        Project project = ProjectBuilder.builder().withProjectDir(new File("build/unitTest")).build();
        project.getTasks().create("test", GenerateStructureTask.class);

        GenerateStructureTask task = (GenerateStructureTask) project.getTasks().getByName("test");

        task.setPackage("test");
        task.setProjectName("projectTest");
        task.setType("reactive");
        task.generateStructure();
    }

}
