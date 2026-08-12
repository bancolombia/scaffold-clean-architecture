package co.com.bancolombia.factory.upgrades.actions;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpgradeY2025M12D12SpringBoot4Test {

  @Mock private Project project;
  @Mock private Logger logger;
  private ModuleBuilder builder;
  private UpgradeAction updater;
  private File tempDir;

  @BeforeEach
  void setup() throws IOException {
    tempDir = Files.createTempDirectory("sample").toFile();
    when(project.getName()).thenReturn("UtilsTest");
    when(project.getLogger()).thenReturn(logger);
    when(project.getProjectDir()).thenReturn(tempDir);

    updater = new UpgradeY2025M12D12SpringBoot4();
    assertNotNull(updater.name());
    assertNotNull(updater.description());
  }

  @Test
  void shouldApplyUpdateJava() throws IOException {
    builder = spy(new ModuleBuilder(project));
    File sampleFile = new File(tempDir, "Sample.java");
    Files.writeString(
        sampleFile.toPath(), "import org.springframework.boot.actuate.health.Health;");
    String file = sampleFile.getAbsolutePath();
    // Act
    boolean applied = updater.up(builder);
    // Assert
    assertTrue(applied);
    verify(builder, times(1))
        .addFile(file, "import org.springframework.boot.health.contributor.Health;");
  }

  @Test
  void shouldApplyUpdateGradle() throws IOException {
    builder = spy(new ModuleBuilder(project));
    File gradleFile = new File(tempDir, "build.gradle");
    Files.writeString(
        gradleFile.toPath(), "testImplementation \"com.fasterxml.jackson.core:jackson-databind\"");
    String file = gradleFile.getAbsolutePath();
    // Act
    boolean applied = updater.up(builder);
    // Assert
    assertTrue(applied);
    verify(builder, times(1))
        .addFile(file, "testImplementation \"tools.jackson.core:jackson-databind\"");
  }

  @Test
  void shouldNormalizeLeftoverJackson2VersionAfterGroupIdMigration() throws IOException {
    builder = spy(new ModuleBuilder(project));
    File gradleFile = new File(tempDir, "build.gradle");
    Files.writeString(
        gradleFile.toPath(),
        "testImplementation \"com.fasterxml.jackson.core:jackson-databind:2.21.1\"");
    String file = gradleFile.getAbsolutePath();
    // Act
    boolean applied = updater.up(builder);
    // Assert
    assertTrue(applied);
    verify(builder, times(1))
        .addFile(file, "testImplementation \"tools.jackson.core:jackson-databind:3.2.1\"");
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("unchangedJacksonDependencyCases")
  void shouldNotChangeAlreadyValidOrManagedJacksonDependencies(String description, String content)
      throws IOException {
    builder = spy(new ModuleBuilder(project));
    File gradleFile = new File(tempDir, "build.gradle");
    Files.writeString(gradleFile.toPath(), content);
    String file = gradleFile.getAbsolutePath();
    // Act
    boolean applied = updater.up(builder);
    // Assert
    assertFalse(applied, "Expected no update to be applied for case: " + description);
    verify(builder, times(0)).addFile(eq(file), any());
  }

  private static Stream<Arguments> unchangedJacksonDependencyCases() {
    return Stream.of(
        Arguments.of(
            "already migrated Jackson 3 version",
            "testImplementation \"tools.jackson.core:jackson-databind:3.2.1\""),
        Arguments.of(
            "dependency without version managed by Spring Boot BOM",
            "implementation 'tools.jackson.core:jackson-databind'"),
        Arguments.of(
            "jackson-annotations group id and version",
            "testImplementation \"com.fasterxml.jackson.core:jackson-annotations:2.21.1\""));
  }
}
