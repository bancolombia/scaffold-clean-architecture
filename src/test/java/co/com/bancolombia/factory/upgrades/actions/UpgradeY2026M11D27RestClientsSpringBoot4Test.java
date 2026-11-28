package co.com.bancolombia.factory.upgrades.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import co.com.bancolombia.Constants;
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
class UpgradeY2026M11D27RestClientsSpringBoot4Test {
  private static final String REST_CONSUMER =
      "infrastructure/driven-adapters/rest-consumer/build.gradle";
  private static final String COGNITO =
      "infrastructure/driven-adapters/cognito-token-provider/build.gradle";
  private static final String APP_SERVICE = "applications/app-service/build.gradle";
  private static final String ENTRY_POINT = "infrastructure/entry-points/reactive-web/build.gradle";
  private static final String REACTIVE_CONSUMER =
      """
      dependencies {
          implementation 'org.springframework.boot:spring-boot-starter-webflux'
          implementation 'io.github.resilience4j:resilience4j-spring-boot3:2.2.0'
          implementation 'io.github.resilience4j:resilience4j-reactor:2.2.0'

          testImplementation 'org.springframework.boot:spring-boot-starter-webflux-test'
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
    updater = new UpgradeY2026M11D27RestClientsSpringBoot4();

    assertNotNull(updater.name());
    assertNotNull(updater.description());
  }

  @Test
  void shouldUseTheWebClientStarterInTheReactiveConsumer() throws IOException {
    write(REST_CONSUMER, REACTIVE_CONSUMER);

    assertTrue(updater.up(builder));

    assertEquals(
        """
        dependencies {
            implementation 'org.springframework.boot:spring-boot-starter-webclient'
            implementation 'io.github.resilience4j:resilience4j-spring-boot4:%1$s'
            implementation 'io.github.resilience4j:resilience4j-reactor:%1$s'

            testImplementation 'org.springframework.boot:spring-boot-starter-webclient-test'
        }"""
            .formatted(Constants.RESILIENCE_4J_VERSION),
        read(REST_CONSUMER));
  }

  @Test
  void shouldUseTheRestClientStarterInTheImperativeConsumer() throws IOException {
    write(
        REST_CONSUMER,
        """
        dependencies {
            implementation 'org.springframework.boot:spring-boot-starter-webmvc'
            implementation 'io.github.resilience4j:resilience4j-spring-boot3:9.0.0'
            testImplementation 'org.springframework.boot:spring-boot-starter-webmvc-test'
        }
        """);

    assertTrue(updater.up(builder));

    assertEquals(
        """
        dependencies {
            implementation 'org.springframework.boot:spring-boot-starter-restclient'
            implementation 'io.github.resilience4j:resilience4j-spring-boot4:9.0.0'
            testImplementation 'org.springframework.boot:spring-boot-starter-restclient-test'
        }""",
        read(REST_CONSUMER));
  }

  @Test
  void shouldUseTheWebClientStarterInTheCognitoTokenProvider() throws IOException {
    write(
        COGNITO,
        "dependencies {\n    implementation 'org.springframework.boot:spring-boot-starter-webflux'\n}\n");

    assertTrue(updater.up(builder));

    assertEquals(
        "dependencies {\n    implementation 'org.springframework.boot:spring-boot-starter-webclient'\n}",
        read(COGNITO));
  }

  @Test
  void shouldRemoveTheServerStarterWhenTheClientOneIsAlreadyDeclared() throws IOException {
    write(
        REST_CONSUMER,
        """
        dependencies {
            implementation 'org.springframework.boot:spring-boot-starter-webflux'
            implementation 'org.springframework.boot:spring-boot-starter-webclient'
        }
        """);

    assertTrue(updater.up(builder));

    assertEquals(
        """
        dependencies {
            implementation 'org.springframework.boot:spring-boot-starter-webclient'
        }""",
        read(REST_CONSUMER));
  }

  @Test
  void shouldOnlyUpdateResilience4jInTheEntryPoints() throws IOException {
    write(
        ENTRY_POINT,
        """
        dependencies {
            implementation 'org.springframework.boot:spring-boot-starter-webflux'
            implementation 'io.github.resilience4j:resilience4j-spring-boot3:2.2.0'
            testImplementation 'org.springframework.boot:spring-boot-starter-webflux-test'
        }
        """);

    assertTrue(updater.up(builder));

    assertEquals(
        """
        dependencies {
            implementation 'org.springframework.boot:spring-boot-starter-webflux'
            implementation 'io.github.resilience4j:resilience4j-spring-boot4:%s'
            testImplementation 'org.springframework.boot:spring-boot-starter-webflux-test'
        }"""
            .formatted(Constants.RESILIENCE_4J_VERSION),
        read(ENTRY_POINT));
  }

  @Test
  void shouldNotChangeAnUpgradedProject() throws IOException {
    write(
        ENTRY_POINT,
        "dependencies {\n    implementation 'org.springframework.boot:spring-boot-starter-webflux'\n}\n");
    write(
        REST_CONSUMER,
        "dependencies {\n    implementation 'org.springframework.boot:spring-boot-starter-webclient'\n}\n");

    assertFalse(updater.up(builder));
  }

  @Test
  void shouldMoveTheServerStarterToAppServiceWhenNoEntryPointDeclaresIt() throws IOException {
    write(REST_CONSUMER, REACTIVE_CONSUMER);
    write(APP_SERVICE, "dependencies {\n    implementation project(':model')\n}\n");

    assertTrue(updater.up(builder));

    assertEquals(
        """
        dependencies {
            implementation 'org.springframework.boot:spring-boot-starter-webflux'
            implementation project(':model')
        }""",
        read(APP_SERVICE));
    assertTrue(read(REST_CONSUMER).contains("spring-boot-starter-webclient'"));
  }

  @Test
  void shouldNotTouchAppServiceWhenAnEntryPointDeclaresTheServer() throws IOException {
    String appService = "dependencies {\n    implementation project(':model')\n}";
    write(REST_CONSUMER, REACTIVE_CONSUMER);
    write(APP_SERVICE, appService + "\n");
    write(
        ENTRY_POINT,
        "dependencies {\n    implementation 'org.springframework.boot:spring-boot-starter-webflux'\n}\n");

    assertTrue(updater.up(builder));

    assertEquals(appService, read(APP_SERVICE));
  }

  @Test
  void shouldRenameResilience4jWithAVersionFromTheBomOrAVariable() {
    var action = new UpgradeY2026M11D27RestClientsSpringBoot4();

    assertEquals(
        "implementation 'io.github.resilience4j:resilience4j-spring-boot4'",
        action.useResilience4jBoot4(
            "implementation 'io.github.resilience4j:resilience4j-spring-boot3'"));
    assertEquals(
        "implementation \"io.github.resilience4j:resilience4j-spring-boot4:${resilience4jVersion}\"",
        action.useResilience4jBoot4(
            "implementation \"io.github.resilience4j:resilience4j-spring-boot3:${resilience4jVersion}\""));
  }

  @Test
  void shouldMoveTheServerWhenOtherModulesOnlyUseItForTests() throws IOException {
    write(REST_CONSUMER, REACTIVE_CONSUMER);
    write(APP_SERVICE, "dependencies {\n}\n");
    write(
        ENTRY_POINT,
        """
        dependencies {
            // implementation 'org.springframework.boot:spring-boot-starter-webflux'
            testImplementation 'org.springframework.boot:spring-boot-starter-webflux'
        }
        """);

    assertTrue(updater.up(builder));

    assertTrue(
        read(APP_SERVICE)
            .contains("    implementation 'org.springframework.boot:spring-boot-starter-webflux'"));
  }

  @Test
  void shouldNotMoveAServerThatTheClientOnlyUsedForTests() throws IOException {
    String appService = "dependencies {\n}";
    write(
        REST_CONSUMER,
        "dependencies {\n    testImplementation 'org.springframework.boot:spring-boot-starter-webmvc'\n}\n");
    write(APP_SERVICE, appService + "\n");

    assertTrue(updater.up(builder));

    assertEquals(appService, read(APP_SERVICE));
  }

  @Test
  void shouldUpgradeADrivenAdapterWithAnotherFolderName() throws IOException {
    String renamed = "infrastructure/driven-adapters/employee-consumer/build.gradle";
    write(renamed, REACTIVE_CONSUMER);
    write(
        ENTRY_POINT,
        "dependencies {\n    implementation 'org.springframework.boot:spring-boot-starter-webflux'\n}\n");

    assertTrue(updater.up(builder));

    String updated = read(renamed);
    assertTrue(
        updated.contains(
            "implementation 'org.springframework.boot:spring-boot-starter-webclient'"));
    assertTrue(updated.contains("spring-boot-starter-webclient-test'"));
    assertFalse(updated.contains("spring-boot-starter-webflux"));
    assertTrue(read(ENTRY_POINT).contains("spring-boot-starter-webflux'"));
  }

  private void write(String path, String content) throws IOException {
    Path file = projectDir.resolve(path);
    Files.createDirectories(file.getParent());
    Files.writeString(file, content);
  }

  private String read(String path) throws IOException {
    return builder.readFile(projectDir.resolve(path).toString());
  }
}
