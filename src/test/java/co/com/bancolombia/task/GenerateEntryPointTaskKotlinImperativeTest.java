package co.com.bancolombia.task;

import static co.com.bancolombia.TestUtils.assertFileContains;
import static co.com.bancolombia.TestUtils.assertFilesExistsInDir;
import static co.com.bancolombia.TestUtils.createTask;
import static co.com.bancolombia.TestUtils.deleteStructure;
import static co.com.bancolombia.TestUtils.getTask;
import static co.com.bancolombia.TestUtils.getTestDir;
import static co.com.bancolombia.TestUtils.setupProject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.entrypoints.EntryPointRestMvcServer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class GenerateEntryPointTaskKotlinImperativeTest {
  private static final String TEST_DIR =
      getTestDir(GenerateEntryPointTaskKotlinImperativeTest.class);
  private static GenerateEntryPointTask task;

  @BeforeAll
  public static void setup() throws IOException, CleanException {
    deleteStructure(Path.of(TEST_DIR));
    Project project =
        setupProject(GenerateEntryPointTaskKotlinImperativeTest.class, GenerateStructureTask.class);

    GenerateStructureTask taskStructure = getTask(project, GenerateStructureTask.class);
    taskStructure.setType(GenerateStructureTask.ProjectType.IMPERATIVE);
    taskStructure.setLanguage(GenerateStructureTask.Language.KOTLIN);
    taskStructure.execute();

    ProjectBuilder.builder()
        .withName("app-service")
        .withProjectDir(new File(TEST_DIR + "/applications/app-service"))
        .withParent(project)
        .build();

    task = createTask(project, GenerateEntryPointTask.class);
  }

  @AfterAll
  public static void tearDown() {
    deleteStructure(Path.of(TEST_DIR));
  }

  // Assert
  @Test
  public void shouldHandleErrorWhenNoType() {
    // Arrange
    task.setType(null);
    // Act
    assertThrows(IllegalArgumentException.class, () -> task.execute());
  }

  // Assert
  @Test
  public void shouldHandleErrorWhenNoName() {
    // Arrange
    task.setType("GENERIC");
    task.setName(null);
    // Act
    assertThrows(IllegalArgumentException.class, () -> task.execute());
  }

  // Assert
  @Test
  public void shouldHandleErrorWhenEmptyName() {
    // Arrange
    task.setType("GENERIC");
    task.setName("");
    // Act
    assertThrows(IllegalArgumentException.class, () -> task.execute());
  }

  @Test
  public void generateEntryPointGeneric() throws IOException, CleanException {
    // Arrange
    task.setType("GENERIC");
    task.setName("MyEntryPoint");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/entry-points/my-entry-point/",
        "build.gradle.kts",
        "src/main/kotlin/co/com/bancolombia/myentrypoint",
        "src/test/kotlin/co/com/bancolombia/myentrypoint");
  }

  @Test
  public void generateEntryPointApiRestWithDefaultServer() throws IOException, CleanException {
    // Arrange
    task.setType("RESTMVC");
    task.setServer(EntryPointRestMvcServer.Server.UNDERTOW);
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/entry-points/api-rest/",
        "build.gradle.kts",
        "src/main/kotlin/co/com/bancolombia/api/ApiRest.kt",
        "src/test/kotlin/co/com/bancolombia/api");
  }

  @Test
  public void generateEntryPointApiRestWithDefaultServerAndSwagger()
      throws IOException, CleanException {
    // Arrange
    task.setType("RESTMVC");
    task.setServer(EntryPointRestMvcServer.Server.UNDERTOW);
    task.setSwagger(AbstractCleanArchitectureDefaultTask.BooleanOption.TRUE);
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/entry-points/api-rest/",
        "build.gradle.kts",
        "src/main/kotlin/co/com/bancolombia/api/ApiRest.kt",
        "src/test/kotlin/co/com/bancolombia/api",
        "src/main/kotlin/co/com/bancolombia/config/SpringFoxConfig.kt");
  }

  @Test
  public void generateEntryPointApiRestWithUndertowServer() throws IOException, CleanException {
    // Arrange
    task.setType("RESTMVC");
    task.setServer(EntryPointRestMvcServer.Server.UNDERTOW);
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/entry-points/api-rest/",
        "build.gradle.kts",
        "src/main/kotlin/co/com/bancolombia/api/ApiRest.kt",
        "src/test/kotlin/co/com/bancolombia/api");

    assertFileContains(
        TEST_DIR + "/infrastructure/entry-points/api-rest/build.gradle.kts",
        "spring-boot-starter-undertow",
        "exclude(group = \"org.springframework.boot\", module = \"spring-boot-starter-tomcat\")");
  }

  @Test
  public void generateEntryPointApiRestWithJettyServer() throws IOException, CleanException {
    // Arrange
    task.setType("RESTMVC");
    task.setServer(EntryPointRestMvcServer.Server.JETTY);
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/entry-points/api-rest/",
        "build.gradle.kts",
        "src/main/kotlin/co/com/bancolombia/api/ApiRest.kt",
        "src/test/kotlin/co/com/bancolombia/api");
    assertFileContains(
        TEST_DIR + "/infrastructure/entry-points/api-rest/build.gradle.kts",
        "spring-boot-starter-jetty",
        "exclude(group = \"org.springframework.boot\", module = \"spring-boot-starter-tomcat\")");
  }

  @Test
  public void generateEntryPointApiRestWithTomcatServer() throws IOException, CleanException {
    // Arrange
    task.setType("RESTMVC");
    task.setServer(EntryPointRestMvcServer.Server.TOMCAT);
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/entry-points/api-rest/",
        "build.gradle.kts",
        "src/main/kotlin/co/com/bancolombia/api/ApiRest.kt",
        "src/test/kotlin/co/com/bancolombia/api");
  }

  @Test
  public void shouldGetServerOptions() {
    // Arrange
    // Act
    List<EntryPointRestMvcServer.Server> options = task.getServerOptions();
    // Assert
    assertEquals(3, options.size());
  }

  @Test
  public void shouldGetRouterOptions() {
    // Arrange
    // Act
    List<AbstractCleanArchitectureDefaultTask.BooleanOption> options = task.getRoutersOptions();
    // Assert
    assertEquals(2, options.size());
  }
}
