package co.com.bancolombia.utils.offline;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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

  @Mock private ExternalOperations operations;
  private UpdateProjectDependencies task;
  private String originalContent;

  @BeforeEach
  void setup() throws IOException {
    Path original = Path.of(UpdateProjectDependencies.BUILD_GRADLE_FILE);
    Path destination = Path.of(TEST_PATH);
    Files.deleteIfExists(destination);
    Files.copy(original, destination);
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
    String result = Files.readString(Path.of(TEST_PATH));
    assertNotEquals(result, originalContent);
    assertTrue(result.contains("TEST_VERSION"));
    assertTrue(result.contains("GRADLE_VERSION"));
  }
}
