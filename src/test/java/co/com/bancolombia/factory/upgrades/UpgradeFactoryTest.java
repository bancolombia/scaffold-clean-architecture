package co.com.bancolombia.factory.upgrades;

import static co.com.bancolombia.TestUtils.deleteStructure;
import static co.com.bancolombia.TestUtils.getTask;
import static co.com.bancolombia.TestUtils.getTestDir;
import static co.com.bancolombia.TestUtils.setupProject;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.task.GenerateStructureTask;
import co.com.bancolombia.utils.FileUpdater;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpgradeFactoryTest {
  private static final String TEST_DIR = getTestDir(UpgradeFactoryTest.class);
  private ModuleBuilder builder;

  @BeforeEach
  public void setup() throws IOException, CleanException {
    deleteStructure(Path.of(TEST_DIR));
    Project project = setupProject(UpgradeFactoryTest.class, GenerateStructureTask.class);

    GenerateStructureTask taskStructure = getTask(project, GenerateStructureTask.class);
    taskStructure.setType(GenerateStructureTask.ProjectType.IMPERATIVE);
    taskStructure.setLanguage(GenerateStructureTask.Language.JAVA);
    taskStructure.execute();

    ProjectBuilder.builder()
        .withName("app-service")
        .withProjectDir(new File(TEST_DIR + "/applications/app-service"))
        .withParent(project)
        .build();

    builder = spy(new ModuleBuilder(project));
    builder.addParam(UpgradeFactory.UPGRADES, "co.com.bancolombia.factory.upgrades.samples");
  }

  @AfterAll
  public static void tearDown() {
    deleteStructure(Path.of(TEST_DIR));
  }

  @Test
  void shouldFetchAndRunAllUpdates() throws CleanException, IOException {
    // Arrange
    UpgradeFactory factory = new UpgradeFactory();
    // Act
    factory.buildModule(builder);
    // Assert
    verify(builder, times(1)).updateFile(anyString(), any(FileUpdater.class));
  }
}
