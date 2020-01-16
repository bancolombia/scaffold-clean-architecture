package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.task.GenerateDrivenAdapterTask;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;

import static org.junit.Assert.assertTrue;

public class GenerateDrivenAdapterTaskTest {

    @Test(expected = IllegalArgumentException.class)
    public void generateDrivenAdapterValueNegative() throws IOException, CleanException {
        Project project = ProjectBuilder.builder().build();
        project.getTasks().create("test", GenerateDrivenAdapterTask.class);

        GenerateDrivenAdapterTask task = (GenerateDrivenAdapterTask) project.getTasks().getByName("test");

        task.setDrivenAdapter("-8");
        task.generateDrivenAdapterTask();
    }

    @Test(expected = IllegalArgumentException.class)
    public void generateDrivenAdapterValueUnExistent() throws IOException {
        Project project = ProjectBuilder.builder().build();
        project.getTasks().create("test", GenerateDrivenAdapterTask.class);

        GenerateDrivenAdapterTask task = (GenerateDrivenAdapterTask) project.getTasks().getByName("test");

        task.setDrivenAdapter("100");
        task.generateDrivenAdapterTask();
    }

    @Test
    public void generateDrivenAdapter() throws IOException {
        File projectDir = new File("build/unitTest");
        Files.createDirectories(projectDir.toPath());
        writeString(new File(projectDir, "settings.gradle"), "");
        writeString(new File(projectDir, "build.gradle"),
                "plugins {" +
                        "  id('co.com.bancolombia.cleanArchitecture')" +
                        "}");

        Project project = ProjectBuilder.builder().withProjectDir(new File("build/unitTest")).build();
        project.getTasks().create("test", GenerateDrivenAdapterTask.class);

        GenerateDrivenAdapterTask task = (GenerateDrivenAdapterTask) project.getTasks().getByName("test");

        task.setDrivenAdapter("1");
        task.generateDrivenAdapterTask();

        task.setDrivenAdapter("2");
        task.generateDrivenAdapterTask();

        task.setDrivenAdapter("3");
        task.generateDrivenAdapterTask();
        assertTrue(new File("build/unitTest/infrastructure/driven-adapters/secrets-manager-consumer/build.gradle").exists());
        assertTrue(new File("build/unitTest/infrastructure/driven-adapters/secrets-manager-consumer/src/main/java/co/com/bancolombia/secrets/SecretsManager.java").exists());

    }
    private void writeString(File file, String string) throws IOException {
        try (Writer writer = new FileWriter(file)) {
            writer.write(string);
        }
    }
}
