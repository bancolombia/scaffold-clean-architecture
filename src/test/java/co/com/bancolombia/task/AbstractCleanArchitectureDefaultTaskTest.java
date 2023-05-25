package co.com.bancolombia.task;

import static co.com.bancolombia.utils.FileUtilsTest.deleteStructure;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

import co.com.bancolombia.exceptions.CleanException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.internal.tasks.options.OptionDescriptor;
import org.gradle.api.internal.tasks.options.OptionReader;
import org.gradle.internal.logging.text.StyledTextOutputFactory;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;

public class AbstractCleanArchitectureDefaultTaskTest {
  private Project project;
  private AbstractCleanArchitectureDefaultTask task;
  private AbstractCleanArchitectureDefaultTask helperTask;

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
    task = (AbstractCleanArchitectureDefaultTask) project.getTasks().getByName("dm");
    helperTask = spy((AbstractCleanArchitectureDefaultTask) project.getTasks().getByName("cadt"));
  }

  @Test
  public void shouldGetTaskDescriptor() {
    // Arrange
    // Act
    OptionReader reader = helperTask.getOptionReader();
    // Assert
    assertEquals(1, reader.getOptions(helperTask).size());
  }

  @Test
  public void shouldGetTaskDescriptorWithOptions() {
    // Arrange
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
    // Act
    StyledTextOutputFactory factory = task.getTextOutputFactory();
    // Assert
    assertNotNull(factory);
  }

  @Test
  public void shouldPrintHelp() {
    // Arrange
    // Act
    task.printHelp();
    // Assert
    assertNotNull(task.getTextOutputFactory());
  }

  @Test
  public void shouldPrintHelpWhenExisting() {
    // Arrange
    Action<HelperTask> action = mock(Action.class);
    project.getTasks().register("help", HelperTask.class, action);
    // Act
    task.printHelp();
    // Assert
    verify(action, times(1)).execute(any());
  }

  @Test
  public void shouldExecuteTask() throws CleanException, IOException {
    // Arrange
    // Act
    helperTask.executeBaseTask();
    // Assert
    verify(helperTask, times(1)).execute();
  }

  public static class HelperTask extends AbstractCleanArchitectureDefaultTask {

    @Inject
    public String getTaskPath() {
      throw new UnsupportedOperationException();
    }

    public void setTaskPath(String taskPath) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void execute() throws IOException, CleanException {}
  }
}
