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

public class GenerateEntryPointTaskImperativeTest {
  private static final String TEST_DIR = getTestDir(GenerateEntryPointTaskImperativeTest.class);
  public static final String SWAGGER_FILE = "src/test/resources/swagger/pet-store.yaml";
  private static GenerateEntryPointTask task;

  @BeforeAll
  public static void setup() throws IOException, CleanException {
    deleteStructure(Path.of(TEST_DIR));
    Project project =
        setupProject(GenerateEntryPointTaskImperativeTest.class, GenerateStructureTask.class);

    GenerateStructureTask taskStructure = getTask(project, GenerateStructureTask.class);
    taskStructure.setType(GenerateStructureTask.ProjectType.IMPERATIVE);
    taskStructure.setLanguage(GenerateStructureTask.Language.JAVA);
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
  void shouldHandleErrorWhenNoType() {
    // Arrange
    task.setType(null);
    // Act
    assertThrows(IllegalArgumentException.class, () -> task.execute());
  }

  // Assert
  @Test
  void shouldHandleErrorWhenNoName() {
    // Arrange
    task.setType("GENERIC");
    task.setName(null);
    // Act
    assertThrows(IllegalArgumentException.class, () -> task.execute());
  }

  // Assert
  @Test
  void shouldHandleErrorWhenEmptyName() {
    // Arrange
    task.setType("GENERIC");
    task.setName("");
    // Act
    assertThrows(IllegalArgumentException.class, () -> task.execute());
  }

  @Test
  void generateEntryPointGeneric() throws IOException, CleanException {
    // Arrange
    task.setType("GENERIC");
    task.setName("MyEntryPoint");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/entry-points/my-entry-point/",
        "build.gradle",
        "src/main/java/co/com/bancolombia/myentrypoint",
        "src/test/java/co/com/bancolombia/myentrypoint");
  }

  @Test
  void generateEntryPointApiRestWithDefaultServer() throws IOException, CleanException {
    // Arrange
    task.setType("RESTMVC");
    task.setServer(EntryPointRestMvcServer.Server.UNDERTOW);
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/entry-points/api-rest/",
        "build.gradle",
        "src/main/java/co/com/bancolombia/api/ApiRest.java",
        "src/test/java/co/com/bancolombia/api/ApiRestTest.java",
        "src/test/java/co/com/bancolombia/api");
  }

  @Test
  void generateEntryPointApiRestWithDefaultServerFromSwaggerFile()
      throws IOException, CleanException {
    // Arrange
    task.setType("RESTMVC");
    task.setServer(EntryPointRestMvcServer.Server.UNDERTOW);
    task.setFromSwagger(SWAGGER_FILE);
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/entry-points/api-rest/",
        "src/main/java/co/com/bancolombia/api/PetApiController.java",
        "src/main/java/co/com/bancolombia/api/model/Pet.java");
  }

  @Test
  void generateEntryPointApiRestWithDefaultServerAndSwagger() throws IOException, CleanException {
    // Arrange
    task.setType("RESTMVC");
    task.setServer(EntryPointRestMvcServer.Server.UNDERTOW);
    task.setSwagger(AbstractCleanArchitectureDefaultTask.BooleanOption.TRUE);
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/entry-points/api-rest/",
        "build.gradle",
        "src/main/java/co/com/bancolombia/api/ApiRest.java",
        "src/test/java/co/com/bancolombia/api",
        "src/main/java/co/com/bancolombia/config/SpringFoxConfig.java");
  }

  @Test
  void generateEntryPointApiRestWithUndertowServer() throws IOException, CleanException {
    // Arrange
    task.setType("RESTMVC");
    task.setServer(EntryPointRestMvcServer.Server.UNDERTOW);
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/entry-points/api-rest/",
        "build.gradle",
        "src/main/java/co/com/bancolombia/api/ApiRest.java",
        "src/test/java/co/com/bancolombia/api");

    assertFileContains(
        TEST_DIR + "/infrastructure/entry-points/api-rest/build.gradle",
        "spring-boot-starter-undertow",
        "implementation.exclude group: 'org.springframework.boot', module: 'spring-boot-starter-tomcat'");
  }

  @Test
  void generateEntryPointApiRestWithJettyServer() throws IOException, CleanException {
    // Arrange
    task.setType("RESTMVC");
    task.setServer(EntryPointRestMvcServer.Server.JETTY);
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/entry-points/api-rest/",
        "build.gradle",
        "src/main/java/co/com/bancolombia/api/ApiRest.java",
        "src/test/java/co/com/bancolombia/api");
  }

  @Test
  void generateEntryPointApiRestWithTomcatServer() throws IOException, CleanException {
    // Arrange
    task.setType("RESTMVC");
    task.setServer(EntryPointRestMvcServer.Server.TOMCAT);
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/entry-points/api-rest/",
        "build.gradle",
        "src/main/java/co/com/bancolombia/api/ApiRest.java",
        "src/test/java/co/com/bancolombia/api");
  }

  @Test
  void shouldGetServerOptions() {
    // Arrange
    // Act
    List<EntryPointRestMvcServer.Server> options = task.getServerOptions();
    // Assert
    assertEquals(3, options.size());
  }

  @Test
  void shouldGetRouterOptions() {
    // Arrange
    // Act
    List<AbstractCleanArchitectureDefaultTask.BooleanOption> options = task.getRoutersOptions();
    // Assert
    assertEquals(2, options.size());
  }
}
