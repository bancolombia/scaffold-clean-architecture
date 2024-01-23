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

public class GenerateDrivenAdapterTaskKotlinImperativeTest {
  private static final String TEST_DIR =
      getTestDir(GenerateDrivenAdapterTaskKotlinImperativeTest.class);
  private static GenerateDrivenAdapterTask task;

  @BeforeAll
  public static void setup() throws IOException, CleanException {
    deleteStructure(Path.of(TEST_DIR));
    Project project =
        setupProject(
            GenerateDrivenAdapterTaskKotlinImperativeTest.class, GenerateStructureTask.class);

    GenerateStructureTask taskStructure = getTask(project, GenerateStructureTask.class);
    taskStructure.setType(GenerateStructureTask.ProjectType.IMPERATIVE);
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
  void generateDrivenAdapterGeneric() throws IOException, CleanException {
    // Arrange
    task.setType("GENERIC");
    task.setName("MyDrivenAdapter");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/driven-adapters/my-driven-adapter/",
        "build.gradle.kts",
        "src/main/kotlin/co/com/bancolombia/mydrivenadapter",
        "src/test/kotlin/co/com/bancolombia/mydrivenadapter");
  }

  @Test
  void generateRestConsumerForKotlin() throws IOException, CleanException {
    // Arrange
    task.setType("RESTCONSUMER");
    task.setUrl("http://localhost:8080");
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
  void generateDrivenAdapterJPARepository() throws IOException, CleanException {
    // Arrange
    task.setType("JPA");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/driven-adapters/jpa-repository/",
        "build.gradle.kts",
        "src/main/kotlin/co/com/bancolombia/jpa/JPARepository.kt",
        "src/main/kotlin/co/com/bancolombia/jpa/JPARepositoryAdapter.kt",
        "src/main/kotlin/co/com/bancolombia/jpa/helper/AdapterOperations.kt",
        "src/main/kotlin/co/com/bancolombia/jpa/config/DBSecret.kt",
        "src/main/kotlin/co/com/bancolombia/jpa/config/JpaConfig.kt");
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
        "build.gradle.kts",
        "src/main/kotlin/co/com/bancolombia/mongo/MongoDBRepository.kt",
        "src/main/kotlin/co/com/bancolombia/mongo/MongoRepositoryAdapter.kt",
        "src/main/kotlin/co/com/bancolombia/mongo/helper/AdapterOperations.kt",
        "src/main/kotlin/co/com/bancolombia/mongo/config/MongoDBSecret.kt",
        "src/main/kotlin/co/com/bancolombia/mongo/config/MongoConfig.kt");
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
        "build.gradle.kts",
        "src/main/kotlin/co/com/bancolombia/mongo/MongoDBRepository.kt",
        "src/main/kotlin/co/com/bancolombia/mongo/MongoRepositoryAdapter.kt",
        "src/main/kotlin/co/com/bancolombia/mongo/helper/AdapterOperations.kt",
        "src/main/kotlin/co/com/bancolombia/mongo/config/MongoDBSecret.kt",
        "src/main/kotlin/co/com/bancolombia/mongo/config/MongoConfig.kt");
    assertFilesExistsInDir(
        TEST_DIR + "/applications/app-service/",
        "src/main/kotlin/co/com/bancolombia/config/SecretsConfig.kt");
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
        "build.gradle.kts",
        "src/main/kotlin/co/com/bancolombia/mongo/MongoDBRepository.kt",
        "src/main/kotlin/co/com/bancolombia/mongo/MongoRepositoryAdapter.kt",
        "src/main/kotlin/co/com/bancolombia/mongo/helper/AdapterOperations.kt",
        "src/main/kotlin/co/com/bancolombia/mongo/config/MongoDBSecret.kt",
        "src/main/kotlin/co/com/bancolombia/mongo/config/MongoConfig.kt");
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
        "build.gradle.kts",
        "src/main/kotlin/co/com/bancolombia/redis/repository/helper/RepositoryAdapterOperations.kt",
        "src/main/kotlin/co/com/bancolombia/redis/repository/RedisRepository.kt",
        "src/main/kotlin/co/com/bancolombia/redis/repository/RedisRepositoryAdapter.kt");
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
        "build.gradle.kts",
        "src/main/kotlin/co/com/bancolombia/redis/template/helper/TemplateAdapterOperations.kt",
        "src/main/kotlin/co/com/bancolombia/redis/template/RedisTemplateAdapter.kt");
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
        "build.gradle.kts",
        "src/main/kotlin/co/com/bancolombia/kms/KmsAdapter.kt",
        "src/main/kotlin/co/com/bancolombia/kms/config/KmsConfig.kt",
        "src/main/kotlin/co/com/bancolombia/kms/config/model/KmsConnectionProperties.kt");
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
        "build.gradle.kts",
        "src/main/kotlin/co/com/bancolombia/s3/adapter/S3Adapter.kt",
        "src/main/kotlin/co/com/bancolombia/s3/operations/S3Operations.kt",
        "src/main/kotlin/co/com/bancolombia/s3/config/S3Config.kt",
        "src/main/kotlin/co/com/bancolombia/s3/config/model/S3ConnectionProperties.kt");
  }

  @Test
  void generateDrivenAdapterKtor() throws IOException, CleanException {
    // Arrange
    task.setType("KTOR");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/driven-adapters/ktor-client/", "build.gradle.kts");
  }

  @Test
  void generateDrivenAdapterDynamoDB() throws IOException, CleanException {
    // Arrange
    task.setType("DYNAMODB");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/driven-adapters/dynamo-db/", "build.gradle.kts");
  }

  @Test
  void generateDrivenAdapterSQSSenderImperative() throws IOException, CleanException {
    // Arrange
    task.setType("SQS");
    // Act
    task.execute();
    // Assert
    assertFilesExistsInDir(
        TEST_DIR + "/infrastructure/driven-adapters/sqs-sender/", "build.gradle.kts");
  }
}
