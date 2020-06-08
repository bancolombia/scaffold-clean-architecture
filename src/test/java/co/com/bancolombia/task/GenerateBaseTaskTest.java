package co.com.bancolombia.task;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;

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

    private void writeString(File file, String string) throws IOException {
        try (Writer writer = new FileWriter(file)) {
            writer.write(string);
        }
    }

    public static class Helper extends GenerateBaseTask {

    }
}
