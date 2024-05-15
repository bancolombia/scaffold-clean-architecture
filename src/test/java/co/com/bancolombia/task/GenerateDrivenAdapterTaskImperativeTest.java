package co.com.bancolombia.task;

import static co.com.bancolombia.TestUtils.assertFilesExistsInDir;
import static co.com.bancolombia.TestUtils.createTask;
import static co.com.bancolombia.TestUtils.deleteStructure;
import static co.com.bancolombia.TestUtils.getTask;
import static co.com.bancolombia.TestUtils.getTestDir;
import static co.com.bancolombia.TestUtils.setupProject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.adapters.DrivenAdapterRedis;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class GenerateDrivenAdapterTaskImperativeTest {
  private static final String TEST_DIR = getTestDir(GenerateDrivenAdapterTaskImperativeTest.class);
  private static GenerateDrivenAdapterTask task;

  @BeforeAll
  public static void setup() throws IOException, CleanException {
    deleteStructure(Path.of(TEST_DIR));
    Project project =
        setupProject(GenerateDrivenAdapterTaskImperativeTest.class, GenerateStructureTask.class);

    GenerateStructureTask taskStructure = getTask(project, GenerateStructureTask.class);
    taskStructure.setType(GenerateStructureTask.ProjectType.IMPERATIVE);
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
  void generateDrivenAdapterGeneric() throws IOException, CleanException {
    // Arrange
    task.setType("GENERIC");
    task.setName("MyDrivenAdapter");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/driven-adapters/my-driven-adapter/",
        "build.gradle",
        "src/main/java/co/com/bancolombia/mydrivenadapter",
        "src/test/java/co/com/bancolombia/mydrivenadapter");
  }

  @Test
  void generateRestConsumer() throws IOException, CleanException {
    // Arrange
    task.setType("RESTCONSUMER");
    task.setUrl("http://localhost:8080");
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
  void generateRestConsumerFromSwagger() throws IOException, CleanException {
    // Arrange
    task.setType("RESTCONSUMER");
    task.setUrl("http://localhost:8080");
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
  void generateDrivenAdapterJPARepository() throws IOException, CleanException {
    // Arrange
    task.setType("JPA");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/driven-adapters/jpa-repository/",
        "build.gradle",
        "src/main/java/co/com/bancolombia/jpa/JPARepository.java",
        "src/main/java/co/com/bancolombia/jpa/JPARepositoryAdapter.java",
        "src/main/java/co/com/bancolombia/jpa/helper/AdapterOperations.java",
        "src/main/java/co/com/bancolombia/jpa/config/DBSecret.java",
        "src/main/java/co/com/bancolombia/jpa/config/JpaConfig.java");
  }

  @Test
  void generateDrivenAdapterJPARepositoryWithSecrets() throws IOException, CleanException {
    // Arrange
    task.setType("JPA");
    task.setSecret(AbstractCleanArchitectureDefaultTask.BooleanOption.TRUE);
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/driven-adapters/jpa-repository/",
        "build.gradle",
        "src/main/java/co/com/bancolombia/jpa/JPARepository.java",
        "src/main/java/co/com/bancolombia/jpa/JPARepositoryAdapter.java",
        "src/main/java/co/com/bancolombia/jpa/helper/AdapterOperations.java",
        "src/main/java/co/com/bancolombia/jpa/config/DBSecret.java",
        "src/main/java/co/com/bancolombia/jpa/config/JpaConfig.java");
  }

  @Test
  void generateDrivenAdapterMongoRepository() throws IOException, CleanException {
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
  void generateDrivenAdapterMongoRepositoryWithSecrets() throws IOException, CleanException {
    // Arrange
    task.setType("MONGODB");
    task.setSecret(AbstractCleanArchitectureDefaultTask.BooleanOption.TRUE);
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
    assertFilesExistsInDir(
        TEST_DIR + "/applications/app-service/",
        "src/main/java/co/com/bancolombia/config/SecretsConfig.java");
  }

  @Test
  void generateDrivenAdapterMongoRepositoryForNoProjectType() throws IOException, CleanException {
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
  void shouldGetSecretOptions() {
    // Arrange
    // Act
    List<AbstractCleanArchitectureDefaultTask.BooleanOption> options = task.getSecretOptions();
    // Assert
    assertEquals(2, options.size());
  }

  @Test
  void generateDrivenAdapterRedisRepositoryForImperative() throws IOException, CleanException {
    // Arrange
    task.setType("REDIS");
    task.setMode(DrivenAdapterRedis.Mode.REPOSITORY);
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/driven-adapters/redis/",
        "build.gradle",
        "src/main/java/co/com/bancolombia/redis/repository/helper/RepositoryAdapterOperations.java",
        "src/main/java/co/com/bancolombia/redis/repository/RedisRepository.java",
        "src/main/java/co/com/bancolombia/redis/repository/RedisRepositoryAdapter.java");
  }

  @Test
  void generateDrivenAdapterRedisTemplateForImperative() throws IOException, CleanException {
    // Arrange
    task.setType("REDIS");
    task.setMode(DrivenAdapterRedis.Mode.TEMPLATE);
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/driven-adapters/redis/",
        "build.gradle",
        "src/main/java/co/com/bancolombia/redis/template/helper/TemplateAdapterOperations.java",
        "src/main/java/co/com/bancolombia/redis/template/RedisTemplateAdapter.java");
  }

  @Test
  void generateDrivenAdapterRedisRepositoryForImperativeWithSecret()
      throws IOException, CleanException {
    // Arrange
    task.setType("REDIS");
    task.setMode(DrivenAdapterRedis.Mode.REPOSITORY);
    task.setSecret(AbstractCleanArchitectureDefaultTask.BooleanOption.TRUE);

    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/driven-adapters/redis/",
        "build.gradle",
        "src/main/java/co/com/bancolombia/redis/repository/helper/RepositoryAdapterOperations.java",
        "src/main/java/co/com/bancolombia/redis/repository/RedisRepository.java",
        "src/main/java/co/com/bancolombia/redis/repository/RedisRepositoryAdapter.java");
  }

  @Test
  void generateDrivenAdapterBinStashTemplateForImperative() throws IOException, CleanException {
    // Arrange
    task.setType("BINSTASH");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/driven-adapters/bin-stash/",
        "build.gradle",
        "src/main/java/co/com/bancolombia/binstash/config/BinStashCacheConfig.java");
  }

  @Test
  void generateDrivenAdapterKMSForImperative() throws IOException, CleanException {
    // Arrange
    task.setType("KMS");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/driven-adapters/kms-repository/",
        "build.gradle",
        "src/main/java/co/com/bancolombia/kms/KmsAdapter.java",
        "src/main/java/co/com/bancolombia/kms/config/KmsConfig.java",
        "src/main/java/co/com/bancolombia/kms/config/model/KmsConnectionProperties.java");
  }

  @Test
  void generateDrivenAdapterS3ForImperative() throws IOException, CleanException {
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
  void generateDrivenAdapterDynamoDB() throws IOException, CleanException {
    // Arrange
    task.setType("DYNAMODB");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(TEST_DIR + "/infrastructure/driven-adapters/dynamo-db/", "build.gradle");
  }
}
