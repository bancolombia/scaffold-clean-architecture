package co.com.bancolombia.task;

import static co.com.bancolombia.utils.CAAssert.assertFilesExistsInDir;
import static co.com.bancolombia.utils.FileUtilsTest.deleteStructure;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
  public static final String DIR = "build/unitTestCa/";
  private GenerateStructureTask task;

  @Before
  public void setup() {
    deleteStructure(Path.of(DIR));
    Project project =
        ProjectBuilder.builder()
            .withProjectDir(new File(DIR))
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
    task.execute();
    // Assert
    assertFilesExistsInDir(
        DIR,
        "README.md",
        ".gitignore",
        "build.gradle",
        "lombok.config",
        "main.gradle",
        "settings.gradle",
        "gradlew",
        "gradle/wrapper/gradle-wrapper.properties",
        "infrastructure/driven-adapters/",
        "infrastructure/entry-points",
        "infrastructure/helpers",
        "domain/model/src/main/java/co/com/bancolombia/model",
        "domain/model/src/test/java/co/com/bancolombia/model",
        "domain/model/build.gradle",
        "domain/usecase/src/main/java/co/com/bancolombia/usecase",
        "domain/usecase/src/test/java/co/com/bancolombia/usecase",
        "domain/usecase/build.gradle",
        "applications/app-service/build.gradle",
        "applications/app-service/src/main/java/co/com/bancolombia/MainApplication.java",
        "applications/app-service/src/main/java/co/com/bancolombia/config/UseCasesConfig.java",
        "applications/app-service/src/main/java/co/com/bancolombia/config",
        "applications/app-service/src/main/resources/application.yaml",
        "applications/app-service/src/main/resources/log4j2.properties",
        "applications/app-service/src/test/java/co/com/bancolombia");
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
    task.setJavaVersion(JavaVersion.VERSION_17);
    // Act
    task.execute();
    // Assert
    assertFalse(new File(DIR + "lombok.config").exists());
    assertFilesExistsInDir(
        DIR,
        "README.md",
        ".gitignore",
        "build.gradle",
        "main.gradle",
        "settings.gradle",
        "infrastructure/driven-adapters/",
        "infrastructure/entry-points",
        "infrastructure/helpers",
        "domain/model/src/main/java/test/model",
        "domain/model/src/test/java/test/model",
        "domain/model/build.gradle",
        "domain/usecase/src/main/java/test/usecase",
        "domain/usecase/src/test/java/test/usecase",
        "domain/usecase/build.gradle",
        "applications/app-service/build.gradle",
        "applications/app-service/src/main/java/test/MainApplication.java",
        "applications/app-service/src/main/java/test/config/UseCasesConfig.java",
        "applications/app-service/src/main/java/test/config",
        "applications/app-service/src/main/resources/application.yaml",
        "applications/app-service/src/main/resources/log4j2.properties",
        "applications/app-service/src/test/java/test");
  }

  @Test
  public void generateStructureOnExistingProject() throws IOException, CleanException {
    // Arrange
    task.execute();
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(DIR, "build.gradle", "gradle.properties", "main.gradle");
  }

  @Test
  public void generateStructureOnExistingProjectNoLombok() throws IOException, CleanException {
    // Arrange
    task.setStatusLombok(BooleanOption.FALSE);
    task.execute();
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(DIR, "build.gradle", "gradle.properties", "main.gradle");
    assertFalse(new File(DIR + "lombok.config").exists());
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
