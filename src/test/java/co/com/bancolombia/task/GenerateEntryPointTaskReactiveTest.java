package co.com.bancolombia.task;

import static co.com.bancolombia.TestUtils.assertFilesExistsInDir;
import static co.com.bancolombia.TestUtils.createTask;
import static co.com.bancolombia.TestUtils.deleteStructure;
import static co.com.bancolombia.TestUtils.getTask;
import static co.com.bancolombia.TestUtils.getTestDir;
import static co.com.bancolombia.TestUtils.setupProject;
import static co.com.bancolombia.task.AbstractCleanArchitectureDefaultTask.BooleanOption.TRUE;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.entrypoints.EntryPointWebflux;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class GenerateEntryPointTaskReactiveTest {
  private static final String TEST_DIR = getTestDir(GenerateEntryPointTaskReactiveTest.class);
  public static final String SWAGGER_FILE = "src/test/resources/swagger/pet-store.yaml";
  private static GenerateEntryPointTask task;

  @BeforeAll
  static void setup() throws IOException, CleanException {
    deleteStructure(Path.of(TEST_DIR));
    Project project =
        setupProject(GenerateEntryPointTaskReactiveTest.class, GenerateStructureTask.class);

    GenerateStructureTask taskStructure = getTask(project, GenerateStructureTask.class);
    taskStructure.setType(GenerateStructureTask.ProjectType.REACTIVE);
    taskStructure.execute();

    ProjectBuilder.builder()
        .withName("app-service")
        .withProjectDir(new File(TEST_DIR + "/applications/app-service"))
        .withParent(project)
        .build();

    task = createTask(project, GenerateEntryPointTask.class);
  }

  @AfterAll
  static void tearDown() {
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
        "build.gradle",
        "src/main/java/co/com/bancolombia/controller/RsocketController.java",
        "src/test/java/co/com/bancolombia/controller");
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
        "build.gradle",
        "src/main/java/co/com/bancolombia/graphqlapi/ApiQueries.java",
        "src/main/java/co/com/bancolombia/graphqlapi/ApiMutations.java");
  }

  @Test
  void generateEntryPointReactiveWebWithoutRouterFunctions() throws IOException, CleanException {
    // Arrange
    deleteStructure(Path.of(TEST_DIR, "/infrastructure/entry-points/reactive-web"));
    task.setType("WEBFLUX");
    task.setRouter(AbstractCleanArchitectureDefaultTask.BooleanOption.FALSE);
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/entry-points/reactive-web",
        "build.gradle",
        "src/main/java/co/com/bancolombia/api/ApiRest.java",
        "src/test/java/co/com/bancolombia/api/ApiRestTest.java");
  }

  @Test
  void generateEntryPointReactiveWebWithoutRouterFunctionsFromSwagger()
      throws IOException, CleanException {
    // Arrange
    task.setType("WEBFLUX");
    task.setFromSwagger(SWAGGER_FILE);
    task.setRouter(AbstractCleanArchitectureDefaultTask.BooleanOption.FALSE);
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/entry-points/reactive-web/",
        "src/main/java/co/com/bancolombia/api/PetApiController.java",
        "src/main/java/co/com/bancolombia/api/model/Pet.java");
  }

  @Test
  void generateEntryPointReactiveWebWithoutRouterFunctionsAndSwagger()
      throws IOException, CleanException {
    // Arrange
    deleteStructure(Path.of(TEST_DIR, "/infrastructure/entry-points/reactive-web"));
    task.setType("WEBFLUX");
    task.setRouter(AbstractCleanArchitectureDefaultTask.BooleanOption.FALSE);
    task.setSwagger(AbstractCleanArchitectureDefaultTask.BooleanOption.TRUE);
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/entry-points/reactive-web",
        "build.gradle",
        "src/main/java/co/com/bancolombia/api/ApiRest.java",
        "src/test/java/co/com/bancolombia/api/ApiRestTest.java",
        "src/main/java/co/com/bancolombia/config/SpringFoxConfig.java");
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
        "build.gradle",
        "src/main/java/co/com/bancolombia/api/RouterRest.java",
        "src/main/java/co/com/bancolombia/api/Handler.java",
        "src/test/java/co/com/bancolombia/api/RouterRestTest.java");
  }

  @Test
  void generateEntryPointReactiveWebWithRouterFunctionsFromSwagger()
      throws IOException, CleanException {
    // Arrange
    task.setType("WEBFLUX");
    task.setFromSwagger(SWAGGER_FILE);
    task.setRouter(AbstractCleanArchitectureDefaultTask.BooleanOption.TRUE);

    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/entry-points/reactive-web/",
        "src/main/java/co/com/bancolombia/api/PetApiRouter.java",
        "src/main/java/co/com/bancolombia/api/PetApiHandler.java",
        "src/main/java/co/com/bancolombia/api/model/Pet.java");
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
        "build.gradle",
        "src/main/java/co/com/bancolombia/api/RouterRest.java",
        "src/main/java/co/com/bancolombia/api/Handler.java",
        "src/test/java/co/com/bancolombia/api/RouterRestTest.java");
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
        "build.gradle",
        "src/main/java/co/com/bancolombia/events/HandlerRegistryConfiguration.java",
        "src/main/java/co/com/bancolombia/events/handlers/EventsHandler.java",
        "src/main/java/co/com/bancolombia/events/handlers/CommandsHandler.java",
        "src/main/java/co/com/bancolombia/events/handlers/QueriesHandler.java");
  }

  @Test
  void generateEntryPointAsyncEventHandlerForEda() throws IOException, CleanException {
    // Arrange
    task.setType("ASYNCEVENTHANDLER");
    task.setTech("rabbitmq,kafka");
    task.setEda(TRUE);
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/entry-points/async-event-handler/",
        "build.gradle",
        "src/main/java/co/com/bancolombia/events/HandlerRegistryConfiguration.java",
        "src/main/java/co/com/bancolombia/events/handlers/EventsHandler.java",
        "src/main/java/co/com/bancolombia/events/handlers/CommandsHandler.java",
        "src/main/java/co/com/bancolombia/events/handlers/QueriesHandler.java");
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
        "build.gradle",
        "src/main/java/co/com/bancolombia/mq/listener/SampleMQMessageListener.java");
  }

  @Test
  void generateEntryPointSQSListener() throws IOException, CleanException {
    // Arrange
    task.setType("SQS");

    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/entry-points/sqs-listener/",
        "build.gradle",
        "src/main/java/co/com/bancolombia/sqs/listener/SQSProcessor.java");
  }

  @Test
  void generateEntryPointKafkaConsumer() throws IOException, CleanException {
    // Arrange
    task.setType("KAFKA");

    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/entry-points/kafka-consumer/",
        "build.gradle",
        "src/main/java/co/com/bancolombia/kafka/consumer/KafkaConsumer.java");
  }

  @Test
  void generateEntryPointReactiveWebWithNoneStrategy() throws IOException, CleanException {

    // Arrange
    task.setType("WEBFLUX");
    task.setVersioning(EntryPointWebflux.VersioningStrategy.NONE);
    task.setSwagger(AbstractCleanArchitectureDefaultTask.BooleanOption.FALSE);

    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/entry-points/reactive-web/",
        "build.gradle",
        "src/main/java/co/com/bancolombia/api/Handler.java",
        "src/main/java/co/com/bancolombia/api/RouterRest.java",
        "src/test/java/co/com/bancolombia/api/RouterRestTest.java");
  }
}
