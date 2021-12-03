package co.com.bancolombia.task;

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
import java.util.List;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;

public class GenerateDrivenAdapterTaskTest {
  private GenerateDrivenAdapterTask task;

  @Before
  public void init() throws IOException, CleanException {
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
        new File("build/unitTest/infrastructure/driven-adapters/my-driven-adapter/build.gradle")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/my-driven-adapter/src/main/java/co/com/bancolombia/mydrivenadapter")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/my-driven-adapter/src/test/java/co/com/bancolombia/mydrivenadapter")
            .exists());
  }

  @Test
  public void generateRestConsumer() throws IOException, CleanException {
    // Arrange
    task.setType(ModuleFactoryDrivenAdapter.DrivenAdapterType.RESTCONSUMER);
    task.setUrl("http://localhost:8080");
    // Act
    task.generateDrivenAdapterTask();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/driven-adapters/rest-consumer/build.gradle")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/rest-consumer/src/main/java/co/com/bancolombia/consumer/RestConsumer.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/rest-consumer/src/main/java/co/com/bancolombia/consumer/ObjectResponse.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/rest-consumer/src/main/java/co/com/bancolombia/consumer/ObjectRequest.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/rest-consumer/src/main/java/co/com/bancolombia/consumer/config/RestConsumerConfig.java")
            .exists());
  }

  @Test
  public void generateRsocketRequester() throws IOException, CleanException {
    // Arrange
    setup(GenerateStructureTask.ProjectType.REACTIVE);
    task.setType(ModuleFactoryDrivenAdapter.DrivenAdapterType.RSOCKET);
    // Act
    task.generateDrivenAdapterTask();
    // Assert
    assertTrue(
        new File("build/unitTest/infrastructure/driven-adapters/rsocket-requester/build.gradle")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/rsocket-requester/src/main/java/co/com/bancolombia/rsocket/config/RequesterConfig.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/rsocket-requester/src/main/java/co/com/bancolombia/rsocket/service/RsocketAdapter.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/rsocket-requester/src/test/java/co/com/bancolombia/rsocket/service")
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
        new File("build/unitTest/infrastructure/driven-adapters/jpa-repository/build.gradle")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/jpa-repository/src/main/java/co/com/bancolombia/jpa/JPARepository.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/jpa-repository/src/main/java/co/com/bancolombia/jpa/JPARepositoryAdapter.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/jpa-repository/src/main/java/co/com/bancolombia/jpa/helper/AdapterOperations.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/jpa-repository/src/main/java/co/com/bancolombia/jpa/config/DBSecret.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/applications/app-service/src/main/java/co/com/bancolombia/config/JpaConfig.java")
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
        new File("build/unitTest/infrastructure/driven-adapters/mongo-repository/build.gradle")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/java/co/com/bancolombia/mongo/MongoDBRepository.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/java/co/com/bancolombia/mongo/MongoRepositoryAdapter.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/java/co/com/bancolombia/mongo/helper/AdapterOperations.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/java/co/com/bancolombia/mongo/config/MongoDBSecret.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/java/co/com/bancolombia/mongo/config/MongoConfig.java")
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
        new File("build/unitTest/infrastructure/driven-adapters/mongo-repository/build.gradle")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/java/co/com/bancolombia/mongo/MongoDBRepository.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/java/co/com/bancolombia/mongo/MongoRepositoryAdapter.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/java/co/com/bancolombia/mongo/helper/AdapterOperations.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/java/co/com/bancolombia/mongo/config/MongoDBSecret.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/java/co/com/bancolombia/mongo/config/MongoConfig.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/applications/app-service/src/main/java/co/com/bancolombia/config/SecretsConfig.java")
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
        new File("build/unitTest/infrastructure/driven-adapters/mongo-repository/build.gradle")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/java/co/com/bancolombia/mongo/MongoDBRepository.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/java/co/com/bancolombia/mongo/MongoRepositoryAdapter.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/java/co/com/bancolombia/mongo/helper/AdapterOperations.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/java/co/com/bancolombia/mongo/config/MongoDBSecret.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/java/co/com/bancolombia/mongo/config/MongoConfig.java")
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
        new File("build/unitTest/infrastructure/driven-adapters/mongo-repository/build.gradle")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/java/co/com/bancolombia/mongo/MongoDBRepository.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/java/co/com/bancolombia/mongo/MongoRepositoryAdapter.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/java/co/com/bancolombia/mongo/helper/AdapterOperations.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/java/co/com/bancolombia/mongo/config/MongoDBSecret.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mongo-repository/src/main/java/co/com/bancolombia/mongo/config/MongoConfig.java")
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
        new File("build/unitTest/infrastructure/driven-adapters/rest-consumer/build.gradle")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/rest-consumer/src/main/java/co/com/bancolombia/consumer/RestConsumer.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/rest-consumer/src/main/java/co/com/bancolombia/consumer/ObjectResponse.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/rest-consumer/src/main/java/co/com/bancolombia/consumer/ObjectRequest.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/rest-consumer/src/main/java/co/com/bancolombia/consumer/config/RestConsumerConfig.java")
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
        new File("build/unitTest/infrastructure/driven-adapters/async-event-bus/build.gradle")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/async-event-bus/src/main/java/co/com/bancolombia/events/ReactiveEventsGateway.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/async-event-bus/src/main/java/co/com/bancolombia/events/ReactiveDirectAsyncGateway.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/domain/model/src/main/java/co/com/bancolombia/model/events/gateways/EventsGateway.java")
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
        new File("build/unitTest/infrastructure/driven-adapters/redis/build.gradle").exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/redis/src/main/java/co/com/bancolombia/redis/repository/helper/RepositoryAdapterOperations.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/redis/src/main/java/co/com/bancolombia/redis/repository/RedisRepository.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/redis/src/main/java/co/com/bancolombia/redis/repository/RedisRepositoryAdapter.java")
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
        new File("build/unitTest/infrastructure/driven-adapters/redis/build.gradle").exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/redis/src/main/java/co/com/bancolombia/redis/repository/helper/ReactiveRepositoryAdapterOperations.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/redis/src/main/java/co/com/bancolombia/redis/repository/ReactiveRedisRepository.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/redis/src/main/java/co/com/bancolombia/redis/repository/ReactiveRedisRepositoryAdapter.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/redis/src/main/java/co/com/bancolombia/redis/config/RedisConfig.java")
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
        new File("build/unitTest/infrastructure/driven-adapters/redis/build.gradle").exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/redis/src/main/java/co/com/bancolombia/redis/template/helper/TemplateAdapterOperations.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/redis/src/main/java/co/com/bancolombia/redis/template/RedisTemplateAdapter.java")
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
        new File("build/unitTest/infrastructure/driven-adapters/r2dbc-postgresql/build.gradle")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/r2dbc-postgresql/src/main/java/co/com/bancolombia/config/PostgreSQLConnectionPool.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/r2dbc-postgresql/src/main/java/co/com/bancolombia/config/PostgresqlConnectionProperties.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/r2dbc-postgresql/src/main/java/co/com/bancolombia/MyReactiveRepository.java")
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
        new File("build/unitTest/infrastructure/driven-adapters/redis/build.gradle").exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/redis/src/main/java/co/com/bancolombia/redis/template/helper/ReactiveTemplateAdapterOperations.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/redis/src/main/java/co/com/bancolombia/redis/template/ReactiveRedisTemplateAdapter.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/redis/src/main/java/co/com/bancolombia/redis/config/RedisConfig.java")
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
        new File("build/unitTest/infrastructure/driven-adapters/kms-repository/build.gradle")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/kms-repository/src/main/java/co/com/bancolombia/kms/KmsAdapter.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/redis/src/main/java/co/com/bancolombia/redis/template/ReactiveRedisTemplateAdapter.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/redis/src/main/java/co/com/bancolombia/redis/config/RedisConfig.java")
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
        new File("build/unitTest/infrastructure/driven-adapters/kms-repository/build.gradle")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/kms-repository/src/main/java/co/com/bancolombia/kms/KmsAdapter.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/kms-repository/src/main/java/co/com/bancolombia/kms/config/KmsConfig.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/kms-repository/src/main/java/co/com/bancolombia/kms/config/model/KmsConnectionProperties.java")
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
        new File("build/unitTest/infrastructure/driven-adapters/s3-repository/build.gradle")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/s3-repository/src/main/java/co/com/bancolombia/s3/adapter/S3Adapter.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/s3-repository/src/main/java/co/com/bancolombia/s3/operations/S3Operations.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/s3-repository/src/main/java/co/com/bancolombia/s3/config/S3Config.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/s3-repository/src/main/java/co/com/bancolombia/s3/config/model/S3ConnectionProperties.java")
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
        new File("build/unitTest/infrastructure/driven-adapters/s3-repository/build.gradle")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/s3-repository/src/main/java/co/com/bancolombia/s3/adapter/S3Adapter.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/s3-repository/src/main/java/co/com/bancolombia/s3/operations/S3Operations.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/s3-repository/src/main/java/co/com/bancolombia/s3/config/S3Config.java")
            .exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/s3-repository/src/main/java/co/com/bancolombia/s3/config/model/S3ConnectionProperties.java")
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
        new File("build/unitTest/infrastructure/driven-adapters/mq-sender/build.gradle").exists());
    assertTrue(
        new File(
                "build/unitTest/infrastructure/driven-adapters/mq-sender/src/main/java/co/com/bancolombia/mq/sender/SampleMQMessageSender.java")
            .exists());
  }

  private void writeString(File file, String string) throws IOException {
    try (Writer writer = new FileWriter(file)) {
      writer.write(string);
    }
  }
}
