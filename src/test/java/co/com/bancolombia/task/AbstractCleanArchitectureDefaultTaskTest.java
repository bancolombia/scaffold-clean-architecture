package co.com.bancolombia.task;

import static co.com.bancolombia.utils.FileUtilsTest.deleteStructure;
import static org.junit.Assert.*;

import co.com.bancolombia.exceptions.CleanException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.gradle.api.Project;
import org.gradle.api.internal.tasks.options.OptionDescriptor;
import org.gradle.api.internal.tasks.options.OptionReader;
import org.gradle.internal.logging.text.StyledTextOutputFactory;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;

public class AbstractCleanArchitectureDefaultTaskTest {
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
    project.getTasks().create("cadt", HelperTask.class);
  }

  @Test
  public void shouldGetTaskDescriptor() {
    // Arrange
    AbstractCleanArchitectureDefaultTask task =
        (AbstractCleanArchitectureDefaultTask) project.getTasks().getByName("cadt");
    // Act
    OptionReader reader = task.getOptionReader();
    // Assert
    assertEquals(1, reader.getOptions(task).size());
  }

  @Test
  public void shouldGetTaskDescriptorWithOptions() {
    // Arrange
    AbstractCleanArchitectureDefaultTask task =
        (AbstractCleanArchitectureDefaultTask) project.getTasks().getByName("dm");
    // Act
    OptionReader reader = task.getOptionReader();
    // Assert
    List<OptionDescriptor> list = reader.getOptions(task);
    assertEquals(2, list.size());
    assertEquals("module", list.get(0).getName());
  }

  @Test
  public void shouldGetTextOutputFactory() {
    // Arrange
    AbstractCleanArchitectureDefaultTask task =
        (AbstractCleanArchitectureDefaultTask) project.getTasks().getByName("dm");
    // Act
    StyledTextOutputFactory factory = task.getTextOutputFactory();
    // Assert
    assertNotNull(factory);
  }

  @Test
  public void shouldPrintHelp() {
    // Arrange
    AbstractCleanArchitectureDefaultTask task =
        (AbstractCleanArchitectureDefaultTask) project.getTasks().getByName("dm");
    // Act
    task.printHelp();
    // Assert
    assertNotNull(task.getTextOutputFactory());
  }

  public static class HelperTask extends AbstractCleanArchitectureDefaultTask {

    @Override
    public void execute() throws IOException, CleanException {}
  }
}
