package co.com.bancolombia.task;

import static co.com.bancolombia.utils.FileUtilsTest.deleteStructure;
import static org.junit.Assert.assertTrue;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.exceptions.ParamNotFoundException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;

public class GenerateUseCaseTaskTest {
  private GenerateUseCaseTask task;

  @Before
  public void setup() throws IOException, CleanException {
    deleteStructure(Path.of("build/unitTest"));
    Project project = ProjectBuilder.builder().withProjectDir(new File("build/unitTest")).build();
    project.getTasks().create("ca", GenerateStructureTask.class);
    GenerateStructureTask caTask = (GenerateStructureTask) project.getTasks().getByName("ca");
    caTask.generateStructureTask();

    project.getTasks().create("test", GenerateUseCaseTask.class);
    task = (GenerateUseCaseTask) project.getTasks().getByName("test");
  }

  // Assert
  @Test(expected = IllegalArgumentException.class)
  public void generateUseCaseException() throws IOException, ParamNotFoundException {
    // Arrange
    // Act
    task.generateUseCaseTask();
  }

  @Test
  public void generateUseCase() throws IOException, ParamNotFoundException {
    // Arrange
    task.setName("business");
    // Act
    task.generateUseCaseTask();
    // Assert
    assertTrue(
        new File(
                "build/unitTest/domain/usecase/src/main/java/co/com/bancolombia/usecase/business/BusinessUseCase.java")
            .exists());
    assertTrue(new File("build/unitTest/domain/usecase/build.gradle").exists());
  }

  @Test
  public void generateUseCaseWithCorrectName() throws IOException, ParamNotFoundException {
    // Arrange
    task.setName("MyUseCase");
    // Act
    task.generateUseCaseTask();
    // Assert
    assertTrue(
        new File(
                "build/unitTest/domain/usecase/src/main/java/co/com/bancolombia/usecase/my/MyUseCase.java")
            .exists());
    assertTrue(new File("build/unitTest/domain/usecase/build.gradle").exists());
  }
}
