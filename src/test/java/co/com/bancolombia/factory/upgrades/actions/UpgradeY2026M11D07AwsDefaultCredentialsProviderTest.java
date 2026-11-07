package co.com.bancolombia.factory.upgrades.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpgradeY2026M11D07AwsDefaultCredentialsProviderTest {
  private static final String SQS_CONFIG =
      "infrastructure/entry-points/sqs-listener/src/main/java/co/com/bancolombia/sqs/listener/config/SQSConfig.java";
  private static final String DYNAMO_CONFIG =
      "infrastructure/driven-adapters/dynamo-db/src/main/java/co/com/bancolombia/dynamodb/config/DynamoDBConfig.java";

  /** SQS configuration generated with the provider chain. */
  private static final String SQS_BEFORE =
      """
      package co.com.bancolombia.sqs.listener.config;

      import org.springframework.context.annotation.Bean;
      import org.springframework.context.annotation.Configuration;
      import software.amazon.awssdk.auth.credentials.AwsCredentialsProviderChain;
      import software.amazon.awssdk.auth.credentials.ContainerCredentialsProvider;
      import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
      import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
      import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
      import software.amazon.awssdk.auth.credentials.SystemPropertyCredentialsProvider;
      import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
      import software.amazon.awssdk.regions.Region;
      import software.amazon.awssdk.services.sqs.SqsAsyncClient;

      @Configuration
      public class SQSConfig {

          @Bean
          public SqsAsyncClient configSqs(SQSProperties properties) {
              return SqsAsyncClient.builder()
                      .region(Region.of(properties.region()))
                      .credentialsProvider(getProviderChain())
                      .build();
          }

          private AwsCredentialsProviderChain getProviderChain() {
              return AwsCredentialsProviderChain.builder()
                      .addCredentialsProvider(EnvironmentVariableCredentialsProvider.create())
                      .addCredentialsProvider(SystemPropertyCredentialsProvider.create())
                      .addCredentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
                      .addCredentialsProvider(ProfileCredentialsProvider.create())
                      .addCredentialsProvider(ContainerCredentialsProvider.builder().build())
                      .addCredentialsProvider(InstanceProfileCredentialsProvider.create())
                      .build();
          }

          protected String endpoint(SQSProperties properties) {
              return properties.endpoint();
          }
      }
      """;

  private static final String SQS_AFTER =
      """
      package co.com.bancolombia.sqs.listener.config;

      import org.springframework.context.annotation.Bean;
      import org.springframework.context.annotation.Configuration;
      import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
      import software.amazon.awssdk.regions.Region;
      import software.amazon.awssdk.services.sqs.SqsAsyncClient;

      @Configuration
      public class SQSConfig {

          @Bean
          public SqsAsyncClient configSqs(SQSProperties properties) {
              return SqsAsyncClient.builder()
                      .region(Region.of(properties.region()))
                      .credentialsProvider(DefaultCredentialsProvider.builder().build())
                      .build();
          }

          protected String endpoint(SQSProperties properties) {
              return properties.endpoint();
          }
      }
      """;

  /** DynamoDB (also S3) configuration generated with the web identity provider. */
  private static final String DYNAMO_BEFORE =
      """
      package co.com.bancolombia.dynamodb.config;

      import org.springframework.context.annotation.Bean;
      import org.springframework.context.annotation.Profile;
      import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
      import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
      import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

      public class DynamoDBConfig {

          @Bean
          @Profile({"local"})
          public DynamoDbClient amazonDynamoDBLocal() {
              return DynamoDbClient.builder()
                      .credentialsProvider(ProfileCredentialsProvider.create("default"))
                      .build();
          }

          @Bean
          @Profile({"dev", "cer", "pdn"})
          public DynamoDbClient amazonDynamoDB() {
              return DynamoDbClient.builder()
                      .credentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
                      .build();
          }
      }
      """;

  private static final String DYNAMO_AFTER =
      """
      package co.com.bancolombia.dynamodb.config;

      import org.springframework.context.annotation.Bean;
      import org.springframework.context.annotation.Profile;
      import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
      import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
      import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

      public class DynamoDBConfig {

          @Bean
          @Profile({"local"})
          public DynamoDbClient amazonDynamoDBLocal() {
              return DynamoDbClient.builder()
                      .credentialsProvider(ProfileCredentialsProvider.create("default"))
                      .build();
          }

          @Bean
          @Profile({"dev", "cer", "pdn"})
          public DynamoDbClient amazonDynamoDB() {
              return DynamoDbClient.builder()
                      .credentialsProvider(DefaultCredentialsProvider.builder().build())
                      .build();
          }
      }
      """;

  @Mock private Project project;
  @Mock private Logger logger;

  private Path projectDir;
  private ModuleBuilder builder;
  private UpgradeAction updater;

  @BeforeEach
  void setup() throws IOException {
    projectDir = Files.createTempDirectory("sample");
    when(project.getName()).thenReturn("UtilsTest");
    when(project.getLogger()).thenReturn(logger);
    when(project.getProjectDir()).thenReturn(projectDir.toFile());
    builder = new ModuleBuilder(project);
    updater = new UpgradeY2026M11D07AwsDefaultCredentialsProvider();

    assertNotNull(updater.name());
    assertNotNull(updater.description());
  }

  @Test
  void shouldReplaceTheProviderChainWithTheDefaultProvider() throws IOException {
    Path file = write(SQS_CONFIG, SQS_BEFORE);

    assertTrue(updater.up(builder));

    assertEquals(SQS_AFTER.stripTrailing(), read(file));
  }

  @Test
  void shouldReplaceTheWebIdentityProviderAndKeepTheLocalProfile() throws IOException {
    Path file = write(DYNAMO_CONFIG, DYNAMO_BEFORE);

    assertTrue(updater.up(builder));

    assertEquals(DYNAMO_AFTER.stripTrailing(), read(file));
  }

  @Test
  void shouldBeIdempotent() throws IOException {
    write(SQS_CONFIG, SQS_BEFORE);
    write(DYNAMO_CONFIG, DYNAMO_BEFORE);

    assertTrue(updater.up(builder));
    assertFalse(updater.up(builder));
  }

  @Test
  void shouldKeepACustomizedProviderChain() throws IOException {
    String customized =
        SQS_BEFORE.replace(
            ".addCredentialsProvider(WebIdentityTokenFileCredentialsProvider.create())",
            ".addCredentialsProvider(AnonymousCredentialsProvider.create())");
    Path file = write(SQS_CONFIG, customized);

    assertFalse(updater.up(builder));
    assertEquals(customized.stripTrailing(), read(file));
  }

  @Test
  void shouldUpgradeAProjectLocatedInsideABuildFolder() throws IOException {
    projectDir = Files.createDirectories(projectDir.resolve("build/toscan/reactive"));
    when(project.getProjectDir()).thenReturn(projectDir.toFile());
    builder = new ModuleBuilder(project);
    Path file = write(SQS_CONFIG, SQS_BEFORE);

    assertTrue(updater.up(builder));

    assertEquals(SQS_AFTER.stripTrailing(), read(file));
  }

  @Test
  void shouldIgnoreBuildOutputAndFilesWithoutAwsCredentials() throws IOException {
    Path buildFile = write("app/build/generated/SQSConfig.java", SQS_BEFORE);
    write("app/src/main/java/Other.java", "class Other {}\n");

    assertFalse(updater.up(builder));
    assertEquals(SQS_BEFORE, Files.readString(buildFile));
  }

  private Path write(String relativePath, String content) throws IOException {
    Path file = projectDir.resolve(relativePath);
    Files.createDirectories(file.getParent());
    Files.writeString(file, content);
    return file;
  }

  /** The builder does not keep the trailing new line when it reads a file. */
  private String read(Path file) throws IOException {
    return builder.readFile(file.toString()).stripTrailing();
  }
}
