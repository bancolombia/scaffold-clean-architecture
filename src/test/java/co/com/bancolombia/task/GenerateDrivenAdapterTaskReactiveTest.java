package co.com.bancolombia.task;

import static co.com.bancolombia.TestUtils.assertFileContains;
import static co.com.bancolombia.TestUtils.assertFilesExistsInDir;
import static co.com.bancolombia.TestUtils.createTask;
import static co.com.bancolombia.TestUtils.deleteStructure;
import static co.com.bancolombia.TestUtils.getTask;
import static co.com.bancolombia.TestUtils.getTestDir;
import static co.com.bancolombia.TestUtils.setupProject;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.exceptions.ValidationException;
import co.com.bancolombia.factory.adapters.DrivenAdapterRedis;
import co.com.bancolombia.factory.adapters.DrivenAdapterSecrets;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class GenerateDrivenAdapterTaskReactiveTest {
  private static final String TEST_DIR = getTestDir(GenerateDrivenAdapterTaskReactiveTest.class);
  private static GenerateDrivenAdapterTask task;

  @BeforeAll
  public static void setup() throws IOException, CleanException {
    deleteStructure(Path.of(TEST_DIR));
    Project project =
        setupProject(GenerateDrivenAdapterTaskReactiveTest.class, GenerateStructureTask.class);

    GenerateStructureTask taskStructure = getTask(project, GenerateStructureTask.class);
    taskStructure.setType(GenerateStructureTask.ProjectType.REACTIVE);
    taskStructure.setLanguage(GenerateStructureTask.Language.JAVA);
    taskStructure.execute();

    ProjectBuilder.builder()
        .withName("app-service")
        .withProjectDir(new File(TEST_DIR + "/applications/app-service"))
        .withParent(project)
        .build();

    task = createTask(project, GenerateDrivenAdapterTask.class);
  }

  @AfterAll
  public static void tearDown() {
    deleteStructure(Path.of(TEST_DIR));
  }

  @Test
  void generateRsocketRequester() throws IOException, CleanException {
    // Arrange
    task.setType("RSOCKET");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/driven-adapters/rsocket-requester/",
        "build.gradle",
        "src/main/java/co/com/bancolombia/rsocket/config/RequesterConfig.java",
        "src/main/java/co/com/bancolombia/rsocket/service/RsocketAdapter.java",
        "src/test/java/co/com/bancolombia/rsocket/service");
  }

  @Test
  void generateDrivenAdapterMongoRepositoryForReactive() throws IOException, CleanException {
    // Arrange
    task.setType("MONGODB");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/driven-adapters/mongo-repository/",
        "build.gradle",
        "src/main/java/co/com/bancolombia/mongo/MongoDBRepository.java",
        "src/main/java/co/com/bancolombia/mongo/MongoRepositoryAdapter.java",
        "src/main/java/co/com/bancolombia/mongo/helper/AdapterOperations.java",
        "src/main/java/co/com/bancolombia/mongo/config/MongoDBSecret.java",
        "src/main/java/co/com/bancolombia/mongo/config/MongoConfig.java");
  }

  @Test
  void generateReactiveRestConsumer() throws IOException, CleanException {
    // Arrange
    task.setUrl("http://localhost:8080");
    task.setType("RESTCONSUMER");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/driven-adapters/rest-consumer/",
        "build.gradle",
        "src/main/java/co/com/bancolombia/consumer/RestConsumer.java",
        "src/main/java/co/com/bancolombia/consumer/ObjectResponse.java",
        "src/main/java/co/com/bancolombia/consumer/ObjectRequest.java",
        "src/main/java/co/com/bancolombia/consumer/config/RestConsumerConfig.java");
  }

  @Test
  void generateReactiveRestConsumerFromSwagger() throws IOException, CleanException {
    // Arrange
    task.setUrl("http://localhost:8080");
    task.setType("RESTCONSUMER");
    task.setFromSwagger(GenerateEntryPointTaskImperativeTest.SWAGGER_FILE);
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/driven-adapters/rest-consumer/",
        "src/main/java/co/com/bancolombia/consumer/api/PetApi.java",
        "src/main/java/co/com/bancolombia/consumer/api/model/Pet.java");
  }

  @Test
  void generateDrivenAdapterEventBus() throws IOException, CleanException {
    // Arrange
    task.setType("ASYNCEVENTBUS");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/driven-adapters/async-event-bus/",
        "build.gradle",
        "src/main/java/co/com/bancolombia/events/ReactiveEventsGateway.java",
        "src/main/java/co/com/bancolombia/events/ReactiveDirectAsyncGateway.java");
    assertFilesExistsInDir(
        TEST_DIR + "/domain/model/",
        "src/main/java/co/com/bancolombia/model/events/gateways/EventsGateway.java");
  }

  @Test
  void shouldHandleErrorBecauseIncompatibility() {
    // Arrange
    task.setType("REDIS");
    task.setMode(DrivenAdapterRedis.Mode.REPOSITORY);
    task.setSecret(AbstractCleanArchitectureDefaultTask.BooleanOption.TRUE);
    // Act
    assertThrows(ValidationException.class, () -> task.execute());
  }

  @Test
  void generateDrivenAdapterR2dbcReactive() throws IOException, CleanException {
    // Arrange
    task.setType("R2DBC");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/driven-adapters/r2dbc-postgresql/",
        "build.gradle",
        "src/main/java/co/com/bancolombia/r2dbc/config/PostgreSQLConnectionPool.java",
        "src/main/java/co/com/bancolombia/r2dbc/config/PostgresqlConnectionProperties.java",
        "src/main/java/co/com/bancolombia/r2dbc/MyReactiveRepository.java",
        "src/main/java/co/com/bancolombia/r2dbc/MyReactiveRepositoryAdapter.java");
  }

  @Test
  void generateDrivenAdapterRedisTemplateForReactiveWithSecret()
      throws IOException, CleanException {
    // Arrange
    task.setType("REDIS");
    task.setMode(DrivenAdapterRedis.Mode.TEMPLATE);
    task.setSecret(AbstractCleanArchitectureDefaultTask.BooleanOption.TRUE);
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/driven-adapters/redis/",
        "build.gradle",
        "src/main/java/co/com/bancolombia/redis/template/helper/ReactiveTemplateAdapterOperations.java",
        "src/main/java/co/com/bancolombia/redis/template/ReactiveRedisTemplateAdapter.java",
        "src/main/java/co/com/bancolombia/redis/config/RedisConfig.java");
  }

  @Test
  void generateDrivenAdapterRedisRepositoryForReactiveWithSecret()
      throws IOException, CleanException {
    // Arrange
    task.setType("REDIS");
    task.setMode(DrivenAdapterRedis.Mode.TEMPLATE);
    task.setSecret(AbstractCleanArchitectureDefaultTask.BooleanOption.TRUE);

    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/driven-adapters/redis/",
        "build.gradle",
        "src/main/java/co/com/bancolombia/redis/template/helper/ReactiveTemplateAdapterOperations.java",
        "src/main/java/co/com/bancolombia/redis/template/ReactiveRedisTemplateAdapter.java",
        "src/main/java/co/com/bancolombia/redis/config/RedisConfig.java");
  }

  @Test
  void generateDrivenAdapterKMSForReactive() throws IOException, CleanException {
    // Arrange
    task.setType("KMS");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/driven-adapters/kms-repository/",
        "build.gradle",
        "src/main/java/co/com/bancolombia/kms/KmsAdapter.java");
  }

  @Test
  void generateDrivenAdapterS3ForReactive() throws IOException, CleanException {
    // Arrange
    task.setType("S3");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/driven-adapters/s3-repository/",
        "build.gradle",
        "src/main/java/co/com/bancolombia/s3/adapter/S3Adapter.java",
        "src/main/java/co/com/bancolombia/s3/operations/S3Operations.java",
        "src/main/java/co/com/bancolombia/s3/config/S3Config.java",
        "src/main/java/co/com/bancolombia/s3/config/model/S3ConnectionProperties.java");
  }

  @Test
  void generateMQSender() throws IOException, CleanException {
    // Arrange
    task.setType("MQ");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/driven-adapters/mq-sender/",
        "build.gradle",
        "src/main/java/co/com/bancolombia/mq/sender/SampleMQMessageSender.java");
  }

  @Test
  void generateSQSSender() throws IOException, CleanException {
    // Arrange
    task.setType("SQS");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/driven-adapters/sqs-sender/",
        "build.gradle",
        "src/main/java/co/com/bancolombia/sqs/sender/SQSSender.java");
  }

  @Test
  void generateDrivenAdapterSecrets() throws IOException, CleanException {
    // Arrange
    task.setType("SECRETS");
    task.setSecretsBackend(DrivenAdapterSecrets.SecretsBackend.VAULT);
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/applications/app-service/",
        "src/main/java/co/com/bancolombia/config/SecretsConfig.java");
    assertFileContains(
        TEST_DIR + "/applications/app-service/build.gradle",
        "implementation 'com.github.bancolombia:vault-async:");
  }

  @Test
  void generateDrivenAdapterSecretsWithDefault() throws IOException, CleanException {
    // Arrange
    task.setType("SECRETS");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/applications/app-service/",
        "src/main/java/co/com/bancolombia/config/SecretsConfig.java");

    assertFileContains(
        TEST_DIR + "/applications/app-service/build.gradle",
        "implementation 'com.github.bancolombia:aws-secrets-manager-async:");
  }
}
