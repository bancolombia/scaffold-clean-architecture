package co.com.bancolombia.task;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.FileUtilsTest.deleteStructure;
import static org.junit.Assert.assertTrue;

import co.com.bancolombia.exceptions.CleanException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public class GenerateHelperTaskTest {
  private GenerateHelperTask task;

  @Before
  public void init() throws IOException, CleanException {
    deleteStructure(Path.of("build/unitTest"));
    setup(GenerateStructureTask.ProjectType.IMPERATIVE);
  }

  @AfterClass
  public static void clean() {
    deleteStructure(Path.of("build/unitTest"));
  }

  private void setup(GenerateStructureTask.ProjectType type) throws IOException, CleanException {
    Project project = ProjectBuilder.builder().withProjectDir(new File("build/unitTest")).build();

    ProjectBuilder.builder()
        .withName(APP_SERVICE)
        .withProjectDir(new File("build/unitTest/applications/app-service"))
        .withParent(project)
        .build();

    project.getTasks().create("ca", GenerateStructureTask.class);
    GenerateStructureTask taskStructure =
        (GenerateStructureTask) project.getTasks().getByName("ca");
    taskStructure.setType(type);
    taskStructure.generateStructureTask();

    project.getTasks().create("test", GenerateHelperTask.class);
    task = (GenerateHelperTask) project.getTasks().getByName("test");
  }

  // Assert
  @Test(expected = IllegalArgumentException.class)
  public void shouldHandleErrorWhenNoName() throws IOException, CleanException {

    // Act
    task.generateHelperTask();
  }

  // Assert
  @Test(expected = IllegalArgumentException.class)
  public void shouldHandleErrorWhenEmptyName() throws IOException, CleanException {
    // Arrange
    task.setName("");
    // Act
    task.generateHelperTask();
  }

  @Test
  public void generateHelperGeneric() throws IOException, CleanException {
    // Arrange
    task.setName("MyHelper");
    // Act
    task.generateHelperTask();
    // Assert
    assertTrue(new File("build/unitTest/infrastructure/helpers/my-helper/build.gradle").exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/helpers/my-helper/src/main/java/co/com/bancolombia/myhelper")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/helpers/my-helper/src/test/java/co/com/bancolombia/myhelper")
            .exists());
  }
}
