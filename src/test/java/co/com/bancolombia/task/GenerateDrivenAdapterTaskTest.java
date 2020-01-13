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

public class GenerateDrivenAdapterTaskTest {
    @Test
    public void settersTask() {
        Project project = ProjectBuilder.builder().build();
        project.getTasks().create("test", GenerateDrivenAdapterTask.class);
        GenerateDrivenAdapterTask task = (GenerateDrivenAdapterTask) project.getTasks().getByName("test");

        task.setDrivenAdapter("1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void generateDrivenAdapterValueNegative() throws IOException, CleanException {
        Project project = ProjectBuilder.builder().build();
        project.getTasks().create("test", GenerateDrivenAdapterTask.class);

        GenerateDrivenAdapterTask task = (GenerateDrivenAdapterTask) project.getTasks().getByName("test");

        task.setDrivenAdapter("-8");
        task.generateDrivenAdapter();
    }

    @Test(expected = IllegalArgumentException.class)
    public void generateDrivenAdapterValueUnExistent() throws IOException {
        Project project = ProjectBuilder.builder().build();
        project.getTasks().create("test", GenerateDrivenAdapterTask.class);

        GenerateDrivenAdapterTask task = (GenerateDrivenAdapterTask) project.getTasks().getByName("test");

        task.setDrivenAdapter("100");
        task.generateDrivenAdapter();
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
        task.generateDrivenAdapter();

        task.setDrivenAdapter("2");
        task.generateDrivenAdapter();

        task.setDrivenAdapter("3");
        task.generateDrivenAdapter();
    }
    private void writeString(File file, String string) throws IOException {
        try (Writer writer = new FileWriter(file)) {
            writer.write(string);
        }
    }
}
