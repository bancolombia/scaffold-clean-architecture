package co.com.bancolombia.task;

import static co.com.bancolombia.TestUtils.deleteStructure;
import static co.com.bancolombia.TestUtils.getTask;
import static co.com.bancolombia.TestUtils.getTestDir;
import static co.com.bancolombia.TestUtils.setupProject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.exceptions.InvalidTaskOptionException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.internal.tasks.options.OptionDescriptor;
import org.gradle.api.internal.tasks.options.OptionReader;
import org.gradle.internal.logging.text.StyledTextOutputFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AbstractCleanArchitectureDefaultTaskTest {
  private static final String TEST_DIR = getTestDir(AbstractCleanArchitectureDefaultTaskTest.class);
  private Project project;
  private AbstractCleanArchitectureDefaultTask task;
  private HelperTask helperTask;

  @BeforeEach
  public void setup() {
    project =
        setupProject(
            AbstractCleanArchitectureDefaultTaskTest.class,
            DeleteModuleTask.class,
            HelperTask.class);

    task = getTask(project, DeleteModuleTask.class);
    helperTask = spy(getTask(project, HelperTask.class));
  }

  @AfterAll
  public static void tearDown() {
    deleteStructure(Path.of(TEST_DIR));
  }

  @Test
  void shouldGetTaskDescriptor() {
    // Arrange
    // Act
    OptionReader reader = helperTask.getOptionReader();
    // Assert
    assertEquals(0, reader.getOptions(helperTask).size());
  }

  @Test
  void shouldGetTaskDescriptorWithOptions() {
    // Arrange
    // Act
    OptionReader reader = task.getOptionReader();
    // Assert
    Map<String, OptionDescriptor> map = reader.getOptions(task);
    assertEquals(1, map.size());
    assertTrue(map.containsKey("module"));
  }

  @Test
  void shouldGetTextOutputFactory() {
    // Arrange
    // Act
    StyledTextOutputFactory factory = task.getTextOutputFactory();
    // Assert
    assertNotNull(factory);
  }

  @Test
  void shouldPrintHelp() {
    // Arrange
    // Act
    task.printHelp();
    // Assert
    assertNotNull(task.getTextOutputFactory());
  }

  @Test
  void shouldPrintHelpWhenExisting() {
    // Arrange
    Action<HelperTask> action = mock(Action.class);
    project.getTasks().register("help", HelperTask.class, action);
    // Act
    task.printHelp();
    // Assert
    verify(action, times(1)).execute(any());
  }

  @Test
  void shouldExecuteTask() throws CleanException, IOException {
    // Arrange
    // Act
    helperTask.executeBaseTask();
    // Assert
    verify(helperTask, times(1)).execute();
  }

  @Test
  void shouldExecuteAfterModuleFactory() throws CleanException, IOException {
    // Arrange
    doReturn("Custom").when(helperTask).resolvePrefix();
    doReturn("co.com.bancolombia.task").when(helperTask).resolvePackage();
    // Act
    helperTask.executeBaseTask();
    // Assert
    verify(helperTask, times(1)).execute();
    assertEquals("OK", helperTask.helperCheck("check1"));
  }

  @Test
  void shouldHandleCleanException() throws CleanException, IOException {
    // Arrange
    doReturn("Custom").when(helperTask).resolvePrefix();
    doReturn("co.com.bancolombia.task").when(helperTask).resolvePackage();
    helperTask.setThrow("CleanException");
    // Act
    helperTask.executeBaseTask();
    // Assert
    verify(helperTask, times(1)).execute();
    assertNull(helperTask.helperCheck("check3"));
  }

  @Test
  void shouldHandleException() throws CleanException, IOException {
    // Arrange
    doReturn("Custom").when(helperTask).resolvePrefix();
    doReturn("co.com.bancolombia.task").when(helperTask).resolvePackage();
    helperTask.setThrow("Exception");
    // Act
    helperTask.executeBaseTask();
    // Assert
    verify(helperTask, times(1)).execute();
    assertNull(helperTask.helperCheck("check4"));
  }

  @Test
  void shouldExecuteAfterTaskModuleFactory() throws CleanException, IOException {
    // Arrange
    // Act
    helperTask.executeBaseTask();
    // Assert
    assertEquals("OK", helperTask.helperCheck("check2"));
  }

  @Test
  void shouldHandleTaskCleanException() throws CleanException, IOException {
    // Arrange
    helperTask.setThrow("CleanException");
    // Act
    helperTask.executeBaseTask();
    // Assert
    assertNull(helperTask.helperCheck("check2"));
  }

  @Test
  void shouldHandleTaskException() throws CleanException, IOException {
    // Arrange
    helperTask.setThrow("Exception");
    // Act
    helperTask.executeBaseTask();
    // Assert
    assertNull(helperTask.helperCheck("check2"));
  }

  @Test
  void shouldHandleTaskInvalidTaskOptionException() throws CleanException, IOException {
    // Arrange
    helperTask.setThrow("InvalidTaskOptionException");
    // Act
    helperTask.executeBaseTask();
    // Assert
    assertNull(helperTask.helperCheck("check2"));
  }

  public static class AfterCustomJPA extends AfterModuleFactory {
    @Override
    public String check() {
      return "check1";
    }
  }

  public static class AfterHelperTask extends AfterModuleFactory {
    @Override
    public String check() {
      return "check2";
    }
  }

  public abstract static class AfterModuleFactory implements ModuleFactory {

    @Override
    public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
      // some custom module should be called
      if ("CleanException".equals(builder.getStringParam("throw"))) {
        throw new CleanException("Thrown out for testing");
      }
      if ("Exception".equals(builder.getStringParam("throw"))) {
        throw new RuntimeException("Thrown out for testing");
      }
      if ("InvalidTaskOptionException".equals(builder.getStringParam("throw"))) {
        throw new InvalidTaskOptionException("Thrown out for testing");
      }
      builder.addParam(check(), "OK");
    }

    protected abstract String check();
  }

  public static class HelperTask extends AbstractCleanArchitectureDefaultTask {
    public HelperTask() {
      builder.addParam("type", "JPA");
    }

    public String helperCheck(String check) {
      return builder.getStringParam(check);
    }

    public void setThrow(String value) {
      builder.addParam("throw", value);
    }

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
