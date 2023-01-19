package co.com.bancolombia.task;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.FileUtilsTest.deleteStructure;
import static org.junit.Assert.assertTrue;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.tests.performance.ModuleFactoryPerformanceTests.PerformanceTestType;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public class GeneratePerformanceTestTaskTest {

  GeneratePerformanceTestTask task;

  static Project project =
      ProjectBuilder.builder().withProjectDir(new File("build/unitTest")).build();

  @Before
  public void init() throws IOException, CleanException {
    deleteStructure(Path.of("build/unitTest"));
    setup(GenerateStructureTask.ProjectType.IMPERATIVE);
  }

  @AfterClass
  public static void clean() {
    deleteStructure(project.getProjectDir().toPath());
  }

  public void setup(GenerateStructureTask.ProjectType type) throws IOException, CleanException {
    deleteStructure(project.getProjectDir().toPath());
    project.getTasks().create("ca", GenerateStructureTask.class);
    GenerateStructureTask caTask = (GenerateStructureTask) project.getTasks().getByName("ca");
    caTask.setType(type);
    caTask.generateStructureTask();

    ProjectBuilder.builder()
        .withProjectDir(new File("build/unitTest/applications/app-service"))
        .withName(APP_SERVICE)
        .withParent(project)
        .build();

    project.getTasks().create("test", GeneratePerformanceTestTask.class);
    task = (GeneratePerformanceTestTask) project.getTasks().getByName("test");
  }

  @Test
  public void generatePerformanceTest() throws IOException, CleanException {
    task.setType(PerformanceTestType.JMETER);
    task.generateAcceptanceTestTask();

    assertTrue(new File("build/unitTest/performance-test/README.md").exists());
  }
}
