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

public class GenerateTestTaskTest {

  GenerateAcceptanceTestTask task;
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

    project.getTasks().create("test", GenerateAcceptanceTestTask.class);
    task = (GenerateAcceptanceTestTask) project.getTasks().getByName("test");
  }

  @Test
  public void generateAcceptanceTest() throws IOException, CleanException {

    task.generateAcceptanceTestTask();

    assertTrue(
        new File(
                "build/unitTest/deployment/acceptance-test/src/test/java/co/com/bancolombia/TestParallel.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/deployment/acceptance-test/src/test/java/co/com/bancolombia/utils/ValidatorTestUtils.java")
            .exists());
    assertTrue(
        new File("build/unitTest/deployment/acceptance-test/src/test/resources/logback-test.xml")
            .exists());
    assertTrue(
        new File("build/unitTest/deployment/acceptance-test/src/test/resources/karate-config.js")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/deployment/acceptance-test/src/test/resources/co/com/bancolombia/demo/demo.feature")
            .exists());
    assertTrue(new File("build/unitTest/deployment/acceptance-test/settings.gradle").exists());
    assertTrue(new File("build/unitTest/deployment/acceptance-test/build.gradle").exists());
    assertTrue(new File("build/unitTest/deployment/acceptance-test/README.md").exists());
  }
}
