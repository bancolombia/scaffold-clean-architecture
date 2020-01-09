package co.com.bancolombia;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.task.GenerateEntryPointTask;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;

public class GenerateEntryPointTaskTest {


    @Test
    public void settersTask() {
        Project project = ProjectBuilder.builder().build();
        project.getTasks().create("test", GenerateEntryPointTask.class);

        GenerateEntryPointTask task = (GenerateEntryPointTask) project.getTasks().getByName("test");

        task.setEntryPoint("1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void generateEntryPointValueNegative() throws IOException, CleanException {
        Project project = ProjectBuilder.builder().build();
        project.getTasks().create("test", GenerateEntryPointTask.class);

        GenerateEntryPointTask task = (GenerateEntryPointTask) project.getTasks().getByName("test");

        task.setEntryPoint("-8");
        task.generateEntryPoint();
    }

    @Test(expected = IllegalArgumentException.class)
    public void generateEntryPointValueUnExistent() throws IOException, CleanException {
        Project project = ProjectBuilder.builder().build();
        project.getTasks().create("test", GenerateEntryPointTask.class);

        GenerateEntryPointTask task = (GenerateEntryPointTask) project.getTasks().getByName("test");

        task.setEntryPoint("100");
        task.generateEntryPoint();
    }

    @Test
    public void generateEntryPoint() throws IOException, CleanException {
        File projectDir = new File("build/unitTest");
        Files.createDirectories(projectDir.toPath());
        writeString(new File(projectDir, "settings.gradle"), "");
        writeString(new File(projectDir, "build.gradle"),
                "plugins {" +
                        "  id('co.com.bancolombia.cleanArchitecture')" +
                        "}");

        Project project = ProjectBuilder.builder().withProjectDir(new File("build/unitTest")).build();
        project.getTasks().create("test", GenerateEntryPointTask.class);

        GenerateEntryPointTask task = (GenerateEntryPointTask) project.getTasks().getByName("test");

        task.setEntryPoint("1");
        task.generateEntryPoint();
    }

    private void writeString(File file, String string) throws IOException {
        try (Writer writer = new FileWriter(file)) {
            writer.write(string);
        }
    }
}
