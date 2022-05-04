package co.com.bancolombia.factory;

import static co.com.bancolombia.Constants.MainFiles.APPLICATION_PROPERTIES;
import static org.junit.Assert.assertTrue;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.task.GenerateStructureTask;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;

public class ModuleBuilderTest {
  private ModuleBuilder builder;

  @Before
  public void init() throws IOException, CleanException {
    File projectDir = new File("build/unitTest");
    Files.createDirectories(projectDir.toPath());
    writeString(
        new File(projectDir, "build.gradle"),
        "plugins {" + "  id('co.com.bancolombia.cleanArchitecture')" + "}");

    Project project =
        ProjectBuilder.builder()
            .withName("cleanArchitecture")
            .withProjectDir(new File("build/unitTest"))
            .build();

    project.getTasks().create("testStructure", GenerateStructureTask.class);
    GenerateStructureTask taskStructure =
        (GenerateStructureTask) project.getTasks().getByName("testStructure");
    taskStructure.generateStructureTask();

    builder = new ModuleBuilder(project);
  }

  @Test
  public void shouldAppendProperties() throws IOException {
    builder.appendToProperties("spring.datasource").put("url", "mydburl");
    builder.appendToProperties("").put("test", "myUnitTes");
    builder.appendToProperties("server").put("port", 8000);
    builder.removeDir(null);
    builder.persist();
    builder.updateFile(
        APPLICATION_PROPERTIES,
        content -> {
          assertTrue(content.contains("myUnitTes"));
          return content;
        });
  }

  private void writeString(File file, String string) throws IOException {
    try (Writer writer = new FileWriter(file)) {
      writer.write(string);
    }
  }
}
