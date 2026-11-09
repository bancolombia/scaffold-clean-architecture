package co.com.bancolombia.utils.offline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.com.bancolombia.models.DependencyRelease;
import co.com.bancolombia.utils.operations.ExternalOperations;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateProjectDependenciesTest {
  private static final String TEST_PATH = "build/project.gradle";
  private static final Path TEST_FILE = Path.of(TEST_PATH);
  private static final String NEW_SHA256 = "b".repeat(64);

  @Mock private ExternalOperations operations;
  private UpdateProjectDependencies task;
  private String originalContent;

  @BeforeEach
  void setup() throws IOException {
    Path original = Path.of(UpdateProjectDependencies.BUILD_GRADLE_FILE);
    Files.deleteIfExists(TEST_FILE);
    Files.copy(original, TEST_FILE);
    originalContent = Files.readString(original);
    task =
        UpdateProjectDependencies.builder()
            .withOperations(operations)
            .withFiles(List.of(TEST_PATH))
            .build();
  }

  @Test
  void shouldUpdateDependencies() throws IOException {
    // Arrange
    DependencyRelease dependencyRelease =
        DependencyRelease.from("com.github.spullara.mustache.java:compiler:TEST_VERSION");
    when(operations.getTheLastDependencyRelease(any())).thenReturn(Optional.of(dependencyRelease));

    DependencyRelease pluginRelease =
        DependencyRelease.from("id org.sonarqube version GRADLE_VERSION");
    when(operations.getLatestGradlePluginVersion(any())).thenReturn(Optional.of(pluginRelease));
    // Act
    task.run();
    // Assert
    String result = Files.readString(TEST_FILE);
    assertNotEquals(result, originalContent);
    assertTrue(result.contains("TEST_VERSION"));
    assertTrue(result.contains("GRADLE_VERSION"));
  }

  @Test
  void shouldUpdateWrapperVersionAndChecksumTogether() throws IOException {
    // Arrange
    when(operations.getGradleWrapperFromFile()).thenReturn(Optional.of("9.9.0"));
    when(operations.getGradleDistributionSha256("9.9.0", "bin"))
        .thenReturn(Optional.of(NEW_SHA256));
    // Act
    task.run();
    // Assert
    String result = Files.readString(TEST_FILE);
    assertTrue(result.contains("gradleVersion = '9.9.0'"));
    assertTrue(result.contains("distributionSha256Sum = '" + NEW_SHA256 + "'"));
  }

  @Test
  void shouldKeepWrapperWhenChecksumIsNotAvailable() throws IOException {
    // Arrange
    when(operations.getGradleWrapperFromFile()).thenReturn(Optional.of("9.9.0"));
    when(operations.getGradleDistributionSha256("9.9.0", "bin")).thenReturn(Optional.empty());
    // Act
    task.run();
    // Assert
    assertEquals(originalContent, Files.readString(TEST_FILE));
  }

  @Test
  void shouldOnlyUpdateVersionWhenWrapperHasNoChecksum() throws IOException {
    // Arrange
    Files.writeString(TEST_FILE, "tasks.named('wrapper') {\n  gradleVersion = '9.8.2'\n}\n");
    when(operations.getGradleWrapperFromFile()).thenReturn(Optional.of("9.9.0"));
    // Act
    task.run();
    // Assert
    String result = Files.readString(TEST_FILE);
    assertTrue(result.contains("gradleVersion = '9.9.0'"));
    assertFalse(result.contains("distributionSha256Sum"));
    verify(operations, never()).getGradleDistributionSha256(any(), any());
  }

  @Test
  void shouldUseAllDistributionChecksum() throws IOException {
    // Arrange
    Files.writeString(
        TEST_FILE,
        """
        tasks.named('wrapper') {
          gradleVersion = '9.8.2'
          distributionType = Wrapper.DistributionType.ALL
          distributionSha256Sum = 'old'
        }
        """);
    when(operations.getGradleWrapperFromFile()).thenReturn(Optional.of("9.9.0"));
    when(operations.getGradleDistributionSha256("9.9.0", "all"))
        .thenReturn(Optional.of(NEW_SHA256));
    // Act
    task.run();
    // Assert
    String result = Files.readString(TEST_FILE);
    assertTrue(result.contains("gradleVersion = '9.9.0'"));
    assertTrue(result.contains("distributionSha256Sum = '" + NEW_SHA256 + "'"));
  }

  @Test
  void shouldWriteVersionsWithDollarSignLiterally() throws IOException {
    // Arrange
    when(operations.getTheLastDependencyRelease(any()))
        .thenReturn(
            Optional.of(
                DependencyRelease.from("com.github.spullara.mustache.java:compiler:1.2$1")));
    when(operations.getLatestGradlePluginVersion(any()))
        .thenReturn(Optional.of(DependencyRelease.from("id org.sonarqube version 5.0$1")));
    when(operations.getGradleWrapperFromFile()).thenReturn(Optional.of("9.9$1"));
    when(operations.getGradleDistributionSha256("9.9$1", "bin"))
        .thenReturn(Optional.of(NEW_SHA256));
    // Act
    task.run();
    // Assert
    String result = Files.readString(TEST_FILE);
    assertTrue(result.contains("'com.github.spullara.mustache.java:compiler:1.2$1'"));
    assertTrue(result.contains("id 'org.sonarqube' version '5.0$1'"));
    assertTrue(result.contains("gradleVersion = '9.9$1'"));
  }
}
