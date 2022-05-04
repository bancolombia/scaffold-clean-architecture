package co.com.bancolombia.task;

import static co.com.bancolombia.utils.FileUtilsTest.deleteStructure;
import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import org.gradle.api.Project;
import org.gradle.api.internal.tasks.options.OptionDescriptor;
import org.gradle.api.internal.tasks.options.OptionReader;
import org.gradle.internal.logging.text.StyledTextOutputFactory;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;

public class CleanArchitectureDefaultTaskTest {
  private Project project;

  @Before
  public void setup() {
    deleteStructure(Path.of("build/unitTest"));
    project =
        ProjectBuilder.builder()
            .withName("cleanArchitecture")
            .withProjectDir(new File("build/unitTest"))
            .build();

    project.getTasks().create("dm", DeleteModuleTask.class);
    project.getTasks().create("cadt", CleanArchitectureDefaultTask.class);
  }

  @Test
  public void shouldGetTaskDescriptor() {
    // Arrange
    CleanArchitectureDefaultTask task =
        (CleanArchitectureDefaultTask) project.getTasks().getByName("cadt");
    // Act
    OptionReader reader = task.getOptionReader();
    // Assert
    assertTrue(reader.getOptions(task).isEmpty());
  }

  @Test
  public void shouldGetTaskDescriptorWithOptions() {
    // Arrange
    CleanArchitectureDefaultTask task =
        (CleanArchitectureDefaultTask) project.getTasks().getByName("dm");
    // Act
    OptionReader reader = task.getOptionReader();
    // Assert
    List<OptionDescriptor> list = reader.getOptions(task);
    assertEquals(1, list.size());
    assertEquals("module", list.get(0).getName());
  }

  @Test
  public void shouldGetTextOutputFactory() {
    // Arrange
    CleanArchitectureDefaultTask task =
        (CleanArchitectureDefaultTask) project.getTasks().getByName("dm");
    // Act
    StyledTextOutputFactory factory = task.getTextOutputFactory();
    // Assert
    assertNotNull(factory);
  }

  @Test
  public void shouldPrintHelp() {
    // Arrange
    CleanArchitectureDefaultTask task =
        (CleanArchitectureDefaultTask) project.getTasks().getByName("dm");
    // Act
    task.printHelp();
    // Assert
    assertNotNull(task.getTextOutputFactory());
  }
}
