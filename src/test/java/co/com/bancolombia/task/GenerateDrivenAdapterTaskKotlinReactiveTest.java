package co.com.bancolombia.task;

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
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class GenerateDrivenAdapterTaskKotlinReactiveTest {
  private static final String TEST_DIR =
      getTestDir(GenerateDrivenAdapterTaskKotlinReactiveTest.class);
  private static GenerateDrivenAdapterTask task;

  @BeforeAll
  public static void setup() throws IOException, CleanException {
    deleteStructure(Path.of(TEST_DIR));
    Project project =
        setupProject(
            GenerateDrivenAdapterTaskKotlinReactiveTest.class, GenerateStructureTask.class);

    GenerateStructureTask taskStructure = getTask(project, GenerateStructureTask.class);
    taskStructure.setType(GenerateStructureTask.ProjectType.REACTIVE);
    taskStructure.setLanguage(GenerateStructureTask.Language.KOTLIN);
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
  public void generateDrivenAdapterMongoRepositoryForReactive() throws IOException, CleanException {
    // Arrange
    task.setType("MONGODB");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/driven-adapters/mongo-repository/",
        "build.gradle.kts",
        "src/main/kotlin/co/com/bancolombia/mongo/MongoDBRepository.kt",
        "src/main/kotlin/co/com/bancolombia/mongo/MongoRepositoryAdapter.kt",
        "src/main/kotlin/co/com/bancolombia/mongo/helper/AdapterOperations.kt",
        "src/main/kotlin/co/com/bancolombia/mongo/config/MongoDBSecret.kt",
        "src/main/kotlin/co/com/bancolombia/mongo/config/MongoConfig.kt");
  }

  @Test
  public void generateReactiveRestConsumer() throws IOException, CleanException {
    // Arrange
    task.setUrl("http://localhost:8080");
    task.setType("RESTCONSUMER");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/driven-adapters/rest-consumer/",
        "build.gradle.kts",
        "src/main/kotlin/co/com/bancolombia/consumer/RestConsumer.kt",
        "src/main/kotlin/co/com/bancolombia/consumer/ObjectResponse.kt",
        "src/main/kotlin/co/com/bancolombia/consumer/ObjectRequest.kt",
        "src/main/kotlin/co/com/bancolombia/consumer/config/RestConsumerConfig.kt");
  }

  @Test
  public void generateDrivenAdapterEventBus() throws IOException, CleanException {
    // Arrange
    task.setType("ASYNCEVENTBUS");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/driven-adapters/async-event-bus/",
        "build.gradle.kts",
        "src/main/kotlin/co/com/bancolombia/events/ReactiveEventsGateway.kt",
        "src/main/kotlin/co/com/bancolombia/events/ReactiveDirectAsyncGateway.kt");
    assertFilesExistsInDir(
        TEST_DIR + "/domain/model/",
        "src/main/kotlin/co/com/bancolombia/model/events/gateways/EventsGateway.kt");
  }

  @Test
  public void shouldHandleErrorBecauseIncompatibility() {
    // Arrange
    task.setType("REDIS");
    task.setMode(DrivenAdapterRedis.Mode.REPOSITORY);
    task.setSecret(AbstractCleanArchitectureDefaultTask.BooleanOption.TRUE);
    // Act
    assertThrows(ValidationException.class, () -> task.execute());
  }

  @Test
  public void generateDrivenAdapterR2dbcReactive() throws IOException, CleanException {
    // Arrange
    task.setType("R2DBC");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/driven-adapters/r2dbc-postgresql/",
        "build.gradle.kts",
        "src/main/kotlin/co/com/bancolombia/config/PostgreSQLConnectionPool.kt",
        "src/main/kotlin/co/com/bancolombia/config/PostgresqlConnectionProperties.kt",
        "src/main/kotlin/co/com/bancolombia/MyReactiveRepository.kt");
  }

  @Test
  public void generateDrivenAdapterRedisTemplateForReactiveWithSecret()
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
        "build.gradle.kts",
        "src/main/kotlin/co/com/bancolombia/redis/template/helper/ReactiveTemplateAdapterOperations.kt",
        "src/main/kotlin/co/com/bancolombia/redis/template/ReactiveRedisTemplateAdapter.kt",
        "src/main/kotlin/co/com/bancolombia/redis/config/RedisConfig.kt");
  }

  @Test
  public void generateDrivenAdapterKMSForReactive() throws IOException, CleanException {
    // Arrange
    task.setType("KMS");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/driven-adapters/kms-repository/",
        "build.gradle.kts",
        "src/main/kotlin/co/com/bancolombia/kms/KmsAdapter.kt");
  }

  @Test
  public void generateDrivenAdapterS3ForReactive() throws IOException, CleanException {
    // Arrange
    task.setType("S3");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/driven-adapters/s3-repository/",
        "build.gradle.kts",
        "src/main/kotlin/co/com/bancolombia/s3/adapter/S3Adapter.kt",
        "src/main/kotlin/co/com/bancolombia/s3/operations/S3Operations.kt",
        "src/main/kotlin/co/com/bancolombia/s3/config/S3Config.kt",
        "src/main/kotlin/co/com/bancolombia/s3/config/model/S3ConnectionProperties.kt");
  }

  @Test
  public void generateMQSender() throws IOException, CleanException {
    // Arrange
    task.setType("MQ");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/driven-adapters/mq-sender/",
        "build.gradle.kts",
        "src/main/kotlin/co/com/bancolombia/mq/sender/SampleMQMessageSender.kt");
  }

  @Test
  public void generateDrivenAdapterSQSSenderForReactive() throws IOException, CleanException {
    // Arrange
    task.setType("SQS");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/driven-adapters/sqs-sender", "build.gradle.kts");
  }
}
