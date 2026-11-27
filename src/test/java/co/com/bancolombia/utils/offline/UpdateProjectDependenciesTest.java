package co.com.bancolombia.utils.offline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.com.bancolombia.models.DependencyRelease;
import co.com.bancolombia.utils.operations.ExternalOperations;
import co.com.bancolombia.utils.operations.LookupResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
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
    lenient().when(operations.findLatestPluginVersion()).thenReturn(LookupResult.notFound("test"));
    lenient().when(operations.findLatestGradleVersion()).thenReturn(LookupResult.notFound("test"));
    lenient()
        .when(operations.findGradleDistributionSha256(anyString(), anyString()))
        .thenReturn(LookupResult.notFound("test"));
    lenient()
        .when(operations.findLatestDependency(any()))
        .thenReturn(LookupResult.notFound("test"));
    lenient()
        .when(operations.findLatestGradlePlugin(any()))
        .thenReturn(LookupResult.notFound("test"));
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
        DependencyRelease.from("com.github.spullara.mustache.java:compiler:999.0.0");
    when(operations.findLatestDependency(any())).thenReturn(LookupResult.found(dependencyRelease));

    DependencyRelease pluginRelease = DependencyRelease.from("id org.sonarqube version 999.1.0");
    when(operations.findLatestGradlePlugin(any())).thenReturn(LookupResult.found(pluginRelease));
    // Act
    task.run();
    // Assert
    String result = Files.readString(TEST_FILE);
    assertNotEquals(result, originalContent);
    assertTrue(result.contains("com.github.spullara.mustache.java:compiler:999.0.0"));
    assertTrue(result.contains("id 'org.sonarqube' version '999.1.0'"));
  }

  @Test
  void shouldUpdateWrapperVersionAndChecksumTogether() throws IOException {
    // Arrange
    when(operations.findLatestGradleVersion()).thenReturn(LookupResult.found("99.0.0"));
    when(operations.findGradleDistributionSha256("99.0.0", "bin"))
        .thenReturn(LookupResult.found(NEW_SHA256));
    // Act
    task.run();
    // Assert
    String result = Files.readString(TEST_FILE);
    assertTrue(result.contains("gradleVersion = '99.0.0'"));
    assertTrue(result.contains("distributionSha256Sum = '" + NEW_SHA256 + "'"));
  }

  @Test
  void shouldKeepWrapperWhenChecksumIsNotAvailable() throws IOException {
    // Arrange
    when(operations.findLatestGradleVersion()).thenReturn(LookupResult.found("99.0.0"));
    when(operations.findGradleDistributionSha256("99.0.0", "bin"))
        .thenReturn(LookupResult.notFound("test"));
    // Act
    task.run();
    // Assert
    assertEquals(
        originalContent.replace("\r\n", "\n"), Files.readString(TEST_FILE).replace("\r\n", "\n"));
  }

  @Test
  void shouldOnlyUpdateVersionWhenWrapperHasNoChecksum() throws IOException {
    // Arrange
    Files.writeString(TEST_FILE, "tasks.named('wrapper') {\n  gradleVersion = '9.8.2'\n}\n");
    when(operations.findLatestGradleVersion()).thenReturn(LookupResult.found("99.0.0"));
    // Act
    task.run();
    // Assert
    String result = Files.readString(TEST_FILE);
    assertTrue(result.contains("gradleVersion = '99.0.0'"));
    assertFalse(result.contains("distributionSha256Sum"));
    verify(operations, never()).findGradleDistributionSha256(any(), any());
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
    when(operations.findLatestGradleVersion()).thenReturn(LookupResult.found("99.0.0"));
    when(operations.findGradleDistributionSha256("99.0.0", "all"))
        .thenReturn(LookupResult.found(NEW_SHA256));
    // Act
    task.run();
    // Assert
    String result = Files.readString(TEST_FILE);
    assertTrue(result.contains("gradleVersion = '99.0.0'"));
    assertTrue(result.contains("distributionSha256Sum = '" + NEW_SHA256 + "'"));
  }

  @Test
  void shouldWriteVersionsWithDollarSignLiterally() throws IOException {
    // Arrange
    when(operations.findLatestDependency(any()))
        .thenReturn(
            LookupResult.found(
                DependencyRelease.from("com.github.spullara.mustache.java:compiler:1.2$1")));
    when(operations.findLatestGradlePlugin(any()))
        .thenReturn(LookupResult.found(DependencyRelease.from("id org.sonarqube version 5.0$1")));
    when(operations.findLatestGradleVersion()).thenReturn(LookupResult.found("99.9$1"));
    when(operations.findGradleDistributionSha256("99.9$1", "bin"))
        .thenReturn(LookupResult.found(NEW_SHA256));
    // Act
    task.run();
    // Assert
    String result = Files.readString(TEST_FILE);
    assertTrue(result.contains("'com.github.spullara.mustache.java:compiler:1.2$1'"));
    assertTrue(result.contains("id 'org.sonarqube' version '5.0$1'"));
    assertTrue(result.contains("gradleVersion = '99.9$1'"));
  }
}
