package co.com.bancolombia.task;

import co.com.bancolombia.task.GenerateUseCaseTask;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;

import static org.junit.Assert.assertTrue;

public class GenerateUseCaseTaskTest {
    private GenerateUseCaseTask task;

    @Before
    public void init() throws IOException {
        File projectDir = new File("build/unitTest");
        Files.createDirectories(projectDir.toPath());
        writeString(new File(projectDir, "settings.gradle"), "");
        writeString(new File(projectDir, "build.gradle"),
                "plugins {" +
                        "  id('co.com.bancolombia.cleanArchitecture')" +
                        "}");
        Project project = ProjectBuilder.builder().withProjectDir(new File("build/unitTest")).build();
        project.getTasks().create("test", GenerateUseCaseTask.class);

        task = (GenerateUseCaseTask) project.getTasks().getByName("test");
    }


    @Test(expected = IllegalArgumentException.class)
    public void generateUseCaseException() throws IOException {

        task.generateUseCaseTask();
    }

    @Test
    public void generateUseCase() throws IOException {
        task.setNameProject("useCaseTest");
        task.generateUseCaseTask();
        assertTrue(new File("build/unitTest/domain/usecase/src/main/java/co/com/bancolombia/usecase/useCaseTest/UseCaseTest.java").exists());

    }

    private void writeString(File file, String string) throws IOException {
        try (Writer writer = new FileWriter(file)) {
            writer.write(string);
        }
    }
}
