package co.com.bancolombia.factory.upgrades;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.task.GenerateStructureTask;
import co.com.bancolombia.utils.FileUpdater;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;

public class UpgradeFactoryTest {
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
    taskStructure.execute();

    builder = spy(new ModuleBuilder(project));
    builder.addParam(UpgradeFactory.UPGRADES, "co.com.bancolombia.factory.upgrades.samples");
  }

  @Test
  public void shouldFetchAndRunAllUpdates() throws CleanException, IOException {
    // Arrange
    UpgradeFactory factory = new UpgradeFactory();
    // Act
    factory.buildModule(builder);
    // Assert
    verify(builder, times(1)).updateFile(anyString(), any(FileUpdater.class));
  }

  private void writeString(File file, String string) throws IOException {
    try (Writer writer = new FileWriter(file)) {
      writer.write(string);
    }
  }
}
