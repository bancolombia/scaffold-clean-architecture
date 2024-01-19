package co.com.bancolombia.task;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.FileUtilsTest.deleteStructure;
import static org.junit.Assert.*;

import co.com.bancolombia.exceptions.CleanException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;

public class AnalyticsTaskTest {
  private AnalyticsTask task;

  @Before
  public void setup() throws IOException, CleanException {
    deleteStructure(Path.of("build/unitTest"));
    Project project =
        ProjectBuilder.builder()
            .withName("cleanArchitecture")
            .withProjectDir(new File("build/unitTest"))
            .build();

    project.getTasks().create("ca", GenerateStructureTask.class);
    GenerateStructureTask generateStructureTask =
        (GenerateStructureTask) project.getTasks().getByName("ca");
    generateStructureTask.execute();

    ProjectBuilder.builder()
        .withName(APP_SERVICE)
        .withProjectDir(new File("build/unitTest/applications/app-service"))
        .withParent(project)
        .build();

    project.getTasks().create("test", AnalyticsTask.class);
    task = (AnalyticsTask) project.getTasks().getByName("test");
  }

  @Test
  public void shouldEnableAnalytics() throws IOException, CleanException {
    // Arrange
    task.setAnalyticsState(AbstractCleanArchitectureDefaultTask.BooleanOption.TRUE);
    // Act
    task.execute();
    // Assert
    assertTrue(
        Files.readString(Path.of("build/unitTest/gradle.properties")).contains("analytics=true"));
  }

  @Test
  public void shouldDisableAnalytics() throws IOException, CleanException {
    // Arrange
    task.setAnalyticsState(AbstractCleanArchitectureDefaultTask.BooleanOption.FALSE);
    // Act
    task.execute();
    // Assert
    assertTrue(
        Files.readString(Path.of("build/unitTest/gradle.properties")).contains("analytics=false"));
  }

  @Test
  public void shouldGetOptions() {
    // Arrange
    // Act
    List<AbstractCleanArchitectureDefaultTask.BooleanOption> options = task.getInputOptions();
    // Assert
    assertEquals(2, options.size());
  }
}
