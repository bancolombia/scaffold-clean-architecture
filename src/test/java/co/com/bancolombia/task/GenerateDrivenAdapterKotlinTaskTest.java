package co.com.bancolombia.task;

import static co.com.bancolombia.utils.FileUtilsTest.deleteStructure;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import co.com.bancolombia.Constants;
import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.adapters.DrivenAdapterRedis;
import co.com.bancolombia.factory.adapters.ModuleFactoryDrivenAdapter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.List;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;

public class GenerateDrivenAdapterKotlinTaskTest {
  private GenerateDrivenAdapterTask task;

  @Before
  public void init() throws IOException, CleanException {
    deleteStructure(Path.of("build/unitTest"));
    setup(GenerateStructureTask.ProjectType.IMPERATIVE);
  }

  private void setup(GenerateStructureTask.ProjectType type) throws IOException, CleanException {
    Project project = ProjectBuilder.builder().withProjectDir(new File("build/unitTest")).build();

    ProjectBuilder.builder()
        .withName("app-service")
        .withProjectDir(new File("build/unitTest/applications/app-service"))
        .withParent(project)
        .build();

    project.getTasks().create("ca", GenerateStructureTask.class);
    GenerateStructureTask taskStructure =
        (GenerateStructureTask) project.getTasks().getByName("ca");
    taskStructure.setType(type);
    taskStructure.setLanguage(GenerateStructureTask.Language.KOTLIN);
    taskStructure.generateStructureTask();

    project.getTasks().create("test", GenerateDrivenAdapterTask.class);
    task = (GenerateDrivenAdapterTask) project.getTasks().getByName("test");
  }

  // Assert
  @Test(expected = IllegalArgumentException.class)
  public void shouldHandleErrorWhenNoType() throws IOException, CleanException {
    // Arrange
    task.setType(null);
    // Act
    task.generateDrivenAdapterTask();
  }

  // Assert
  @Test(expected = IllegalArgumentException.class)
  public void shouldHandleErrorWhenNoName() throws IOException, CleanException {
    // Arrange
    task.setType(ModuleFactoryDrivenAdapter.DrivenAdapterType.GENERIC);
    // Act
    task.generateDrivenAdapterTask();
  }

  // Assert
  @Test(expected = IllegalArgumentException.class)
  public void shouldHandleErrorWhenEmptyName() throws IOException, CleanException {
    // Arrange
    task.setType(ModuleFactoryDrivenAdapter.DrivenAdapterType.GENERIC);
    task.setName("");
    // Act
    task.generateDrivenAdapterTask();
  }

  @Test
  public void generateDrivenAdapterGeneric() throws IOException, CleanException {
    // Arrange
    task.setType(ModuleFactoryDrivenAdapter.DrivenAdapterType.GENERIC);
    task.setName("MyDrivenAdapter");
    // Act
    task.generateDrivenAdapterTask();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/driven-adapters/my-driven-adapter/build.gradle.kts")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/my-driven-adapter/src/main/kotlin/co/com/bancolombia/mydrivenadapter")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/my-driven-adapter/src/test/kotlin/co/com/bancolombia/mydrivenadapter")
            .exists());
  }

  @Test
  public void generateRestConsumerForKotlin() throws IOException, CleanException {
    // Arrange
    task.setType(ModuleFactoryDrivenAdapter.DrivenAdapterType.RESTCONSUMER);
    task.setUrl("http://localhost:8080");
    // Act
    task.generateDrivenAdapterTask();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/driven-adapters/rest-consumer/build.gradle.kts")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/rest-consumer/src/main/kotlin/co/com/bancolombia/consumer/RestConsumer.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/rest-consumer/src/main/kotlin/co/com/bancolombia/consumer/ObjectResponse.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/rest-consumer/src/main/kotlin/co/com/bancolombia/consumer/ObjectRequest.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/rest-consumer/src/main/kotlin/co/com/bancolombia/consumer/config/RestConsumerConfig.kt")
            .exists());
  }

  @Test
  public void generateDrivenAdapterJPARepository() throws IOException, CleanException {
    // Arrange
    task.setType(ModuleFactoryDrivenAdapter.DrivenAdapterType.JPA);
    // Act
    task.generateDrivenAdapterTask();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/driven-adapters/jpa-repository/build.gradle.kts")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/jpa-repository/src/main/kotlin/co/com/bancolombia/jpa/JPARepository.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/jpa-repository/src/main/kotlin/co/com/bancolombia/jpa/JPARepositoryAdapter.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/jpa-repository/src/main/kotlin/co/com/bancolombia/jpa/helper/AdapterOperations.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/jpa-repository/src/main/kotlin/co/com/bancolombia/jpa/config/DBSecret.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/jpa-repository/src/main/kotlin/co/com/bancolombia/jpa/config/JpaConfig.kt")
            .exists());
  }

  @Test
  public void generateDrivenAdapterMongoRepository() throws IOException, CleanException {
    // Arrange
    task.setType(ModuleFactoryDrivenAdapter.DrivenAdapterType.MONGODB);
    // Act
    task.generateDrivenAdapterTask();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/driven-adapters/mongo-repository/build.gradle.kts")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/kotlin/co/com/bancolombia/mongo/MongoDBRepository.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/kotlin/co/com/bancolombia/mongo/MongoRepositoryAdapter.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/kotlin/co/com/bancolombia/mongo/helper/AdapterOperations.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/kotlin/co/com/bancolombia/mongo/config/MongoDBSecret.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/kotlin/co/com/bancolombia/mongo/config/MongoConfig.kt")
            .exists());
  }

  @Test
  public void generateDrivenAdapterMongoRepositoryWithSecrets() throws IOException, CleanException {
    // Arrange
    task.setType(ModuleFactoryDrivenAdapter.DrivenAdapterType.MONGODB);
    task.setSecret(Constants.BooleanOption.TRUE);
    // Act
    task.generateDrivenAdapterTask();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/driven-adapters/mongo-repository/build.gradle.kts")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/kotlin/co/com/bancolombia/mongo/MongoDBRepository.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/kotlin/co/com/bancolombia/mongo/MongoRepositoryAdapter.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/kotlin/co/com/bancolombia/mongo/helper/AdapterOperations.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/kotlin/co/com/bancolombia/mongo/config/MongoDBSecret.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/kotlin/co/com/bancolombia/mongo/config/MongoConfig.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/applications/app-service/src/main/kotlin/co/com/bancolombia/config/SecretsConfig.kt")
            .exists());
  }

  @Test
  public void generateDrivenAdapterMongoRepositoryForNoProjectType()
      throws IOException, CleanException {
    // Arrange
    writeString(
        new File("build/unitTest/gradle.properties"),
        "package=co.com.bancolombia\nsystemProp.version=" + Constants.PLUGIN_VERSION + "\n");
    task.setType(ModuleFactoryDrivenAdapter.DrivenAdapterType.MONGODB);
    // Act
    task.generateDrivenAdapterTask();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/driven-adapters/mongo-repository/build.gradle.kts")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/kotlin/co/com/bancolombia/mongo/MongoDBRepository.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/kotlin/co/com/bancolombia/mongo/MongoRepositoryAdapter.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/kotlin/co/com/bancolombia/mongo/helper/AdapterOperations.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/kotlin/co/com/bancolombia/mongo/config/MongoDBSecret.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/kotlin/co/com/bancolombia/mongo/config/MongoConfig.kt")
            .exists());
  }

  @Test
  public void generateDrivenAdapterMongoRepositoryForReactive() throws IOException, CleanException {
    // Arrange
    setup(GenerateStructureTask.ProjectType.REACTIVE);
    task.setType(ModuleFactoryDrivenAdapter.DrivenAdapterType.MONGODB);
    // Act
    task.generateDrivenAdapterTask();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/driven-adapters/mongo-repository/build.gradle.kts")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/kotlin/co/com/bancolombia/mongo/MongoDBRepository.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/kotlin/co/com/bancolombia/mongo/MongoRepositoryAdapter.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/kotlin/co/com/bancolombia/mongo/helper/AdapterOperations.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/kotlin/co/com/bancolombia/mongo/config/MongoDBSecret.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/kotlin/co/com/bancolombia/mongo/config/MongoConfig.kt")
            .exists());
  }

  @Test
  public void generateReactiveRestConsumer() throws IOException, CleanException {
    // Arrange
    setup(GenerateStructureTask.ProjectType.REACTIVE);
    task.setUrl("http://localhost:8080");
    task.setType(ModuleFactoryDrivenAdapter.DrivenAdapterType.RESTCONSUMER);
    // Act
    task.generateDrivenAdapterTask();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/driven-adapters/rest-consumer/build.gradle.kts")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/rest-consumer/src/main/kotlin/co/com/bancolombia/consumer/RestConsumer.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/rest-consumer/src/main/kotlin/co/com/bancolombia/consumer/ObjectResponse.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/rest-consumer/src/main/kotlin/co/com/bancolombia/consumer/ObjectRequest.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/rest-consumer/src/main/kotlin/co/com/bancolombia/consumer/config/RestConsumerConfig.kt")
            .exists());
  }

  @Test
  public void generateDrivenAdapterEventBus() throws IOException, CleanException {
    // Arrange
    setup(GenerateStructureTask.ProjectType.REACTIVE);
    task.setType(ModuleFactoryDrivenAdapter.DrivenAdapterType.ASYNCEVENTBUS);
    // Act
    task.generateDrivenAdapterTask();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/driven-adapters/async-event-bus/build.gradle.kts")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/async-event-bus/src/main/kotlin/co/com/bancolombia/events/ReactiveEventsGateway.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/async-event-bus/src/main/kotlin/co/com/bancolombia/events/ReactiveDirectAsyncGateway.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/domain/model/src/main/kotlin/co/com/bancolombia/model/events/gateways/EventsGateway.kt")
            .exists());
  }

  @Test
  public void shouldGetSecretOptions() {
    // Arrange
    // Act
    List<Constants.BooleanOption> options = task.getSecretOptions();
    // Assert
    assertEquals(2, options.size());
  }

  @Test
  public void generateDrivenAdapterRedisRepositoryForImperative()
      throws IOException, CleanException {
    // Arrange
    setup(GenerateStructureTask.ProjectType.IMPERATIVE);
    task.setType(ModuleFactoryDrivenAdapter.DrivenAdapterType.REDIS);
    task.setMode(DrivenAdapterRedis.Mode.REPOSITORY);
    // Act
    task.generateDrivenAdapterTask();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/driven-adapters/redis/build.gradle.kts").exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/redis/src/main/kotlin/co/com/bancolombia/redis/repository/helper/RepositoryAdapterOperations.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/redis/src/main/kotlin/co/com/bancolombia/redis/repository/RedisRepository.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/redis/src/main/kotlin/co/com/bancolombia/redis/repository/RedisRepositoryAdapter.kt")
            .exists());
  }

  @Test
  public void generateDrivenAdapterRedisRepositoryForReactiveWithSecret()
      throws IOException, CleanException {
    // Arrange
    setup(GenerateStructureTask.ProjectType.REACTIVE);
    task.setType(ModuleFactoryDrivenAdapter.DrivenAdapterType.REDIS);
    task.setMode(DrivenAdapterRedis.Mode.REPOSITORY);
    task.setSecret(Constants.BooleanOption.TRUE);
    // Act
    task.generateDrivenAdapterTask();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/driven-adapters/redis/build.gradle.kts").exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/redis/src/main/kotlin/co/com/bancolombia/redis/repository/helper/ReactiveRepositoryAdapterOperations.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/redis/src/main/kotlin/co/com/bancolombia/redis/repository/ReactiveRedisRepository.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/redis/src/main/kotlin/co/com/bancolombia/redis/repository/ReactiveRedisRepositoryAdapter.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/redis/src/main/kotlin/co/com/bancolombia/redis/config/RedisConfig.kt")
            .exists());
  }

  @Test
  public void generateDrivenAdapterRedisTemplateForImperative() throws IOException, CleanException {
    // Arrange
    setup(GenerateStructureTask.ProjectType.IMPERATIVE);
    task.setType(ModuleFactoryDrivenAdapter.DrivenAdapterType.REDIS);
    // Act
    task.generateDrivenAdapterTask();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/driven-adapters/redis/build.gradle.kts").exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/redis/src/main/kotlin/co/com/bancolombia/redis/template/helper/TemplateAdapterOperations.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/redis/src/main/kotlin/co/com/bancolombia/redis/template/RedisTemplateAdapter.kt")
            .exists());
  }

  @Test
  public void generateDrivenAdapterR2dbcReactive() throws IOException, CleanException {
    // Arrange
    setup(GenerateStructureTask.ProjectType.REACTIVE);
    task.setType(ModuleFactoryDrivenAdapter.DrivenAdapterType.R2DBC);
    // Act
    task.generateDrivenAdapterTask();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/driven-adapters/r2dbc-postgresql/build.gradle.kts")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/r2dbc-postgresql/src/main/kotlin/co/com/bancolombia/config/PostgreSQLConnectionPool.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/r2dbc-postgresql/src/main/kotlin/co/com/bancolombia/config/PostgresqlConnectionProperties.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/r2dbc-postgresql/src/main/kotlin/co/com/bancolombia/MyReactiveRepository.kt")
            .exists());
  }

  @Test
  public void generateDrivenAdapterRedisTemplateForReactiveWithSecret()
      throws IOException, CleanException {
    // Arrange
    setup(GenerateStructureTask.ProjectType.REACTIVE);
    task.setType(ModuleFactoryDrivenAdapter.DrivenAdapterType.REDIS);
    task.setSecret(Constants.BooleanOption.TRUE);
    // Act
    task.generateDrivenAdapterTask();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/driven-adapters/redis/build.gradle.kts").exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/redis/src/main/kotlin/co/com/bancolombia/redis/template/helper/ReactiveTemplateAdapterOperations.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/redis/src/main/kotlin/co/com/bancolombia/redis/template/ReactiveRedisTemplateAdapter.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/redis/src/main/kotlin/co/com/bancolombia/redis/config/RedisConfig.kt")
            .exists());
  }

  @Test
  public void generateDrivenAdapterKMSForReactive() throws IOException, CleanException {
    // Arrange
    setup(GenerateStructureTask.ProjectType.REACTIVE);
    task.setType(ModuleFactoryDrivenAdapter.DrivenAdapterType.KMS);
    // Act
    task.generateDrivenAdapterTask();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/driven-adapters/kms-repository/build.gradle.kts")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/kms-repository/src/main/kotlin/co/com/bancolombia/kms/KmsAdapter.kt")
            .exists());
  }

  @Test
  public void generateDrivenAdapterKMSForImperative() throws IOException, CleanException {
    // Arrange
    setup(GenerateStructureTask.ProjectType.IMPERATIVE);
    task.setType(ModuleFactoryDrivenAdapter.DrivenAdapterType.KMS);
    // Act
    task.generateDrivenAdapterTask();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/driven-adapters/kms-repository/build.gradle.kts")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/kms-repository/src/main/kotlin/co/com/bancolombia/kms/KmsAdapter.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/kms-repository/src/main/kotlin/co/com/bancolombia/kms/config/KmsConfig.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/kms-repository/src/main/kotlin/co/com/bancolombia/kms/config/model/KmsConnectionProperties.kt")
            .exists());
  }

  @Test
  public void generateDrivenAdapterS3ForReactive() throws IOException, CleanException {
    // Arrange
    setup(GenerateStructureTask.ProjectType.REACTIVE);
    task.setType(ModuleFactoryDrivenAdapter.DrivenAdapterType.S3);
    // Act
    task.generateDrivenAdapterTask();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/driven-adapters/s3-repository/build.gradle.kts")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/s3-repository/src/main/kotlin/co/com/bancolombia/s3/adapter/S3Adapter.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/s3-repository/src/main/kotlin/co/com/bancolombia/s3/operations/S3Operations.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/s3-repository/src/main/kotlin/co/com/bancolombia/s3/config/S3Config.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/s3-repository/src/main/kotlin/co/com/bancolombia/s3/config/model/S3ConnectionProperties.kt")
            .exists());
  }

  @Test
  public void generateDrivenAdapterS3ForImperative() throws IOException, CleanException {
    // Arrange
    setup(GenerateStructureTask.ProjectType.IMPERATIVE);
    task.setType(ModuleFactoryDrivenAdapter.DrivenAdapterType.S3);
    // Act
    task.generateDrivenAdapterTask();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/driven-adapters/s3-repository/build.gradle.kts")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/s3-repository/src/main/kotlin/co/com/bancolombia/s3/adapter/S3Adapter.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/s3-repository/src/main/kotlin/co/com/bancolombia/s3/operations/S3Operations.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/s3-repository/src/main/kotlin/co/com/bancolombia/s3/config/S3Config.kt")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/s3-repository/src/main/kotlin/co/com/bancolombia/s3/config/model/S3ConnectionProperties.kt")
            .exists());
  }

  @Test
  public void generateMQSender() throws IOException, CleanException {
    // Arrange
    setup(GenerateStructureTask.ProjectType.REACTIVE);
    task.setType(ModuleFactoryDrivenAdapter.DrivenAdapterType.MQ);
    // Act
    task.generateDrivenAdapterTask();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/driven-adapters/mq-sender/build.gradle.kts")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mq-sender/src/main/kotlin/co/com/bancolombia/mq/sender/SampleMQMessageSender.kt")
            .exists());
  }

  @Test
  public void generateDrivenAdapterKtor() throws IOException, CleanException {
    // Arrange
    task.setType(ModuleFactoryDrivenAdapter.DrivenAdapterType.KTOR);
    // Act
    task.generateDrivenAdapterTask();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/driven-adapters/ktor-client/build.gradle.kts")
            .exists());
  }

  @Test
  public void generateDrivenAdapterDynamoDB() throws IOException, CleanException {
    // Arrange
    task.setType(ModuleFactoryDrivenAdapter.DrivenAdapterType.DYNAMODB);
    // Act
    task.generateDrivenAdapterTask();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/driven-adapters/dynamo-db/build.gradle.kts")
            .exists());
  }

  @Test
  public void generateDrivenAdapterSQSSenderForReactive() throws IOException, CleanException {
    // Arrange
    setup(GenerateStructureTask.ProjectType.REACTIVE);
    task.setType(ModuleFactoryDrivenAdapter.DrivenAdapterType.SQS);
    // Act
    task.generateDrivenAdapterTask();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/driven-adapters/sqs-sender/build.gradle.kts")
            .exists());
  }

  private void writeString(File file, String string) throws IOException {
    try (Writer writer = new FileWriter(file)) {
      writer.write(string);
    }
  }
}
