package co.com.bancolombia.task;

import static co.com.bancolombia.TestUtils.assertFilesExistsInDir;
import static co.com.bancolombia.TestUtils.createTask;
import static co.com.bancolombia.TestUtils.deleteStructure;
import static co.com.bancolombia.TestUtils.getTask;
import static co.com.bancolombia.TestUtils.getTestDir;
import static co.com.bancolombia.TestUtils.setupProject;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.exceptions.ParamNotFoundException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class GenerateModelTaskTest {
  private static final String TEST_DIR = getTestDir(GenerateModelTaskTest.class);
  private static GenerateModelTask task;

  @BeforeAll
  public static void setup() throws IOException, CleanException {
    deleteStructure(Path.of(TEST_DIR));
    Project project = setupProject(GenerateModelTaskTest.class, GenerateStructureTask.class);

    GenerateStructureTask taskStructure = getTask(project, GenerateStructureTask.class);
    taskStructure.setType(GenerateStructureTask.ProjectType.REACTIVE);
    taskStructure.setLanguage(GenerateStructureTask.Language.JAVA);
    taskStructure.execute();

    ProjectBuilder.builder()
        .withName("app-service")
        .withProjectDir(new File(TEST_DIR + "/applications/app-service"))
        .withParent(project)
        .build();

    task = createTask(project, GenerateModelTask.class);
  }

  @AfterAll
  public static void tearDown() {
    deleteStructure(Path.of(TEST_DIR));
  }

  @Test
  void shouldFailWithoutArgumentsForModel() {
    task.setName(null);
    assertThrows(IllegalArgumentException.class, () -> task.execute());
  }

  @Test
  void shouldGenerateModel() throws IOException, ParamNotFoundException {
    task.setName("testModel");
    task.execute();
    assertFilesExistsInDir(
        TEST_DIR + "/domain/model/",
        "src/main/java/co/com/bancolombia/model/testmodel/gateways/TestModelRepository.java",
        "src/main/java/co/com/bancolombia/model/testmodel/TestModel.java");
  }
}
