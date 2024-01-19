package co.com.bancolombia.task;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static org.junit.Assert.*;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.entrypoints.EntryPointRestMvcServer;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.file.SimplePathVisitor;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;

public class GenerateEntryPointTaskTest {
  public static final String SWAGGER_FILE = "src/test/resources/swagger/pet-store.yaml";
  private GenerateEntryPointTask task;

  @Before
  public void init() throws IOException, CleanException {
    deleteStructure(Path.of("build/unitTest"));
    setup(GenerateStructureTask.ProjectType.IMPERATIVE);
  }

  public void setup(GenerateStructureTask.ProjectType type) throws IOException, CleanException {
    Project project = ProjectBuilder.builder().withProjectDir(new File("build/unitTest")).build();
    deleteStructure(project.getProjectDir().toPath());
    project.getTasks().create("ca", GenerateStructureTask.class);
    GenerateStructureTask caTask = (GenerateStructureTask) project.getTasks().getByName("ca");
    caTask.setType(type);
    caTask.execute();

    ProjectBuilder.builder()
        .withProjectDir(new File("build/unitTest/applications/app-service"))
        .withName(APP_SERVICE)
        .withParent(project)
        .build();

    project.getTasks().create("test", GenerateEntryPointTask.class);
    task = (GenerateEntryPointTask) project.getTasks().getByName("test");
  }

  private void deleteStructure(Path sourcePath) {
    try {
      Files.walkFileTree(
          sourcePath,
          new SimplePathVisitor() {
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
              Files.delete(dir);
              return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
                throws IOException {
              Files.delete(file);
              return FileVisitResult.CONTINUE;
            }
          });
    } catch (IOException e) {
      System.out.println("error delete Structure " + e.getMessage());
    }
  }

  // Assert
  @Test(expected = IllegalArgumentException.class)
  public void shouldHandleErrorWhenNoType() throws IOException, CleanException {
    // Arrange
    task.setType(null);
    // Act
    task.execute();
  }

  // Assert
  @Test(expected = IllegalArgumentException.class)
  public void shouldHandleErrorWhenNoName() throws IOException, CleanException {
    // Arrange
    task.setType("GENERIC");
    // Act
    task.execute();
  }

  // Assert
  @Test(expected = IllegalArgumentException.class)
  public void shouldHandleErrorWhenEmptyName() throws IOException, CleanException {
    // Arrange
    task.setType("GENERIC");
    task.setName("");
    // Act
    task.execute();
  }

  @Test
  public void generateEntryPointGeneric() throws IOException, CleanException {
    // Arrange
    task.setType("GENERIC");
    task.setName("MyEntryPoint");
    // Act
    task.execute();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/entry-points/my-entry-point/build.gradle")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/my-entry-point/src/main/java/co/com/bancolombia/myentrypoint")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/my-entry-point/src/test/java/co/com/bancolombia/myentrypoint")
            .exists());
  }

  @Test
  public void generateEntryPointRsocketResponder() throws IOException, CleanException {
    // Arrange
    setup(GenerateStructureTask.ProjectType.REACTIVE);
    task.setType("RSOCKET");
    // Act
    task.execute();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/entry-points/rsocket-responder/build.gradle")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/rsocket-responder/src/main/java/co/com/bancolombia/controller/RsocketController.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/rsocket-responder/src/test/java/co/com/bancolombia/controller")
            .exists());
  }

  @Test
  public void generateEntryPointApiGraphql() throws IOException, CleanException {
    // Arrange
    setup(GenerateStructureTask.ProjectType.REACTIVE);
    task.setType("GRAPHQL");
    // Act
    task.execute();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/entry-points/graphql-api/build.gradle").exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/graphql-api/src/main/java/co/com/bancolombia/graphqlapi/ApiQueries.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/graphql-api/src/main/java/co/com/bancolombia/graphqlapi/ApiMutations.java")
            .exists());
  }

  @Test
  public void generateEntryPointApiRestWithDefaultServer() throws IOException, CleanException {
    // Arrange
    task.setType("RESTMVC");
    // Act
    task.execute();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/entry-points/api-rest/build.gradle").exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/api-rest/src/main/java/co/com/bancolombia/api/ApiRest.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/api-rest/src/test/java/co/com/bancolombia/api/ApiRestTest.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/api-rest/src/test/java/co/com/bancolombia/api")
            .exists());
  }

  @Test
  public void generateEntryPointApiRestWithDefaultServerFromSwaggerFile()
      throws IOException, CleanException {
    // Arrange
    task.setType("RESTMVC");
    task.setFromSwagger(SWAGGER_FILE);
    // Act
    task.execute();
    // Assert
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/api-rest/src/main/java/co/com/bancolombia/api/PetApiController.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/api-rest/src/main/java/co/com/bancolombia/api/model/Pet.java")
            .exists());
  }

  @Test
  public void generateEntryPointApiRestWithDefaultServerAndSwagger()
      throws IOException, CleanException {
    // Arrange
    task.setType("RESTMVC");
    task.setSwagger(AbstractCleanArchitectureDefaultTask.BooleanOption.TRUE);
    // Act
    task.execute();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/entry-points/api-rest/build.gradle").exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/api-rest/src/main/java/co/com/bancolombia/api/ApiRest.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/api-rest/src/test/java/co/com/bancolombia/api")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/api-rest/src/main/java/co/com/bancolombia/config/SpringFoxConfig.java")
            .exists());
  }

  @Test
  public void generateEntryPointApiRestWithUndertowServer() throws IOException, CleanException {
    // Arrange
    task.setType("RESTMVC");
    task.setServer(EntryPointRestMvcServer.Server.UNDERTOW);
    // Act
    task.execute();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/entry-points/api-rest/build.gradle").exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/api-rest/src/main/java/co/com/bancolombia/api/ApiRest.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/api-rest/src/test/java/co/com/bancolombia/api")
            .exists());

    assertTrue(
        FileUtils.readFileToString(
                new File("build/unitTest/infrastructure/entry-points/api-rest/build.gradle"),
                StandardCharsets.UTF_8)
            .contains("spring-boot-starter-undertow"));
    assertTrue(
        FileUtils.readFileToString(
                new File("build/unitTest/infrastructure/entry-points/api-rest/build.gradle"),
                StandardCharsets.UTF_8)
            .contains(
                "implementation.exclude group: 'org.springframework.boot', module: 'spring-boot-starter-tomcat'"));
  }

  @Test
  public void generateEntryPointApiRestWithJettyServer() throws IOException, CleanException {
    // Arrange
    task.setType("RESTMVC");
    task.setServer(EntryPointRestMvcServer.Server.JETTY);
    // Act
    task.execute();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/entry-points/api-rest/build.gradle").exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/api-rest/src/main/java/co/com/bancolombia/api/ApiRest.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/api-rest/src/test/java/co/com/bancolombia/api")
            .exists());

    assertTrue(
        FileUtils.readFileToString(
                new File("build/unitTest/infrastructure/entry-points/api-rest/build.gradle"),
                StandardCharsets.UTF_8)
            .contains("spring-boot-starter-jetty"));
    assertTrue(
        FileUtils.readFileToString(
                new File("build/unitTest/infrastructure/entry-points/api-rest/build.gradle"),
                StandardCharsets.UTF_8)
            .contains(
                "implementation.exclude group: 'org.springframework.boot', module: 'spring-boot-starter-tomcat'"));
  }

  @Test
  public void generateEntryPointApiRestWithTomcatServer() throws IOException, CleanException {
    // Arrange
    task.setType("RESTMVC");
    task.setServer(EntryPointRestMvcServer.Server.TOMCAT);
    // Act
    task.execute();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/entry-points/api-rest/build.gradle").exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/api-rest/src/main/java/co/com/bancolombia/api/ApiRest.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/api-rest/src/test/java/co/com/bancolombia/api")
            .exists());

    assertFalse(
        FileUtils.readFileToString(
                new File("build/unitTest/applications/app-service/build.gradle"),
                StandardCharsets.UTF_8)
            .contains(
                "compile.exclude group: \"org.springframework.boot\", module:\"spring-boot-starter-tomcat\""));
  }

  @Test
  public void generateEntryPointReactiveWebWithoutRouterFunctions()
      throws IOException, CleanException {
    // Arrange
    setup(GenerateStructureTask.ProjectType.REACTIVE);
    task.setType("WEBFLUX");
    task.setRouter(AbstractCleanArchitectureDefaultTask.BooleanOption.FALSE);
    // Act
    task.execute();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/entry-points/reactive-web/build.gradle").exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/reactive-web/src/main/java/co/com/bancolombia/api/ApiRest.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/reactive-web/src/test/java/co/com/bancolombia/api/ApiRestTest.java")
            .exists());
    assertFalse(
        new File(
                "build/unitTest/infrastructure/entry-points/reactive-web/src/main/java/co/com/bancolombia/api/RouterRest.java")
            .exists());
    assertFalse(
        new File(
                "build/unitTest/infrastructure/entry-points/reactive-web/src/main/java/co/com/bancolombia/api/Handler.java")
            .exists());
  }

  @Test
  public void generateEntryPointReactiveWebWithoutRouterFunctionsFromSwagger()
      throws IOException, CleanException {
    // Arrange
    setup(GenerateStructureTask.ProjectType.REACTIVE);
    task.setType("WEBFLUX");
    task.setFromSwagger(SWAGGER_FILE);
    task.setRouter(AbstractCleanArchitectureDefaultTask.BooleanOption.FALSE);
    // Act
    task.execute();
    // Assert
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/reactive-web/src/main/java/co/com/bancolombia/api/PetApiController.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/reactive-web/src/main/java/co/com/bancolombia/api/model/Pet.java")
            .exists());
  }

  @Test
  public void generateEntryPointReactiveWebWithoutRouterFunctionsAndSwagger()
      throws IOException, CleanException {
    // Arrange
    setup(GenerateStructureTask.ProjectType.REACTIVE);
    task.setType("WEBFLUX");
    task.setRouter(AbstractCleanArchitectureDefaultTask.BooleanOption.FALSE);
    task.setSwagger(AbstractCleanArchitectureDefaultTask.BooleanOption.TRUE);
    // Act
    task.execute();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/entry-points/reactive-web/build.gradle").exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/reactive-web/src/main/java/co/com/bancolombia/api/ApiRest.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/reactive-web/src/test/java/co/com/bancolombia/api/ApiRestTest.java")
            .exists());
    assertFalse(
        new File(
                "build/unitTest/infrastructure/entry-points/reactive-web/src/main/java/co/com/bancolombia/api/RouterRest.java")
            .exists());
    assertFalse(
        new File(
                "build/unitTest/infrastructure/entry-points/reactive-web/src/main/java/co/com/bancolombia/api/Handler.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/reactive-web/src/main/java/co/com/bancolombia/config/SpringFoxConfig.java")
            .exists());
  }

  @Test
  public void generateEntryPointReactiveWebWithRouterFunctions()
      throws IOException, CleanException {
    // Arrange
    setup(GenerateStructureTask.ProjectType.REACTIVE);
    task.setType("WEBFLUX");
    task.setRouter(AbstractCleanArchitectureDefaultTask.BooleanOption.TRUE);

    // Act
    task.execute();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/entry-points/reactive-web/build.gradle").exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/reactive-web/src/main/java/co/com/bancolombia/api/RouterRest.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/reactive-web/src/main/java/co/com/bancolombia/api/Handler.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/reactive-web/src/test/java/co/com/bancolombia/api/RouterRestTest.java")
            .exists());
  }

  @Test
  public void generateEntryPointReactiveWebWithRouterFunctionsFromSwagger()
      throws IOException, CleanException {
    // Arrange
    setup(GenerateStructureTask.ProjectType.REACTIVE);
    task.setType("WEBFLUX");
    task.setFromSwagger(SWAGGER_FILE);
    task.setRouter(AbstractCleanArchitectureDefaultTask.BooleanOption.TRUE);

    // Act
    task.execute();
    // Assert
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/reactive-web/src/main/java/co/com/bancolombia/api/PetApiRouter.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/reactive-web/src/main/java/co/com/bancolombia/api/PetApiHandler.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/reactive-web/src/main/java/co/com/bancolombia/api/model/Pet.java")
            .exists());
  }

  @Test
  public void generateEntryPointReactiveWebWithDefaultOptionFunctions()
      throws IOException, CleanException {
    // Arrange
    setup(GenerateStructureTask.ProjectType.REACTIVE);
    task.setType("WEBFLUX");

    // Act
    task.execute();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/entry-points/reactive-web/build.gradle").exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/reactive-web/src/main/java/co/com/bancolombia/api/RouterRest.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/reactive-web/src/main/java/co/com/bancolombia/api/Handler.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/reactive-web/src/test/java/co/com/bancolombia/api/RouterRestTest.java")
            .exists());
  }

  @Test
  public void generateEntryPointAsyncEventHandler() throws IOException, CleanException {
    // Arrange
    setup(GenerateStructureTask.ProjectType.REACTIVE);
    task.setType("ASYNCEVENTHANDLER");
    // Act
    task.execute();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/entry-points/async-event-handler/build.gradle")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/async-event-handler/src/main/java/co/com/bancolombia/events/HandlerRegistryConfiguration.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/async-event-handler/src/main/java/co/com/bancolombia/events/handlers/EventsHandler.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/async-event-handler/src/main/java/co/com/bancolombia/events/handlers/CommandsHandler.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/async-event-handler/src/main/java/co/com/bancolombia/events/handlers/QueriesHandler.java")
            .exists());
  }

  @Test
  public void generateEntryPointMQListener() throws IOException, CleanException {
    // Arrange
    setup(GenerateStructureTask.ProjectType.REACTIVE);
    task.setType("MQ");

    // Act
    task.execute();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/entry-points/mq-listener/build.gradle").exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/mq-listener/src/main/java/co/com/bancolombia/mq/listener/SampleMQMessageListener.java")
            .exists());
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

  @Test
  public void generateEntryPointSQSListener() throws IOException, CleanException {
    // Arrange
    setup(GenerateStructureTask.ProjectType.REACTIVE);
    task.setType("SQS");

    // Act
    task.execute();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/entry-points/sqs-listener/build.gradle").exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/sqs-listener/src/main/java/co/com/bancolombia/sqs/listener/SQSProcessor.java")
            .exists());
  }

  @Test
  public void generateEntryPointKafkaConsumer() throws IOException, CleanException {
    // Arrange
    setup(GenerateStructureTask.ProjectType.REACTIVE);
    task.setType("KAFKA");

    // Act
    task.execute();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/entry-points/kafka-consumer/build.gradle")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/entry-points/kafka-consumer/src/main/java/co/com/bancolombia/kafka/consumer/KafkaConsumer.java")
            .exists());
  }
}
