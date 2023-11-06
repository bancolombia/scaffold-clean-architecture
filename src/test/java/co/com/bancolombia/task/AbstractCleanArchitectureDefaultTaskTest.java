package co.com.bancolombia.task;

import static co.com.bancolombia.utils.FileUtilsTest.deleteStructure;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
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
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
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
  private HelperTask helperTask;

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
    helperTask = spy((HelperTask) project.getTasks().getByName("cadt"));
  }

  @Test
  public void shouldGetTaskDescriptor() {
    // Arrange
    // Act
    OptionReader reader = helperTask.getOptionReader();

    // Assert
    assertEquals(0, reader.getOptions(helperTask).size());
  }

  @Test
  public void shouldGetTaskDescriptorWithOptions() {
    // Arrange
    // Act
    OptionReader reader = task.getOptionReader();
    // Assert
    Map<String, OptionDescriptor> map = reader.getOptions(task);
    assertEquals(1, map.size());
    assertTrue(map.containsKey("module"));
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

  @Test
  public void shouldExecuteAfterModuleFactory() throws CleanException, IOException {
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
  public void shouldHandleCleanException() throws CleanException, IOException {
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
  public void shouldHandleException() throws CleanException, IOException {
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
  public void shouldExecuteAfterTaskModuleFactory() throws CleanException, IOException {
    // Arrange
    HelperTask task = (HelperTask) project.getTasks().getByName("cadt");
    // Act
    task.executeBaseTask();
    // Assert
    assertEquals("OK", task.helperCheck("check2"));
  }

  @Test
  public void shouldHandleTaskCleanException() throws CleanException, IOException {
    // Arrange
    HelperTask task = (HelperTask) project.getTasks().getByName("cadt");
    task.setThrow("CleanException");
    // Act
    task.executeBaseTask();
    // Assert
    assertNull(task.helperCheck("check2"));
  }

  @Test
  public void shouldHandleTaskException() throws CleanException, IOException {
    // Arrange
    HelperTask task = (HelperTask) project.getTasks().getByName("cadt");
    task.setThrow("Exception");
    // Act
    task.executeBaseTask();
    // Assert
    assertNull(task.helperCheck("check2"));
  }

  @Test
  public void shouldHandleTaskInvalidTaskOptionException() throws CleanException, IOException {
    // Arrange
    HelperTask task = (HelperTask) project.getTasks().getByName("cadt");
    task.setThrow("InvalidTaskOptionException");
    // Act
    task.executeBaseTask();
    // Assert
    assertNull(task.helperCheck("check2"));
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
