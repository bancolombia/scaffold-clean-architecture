package co.com.bancolombia.task;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.FileUtilsTest.deleteStructure;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.utils.FileUtils;
import co.com.bancolombia.utils.SonarCheck;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;

public class InternalTaskTest {
  private InternalTask task;
  private Project appService;

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
    generateStructureTask.setWithExample(AbstractCleanArchitectureDefaultTask.BooleanOption.TRUE);
    generateStructureTask.execute();

    appService =
        ProjectBuilder.builder()
            .withName(APP_SERVICE)
            .withProjectDir(new File("build/unitTest/applications/app-service"))
            .withParent(project)
            .build();

    project.getTasks().create("test", InternalTask.class);
    task = (InternalTask) project.getTasks().getByName("test");
  }

  @Test
  public void shouldParseDependencyReport() throws IOException {
    // Arrange
    Files.createDirectories(appService.file(SonarCheck.INPUT).getParentFile().toPath());
    FileUtils.writeString(
        appService, SonarCheck.INPUT, readResourceFile("dependencies-check/report.json"));
    // Act
    task.execute();
    // Assert
    assertTrue(appService.file(SonarCheck.OUTPUT).exists());
  }

  @Test
  public void shouldGetOptions() {
    // Arrange
    // Act
    List<InternalTask.Action> options = task.getInputOptions();
    // Assert
    assertEquals(1, options.size());
  }

  private String readResourceFile(String location) throws IOException {
    ClassLoader classLoader = getClass().getClassLoader();
    return Files.readString(
        Path.of(Objects.requireNonNull(classLoader.getResource(location)).getPath()));
  }
}
