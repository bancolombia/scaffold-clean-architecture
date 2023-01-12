package co.com.bancolombia.task;

import static co.com.bancolombia.utils.FileUtilsTest.deleteStructure;
import static org.junit.Assert.*;

import co.com.bancolombia.Constants.BooleanOption;
import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.task.GenerateStructureTask.JavaVersion;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public class GenerateStructureTaskTest {
  public static final String DIR = "build/unitTest/";
  private GenerateStructureTask task;

  @Before
  public void setup() {
    deleteStructure(Path.of(DIR));
    Project project =
        ProjectBuilder.builder()
            .withProjectDir(new File("build/unitTest/"))
            .withGradleUserHomeDir(new File(DIR))
            .build();
    project.getTasks().create("test", GenerateStructureTask.class);

    task = (GenerateStructureTask) project.getTasks().getByName("test");
  }

  @Test
  public void shouldReturnProjectTypes() {
    // Arrange
    // Act
    List<GenerateStructureTask.ProjectType> types = task.getAvailableProjectTypes();
    // Assert
    assertEquals(Arrays.asList(GenerateStructureTask.ProjectType.values()), types);
  }

  @Test
  public void shouldReturnCoveragePluginTypes() {
    // Arrange
    // Act
    List<GenerateStructureTask.CoveragePlugin> types = task.getCoveragePlugins();
    // Assert
    assertEquals(Arrays.asList(GenerateStructureTask.CoveragePlugin.values()), types);
  }

  @Test
  public void shouldReturnMetricsOptions() {
    // Arrange
    // Act
    List<BooleanOption> types = task.getMetricsOptions();
    // Assert
    assertEquals(Arrays.asList(BooleanOption.values()), types);
  }

  @Test
  public void shouldReturnForceOptions() {
    // Arrange
    // Act
    List<BooleanOption> types = task.getForceOptions();
    // Assert
    assertEquals(Arrays.asList(BooleanOption.values()), types);
  }

  @Test
  public void shouldReturnJavaVersion() {
    // Arrange
    // Act
    List<JavaVersion> types = task.getJavaVersions();
    // Assert
    assertEquals(Arrays.asList(JavaVersion.values()), types);
  }

  @Test
  public void generateStructure() throws IOException, CleanException {
    // Arrange
    // Act
    task.generateStructureTask();
    // Assert
    assertTrue(new File("build/unitTest/README.md").exists());
    assertTrue(new File("build/unitTest/.gitignore").exists());
    assertTrue(new File("build/unitTest/build.gradle").exists());
    assertTrue(new File("build/unitTest/lombok.config").exists());
    assertTrue(new File("build/unitTest/main.gradle").exists());
    assertTrue(new File("build/unitTest/settings.gradle").exists());

    assertTrue(new File("build/unitTest/infrastructure/driven-adapters/").exists());
    assertTrue(new File("build/unitTest/infrastructure/entry-points").exists());
    assertTrue(new File("build/unitTest/infrastructure/helpers").exists());

    assertTrue(
        new File("build/unitTest/domain/model/src/main/java/co/com/bancolombia/model").exists());
    assertTrue(
        new File("build/unitTest/domain/model/src/test/java/co/com/bancolombia/model").exists());
    assertTrue(new File("build/unitTest/domain/model/build.gradle").exists());
    assertTrue(
        new File("build/unitTest/domain/usecase/src/main/java/co/com/bancolombia/usecase")
            .exists());
    assertTrue(
        new File("build/unitTest/domain/usecase/src/test/java/co/com/bancolombia/usecase")
            .exists());
    assertTrue(new File("build/unitTest/domain/usecase/build.gradle").exists());

    assertTrue(new File("build/unitTest/applications/app-service/build.gradle").exists());
    assertTrue(
        new File(
                "build/unitTest/applications/app-service/src/main/java/co/com/bancolombia/MainApplication.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/applications/app-service/src/main/java/co/com/bancolombia/config/UseCasesConfig.java")
            .exists());
    assertTrue(
        new File("build/unitTest/applications/app-service/src/main/java/co/com/bancolombia/config")
            .exists());
    assertTrue(
        new File("build/unitTest/applications/app-service/src/main/resources/application.yaml")
            .exists());
    assertTrue(
        new File("build/unitTest/applications/app-service/src/main/resources/log4j2.properties")
            .exists());
    assertTrue(
        new File("build/unitTest/applications/app-service/src/test/java/co/com/bancolombia")
            .exists());
  }

  @Test
  public void generateStructureReactiveWithCoberturaNoLombok() throws IOException, CleanException {
    // Arrange
    task.setPackage("test");
    task.setName("projectTest");
    task.setType(GenerateStructureTask.ProjectType.REACTIVE);
    task.setCoveragePlugin(GenerateStructureTask.CoveragePlugin.COBERTURA);
    task.setStatusLombok(BooleanOption.FALSE);
    task.setMetrics(BooleanOption.FALSE);
    task.setForce(BooleanOption.FALSE);
    task.setJavaVersion(JavaVersion.VERSION_11);
    // Act
    task.generateStructureTask();
    // Assert
    assertTrue(new File("build/unitTest/README.md").exists());
    assertTrue(new File("build/unitTest/.gitignore").exists());
    assertTrue(new File("build/unitTest/build.gradle").exists());
    assertFalse(new File("build/unitTest/lombok.config").exists());
    assertTrue(new File("build/unitTest/main.gradle").exists());
    assertTrue(new File("build/unitTest/settings.gradle").exists());

    assertTrue(new File("build/unitTest/infrastructure/driven-adapters/").exists());
    assertTrue(new File("build/unitTest/infrastructure/entry-points").exists());
    assertTrue(new File("build/unitTest/infrastructure/helpers").exists());

    assertTrue(new File("build/unitTest/domain/model/src/main/java/test/model").exists());
    assertTrue(new File("build/unitTest/domain/model/src/test/java/test/model").exists());
    assertTrue(new File("build/unitTest/domain/model/build.gradle").exists());
    assertTrue(new File("build/unitTest/domain/usecase/src/main/java/test/usecase").exists());
    assertTrue(new File("build/unitTest/domain/usecase/src/test/java/test/usecase").exists());
    assertTrue(new File("build/unitTest/domain/usecase/build.gradle").exists());

    assertTrue(new File("build/unitTest/applications/app-service/build.gradle").exists());
    assertTrue(
        new File("build/unitTest/applications/app-service/src/main/java/test/MainApplication.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/applications/app-service/src/main/java/test/config/UseCasesConfig.java")
            .exists());
    assertTrue(
        new File("build/unitTest/applications/app-service/src/main/java/test/config").exists());
    assertTrue(
        new File("build/unitTest/applications/app-service/src/main/resources/application.yaml")
            .exists());
    assertTrue(
        new File("build/unitTest/applications/app-service/src/main/resources/log4j2.properties")
            .exists());
    assertTrue(new File("build/unitTest/applications/app-service/src/test/java/test").exists());
  }

  @Test
  public void generateStructureOnExistingProject() throws IOException, CleanException {
    // Arrange
    task.generateStructureTask();
    // Act
    task.generateStructureTask();
    // Assert
    assertTrue(new File("build/unitTest/build.gradle").exists());
    assertTrue(new File("build/unitTest/gradle.properties").exists());
    assertTrue(new File("build/unitTest/main.gradle").exists());
  }

  @Test
  public void generateStructureOnExistingProjectNoLombok() throws IOException, CleanException {
    // Arrange
    task.setStatusLombok(BooleanOption.FALSE);
    task.generateStructureTask();
    // Act
    task.generateStructureTask();
    // Assert
    assertTrue(new File("build/unitTest/build.gradle").exists());
    assertTrue(new File("build/unitTest/gradle.properties").exists());
    assertTrue(new File("build/unitTest/main.gradle").exists());
    assertFalse(new File("build/unitTest/lombok.config").exists());
  }

  @Test
  public void shouldGetLombokOptions() {
    // Arrange
    // Act
    List<BooleanOption> options = task.getLombokOptions();
    // Assert
    assertEquals(2, options.size());
  }

  @AfterClass
  public static void clean() {
    deleteStructure(Path.of(DIR));
  }
}
