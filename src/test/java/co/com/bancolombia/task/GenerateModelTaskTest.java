package co.com.bancolombia.task;

import static org.junit.Assert.assertTrue;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.exceptions.ParamNotFoundException;
import java.io.File;
import java.io.IOException;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;

public class GenerateModelTaskTest {
  private GenerateModelTask task;

  @Before
  public void setup() throws IOException, CleanException {
    Project project = ProjectBuilder.builder().withProjectDir(new File("build/unitTest")).build();
    project.getTasks().create("ca", GenerateStructureTask.class);
    project.getTasks().create("test", GenerateModelTask.class);
    GenerateStructureTask caTask = (GenerateStructureTask) project.getTasks().getByName("ca");
    caTask.generateStructureTask();
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
