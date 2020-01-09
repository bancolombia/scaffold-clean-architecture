package co.com.bancolombia;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.task.GenerateEntryPointTask;
import co.com.bancolombia.task.GenerateUseCaseTask;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;

public class GenerateUseCaseTaskTest {
    @Test
    public void settersTask() {
        Project project = ProjectBuilder.builder().build();
        project.getTasks().create("test", GenerateUseCaseTask.class);

        GenerateUseCaseTask task = (GenerateUseCaseTask) project.getTasks().getByName("test");

        task.setNameProject("testName");
    }

    @Test(expected = IllegalArgumentException.class)
    public void generateUseCaseException() throws IOException {
        Project project = ProjectBuilder.builder().build();
        project.getTasks().create("test", GenerateUseCaseTask.class);

        GenerateUseCaseTask task = (GenerateUseCaseTask) project.getTasks().getByName("test");

        task.generateUseCase();
    }

    @Test
    public void generateUseCase() throws IOException {
        File projectDir = new File("build/unitTest");
        Files.createDirectories(projectDir.toPath());
        writeString(new File(projectDir, "settings.gradle"), "");
        writeString(new File(projectDir, "build.gradle"),
                "plugins {" +
                        "  id('co.com.bancolombia.cleanArchitecture')" +
                        "}");

        Project project = ProjectBuilder.builder().withProjectDir(new File("build/unitTest")).build();
        project.getTasks().create("test", GenerateUseCaseTask.class);

        GenerateUseCaseTask task = (GenerateUseCaseTask) project.getTasks().getByName("test");

        task.setNameProject("useCaseTest");
        task.generateUseCase();
    }

    private void writeString(File file, String string) throws IOException {
        try (Writer writer = new FileWriter(file)) {
            writer.write(string);
        }
    }
}
