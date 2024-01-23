package co.com.bancolombia.task;

import static co.com.bancolombia.TestUtils.assertFilesExistsInDir;
import static co.com.bancolombia.TestUtils.createTask;
import static co.com.bancolombia.TestUtils.deleteStructure;
import static co.com.bancolombia.TestUtils.getTask;
import static co.com.bancolombia.TestUtils.getTestDir;
import static co.com.bancolombia.TestUtils.setupProject;

import co.com.bancolombia.exceptions.CleanException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class GenerateEntryPointTaskKotlinReactiveTest {
  private static final String TEST_DIR = getTestDir(GenerateEntryPointTaskKotlinReactiveTest.class);
  private static GenerateEntryPointTask task;

  @BeforeAll
  public static void setup() throws IOException, CleanException {
    deleteStructure(Path.of(TEST_DIR));
    Project project =
        setupProject(GenerateEntryPointTaskKotlinReactiveTest.class, GenerateStructureTask.class);

    GenerateStructureTask taskStructure = getTask(project, GenerateStructureTask.class);
    taskStructure.setType(GenerateStructureTask.ProjectType.REACTIVE);
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

  @Test
  void generateEntryPointRsocketResponder() throws IOException, CleanException {
    // Arrange
    task.setType("RSOCKET");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/entry-points/rsocket-responder/",
        "build.gradle.kts",
        "src/main/kotlin/co/com/bancolombia/controller/RsocketController.kt",
        "src/test/kotlin/co/com/bancolombia/controller");
  }

  @Test
  void generateEntryPointApiGraphql() throws IOException, CleanException {
    // Arrange
    task.setType("GRAPHQL");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/entry-points/graphql-api/",
        "build.gradle.kts",
        "src/main/kotlin/co/com/bancolombia/graphqlapi/ApiQueries.kt",
        "src/main/kotlin/co/com/bancolombia/graphqlapi/ApiMutations.kt");
  }

  @Test
  void generateEntryPointReactiveWebWithoutRouterFunctions() throws IOException, CleanException {
    // Arrange
    deleteStructure(Path.of(TEST_DIR, "/infrastructure/entry-points/reactive-web/"));
    task.setType("WEBFLUX");
    task.setRouter(AbstractCleanArchitectureDefaultTask.BooleanOption.FALSE);
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/entry-points/reactive-web/",
        "build.gradle.kts",
        "src/main/kotlin/co/com/bancolombia/api/ApiRest.kt");
  }

  @Test
  void generateEntryPointReactiveWebWithoutRouterFunctionsAndSwagger()
      throws IOException, CleanException {
    // Arrange
    deleteStructure(Path.of(TEST_DIR, "/infrastructure/entry-points/reactive-web/"));
    task.setType("WEBFLUX");
    task.setRouter(AbstractCleanArchitectureDefaultTask.BooleanOption.FALSE);
    task.setSwagger(AbstractCleanArchitectureDefaultTask.BooleanOption.TRUE);
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/entry-points/reactive-web/",
        "build.gradle.kts",
        "src/main/kotlin/co/com/bancolombia/api/ApiRest.kt",
        "src/main/kotlin/co/com/bancolombia/config/SpringFoxConfig.kt");
  }

  @Test
  void generateEntryPointReactiveWebWithRouterFunctions() throws IOException, CleanException {
    // Arrange
    task.setType("WEBFLUX");
    task.setRouter(AbstractCleanArchitectureDefaultTask.BooleanOption.TRUE);

    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/entry-points/reactive-web/",
        "build.gradle.kts",
        "src/main/kotlin/co/com/bancolombia/api/RouterRest.kt",
        "src/main/kotlin/co/com/bancolombia/api/Handler.kt");
  }

  @Test
  void generateEntryPointReactiveWebWithDefaultOptionFunctions()
      throws IOException, CleanException {
    // Arrange
    task.setType("WEBFLUX");

    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/entry-points/reactive-web/",
        "build.gradle.kts",
        "src/main/kotlin/co/com/bancolombia/api/RouterRest.kt",
        "src/main/kotlin/co/com/bancolombia/api/Handler.kt");
  }

  @Test
  void generateEntryPointAsyncEventHandler() throws IOException, CleanException {
    // Arrange
    task.setType("ASYNCEVENTHANDLER");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/entry-points/async-event-handler/",
        "build.gradle.kts",
        "src/main/kotlin/co/com/bancolombia/events/HandlerRegistryConfiguration.kt",
        "src/main/kotlin/co/com/bancolombia/events/handlers/EventsHandler.kt",
        "src/main/kotlin/co/com/bancolombia/events/handlers/CommandsHandler.kt",
        "src/main/kotlin/co/com/bancolombia/events/handlers/QueriesHandler.kt");
  }

  @Test
  void generateEntryPointMQListener() throws IOException, CleanException {
    // Arrange
    task.setType("MQ");

    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/entry-points/mq-listener/",
        "build.gradle.kts",
        "src/main/kotlin/co/com/bancolombia/mq/listener/SampleMQMessageListener.kt");
  }
}
