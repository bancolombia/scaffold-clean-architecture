package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.exceptions.ParamNotFoundException;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;

public class GenerateBaseTaskTest {
    private Helper task;

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

        project.getTasks().create("testBase", Helper.class);
        task = (Helper) project.getTasks().getByName("testBase");
    }

    @Test
    public void shouldReplacePlaceholders() throws CleanException {
        // Arrange
        String fillablePath = "default/driven-adapters/{{name}}/src/main/{{className}}";
        // Act
        task.addParam("name", "redis");
        task.addParam("className", "Redis.java");
        String result = task.fillPath(fillablePath);
        // Assert
        assertEquals("default/driven-adapters/redis/src/main/Redis.java", result);
    }

    @Test(expected = ParamNotFoundException.class)
    public void shouldHandleErrorWhenNotParamReplacePlaceholders() throws CleanException {
        // Arrange
        String fillablePath = "default/driven-adapters/{{name}}/src/main/{{className}}";
        // Act
        task.addParam("className", "Redis.java");
        String result = task.fillPath(fillablePath);
        // Assert
    }

    private void writeString(File file, String string) throws IOException {
        try (Writer writer = new FileWriter(file)) {
            writer.write(string);
        }
    }

    public static class Helper extends GenerateBaseTask {

        @Override
        protected void setupDirs() {

        }

        @Override
        protected void setupFiles() throws IOException {

        }
    }
}
