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

public class GenerateModelTaskTest {
  private GenerateModelTask task;

  @Before
  public void setup() throws IOException, CleanException {
    deleteStructure(Path.of("build/unitTest"));
    Project project = ProjectBuilder.builder().withProjectDir(new File("build/unitTest")).build();
    project.getTasks().create("ca", GenerateStructureTask.class);
    GenerateStructureTask caTask = (GenerateStructureTask) project.getTasks().getByName("ca");
    caTask.generateStructureTask();

    project.getTasks().create("test", GenerateModelTask.class);
    task = (GenerateModelTask) project.getTasks().getByName("test");
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldFailWithoutArgumentsForModel() throws IOException, CleanException {
    task.generateModelTask();
  }

  @Test
  public void shouldGenerateModel() throws IOException, ParamNotFoundException {
    task.setName("testModel");
    task.generateModelTask();
    assertTrue(
        new File(
                "build/unitTest/domain/model/src/main/java/co/com/bancolombia/model/testmodel/gateways/TestModelRepository.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/domain/model/src/main/java/co/com/bancolombia/model/testmodel/TestModel.java")
            .exists());
  }
}
