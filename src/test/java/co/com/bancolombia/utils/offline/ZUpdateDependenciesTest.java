package co.com.bancolombia.utils.offline;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import co.com.bancolombia.Constants;
import co.com.bancolombia.models.DependencyRelease;
import co.com.bancolombia.models.Release;
import co.com.bancolombia.utils.operations.ExternalOperations;
import co.com.bancolombia.utils.operations.LookupResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ZUpdateDependenciesTest {
  private static final String TEST_PATH = "build/Constants.java";
  private static final String NEW_SHA256 = "a".repeat(64);

  @Mock private ExternalOperations operations;
  private UpdateDependencies task;
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
    Path original = Path.of(UpdateDependencies.CONSTANTS_PATH);
    Path destination = Path.of(TEST_PATH);
    Files.deleteIfExists(destination);
    Files.copy(original, destination);
    originalContent = Files.readString(original);
    task =
        UpdateDependencies.builder()
            .withOperations(operations)
            .withConstantsPath(TEST_PATH)
            .build();
  }

  @Test
  void shouldUpdateDependencies() throws IOException {
    // Arrange
    String newVersion = "TEST_VERSION";
    DependencyRelease dependencyRelease = DependencyRelease.from("a:b:1.0.0");
    Release release = new Release();
    release.setTagName("1.2.3");
    when(operations.findLatestGradleVersion()).thenReturn(LookupResult.found(newVersion));
    when(operations.findGradleDistributionSha256(newVersion, "bin"))
        .thenReturn(LookupResult.found(NEW_SHA256));
    when(operations.findLatestGradlePlugin(any()))
        .thenReturn(LookupResult.found(dependencyRelease));
    when(operations.findLatestDependency(any())).thenReturn(LookupResult.found(dependencyRelease));
    // Act
    task.run();
    // Assert
    String result = Files.readString(Path.of(TEST_PATH));
    assertNotEquals(result, originalContent);
    assertTrue(result.contains("GRADLE_WRAPPER_VERSION = \"" + newVersion + "\";"));
    assertTrue(result.contains("GRADLE_WRAPPER_SHA256 = \"" + NEW_SHA256 + "\";"));
  }

  @Test
  void shouldKeepGradleWrapperWhenChecksumIsNotAvailable() throws IOException {
    // Arrange
    String newVersion = Constants.GRADLE_WRAPPER_VERSION + "-next";
    when(operations.findLatestGradleVersion()).thenReturn(LookupResult.found(newVersion));
    when(operations.findGradleDistributionSha256(newVersion, "bin"))
        .thenReturn(LookupResult.notFound("test"));
    // Act
    task.run();
    // Assert
    String result = Files.readString(Path.of(TEST_PATH));
    assertFalse(result.contains("\"" + newVersion + "\""));
    assertTrue(
        result.contains("GRADLE_WRAPPER_VERSION = \"" + Constants.GRADLE_WRAPPER_VERSION + "\";"));
    assertTrue(result.contains(Constants.GRADLE_WRAPPER_SHA256));
  }
}
