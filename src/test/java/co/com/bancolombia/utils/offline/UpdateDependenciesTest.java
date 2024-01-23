package co.com.bancolombia.utils.offline;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import co.com.bancolombia.models.DependencyRelease;
import co.com.bancolombia.models.Release;
import co.com.bancolombia.utils.operations.ExternalOperations;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UpdateDependenciesTest {
  private static final String TEST_PATH = "build/Constants.java";

  @Mock private ExternalOperations operations;
  private UpdateDependencies task;
  private String originalContent;

  @BeforeEach
  public void setup() throws IOException {
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
  public void shouldUpdateDependencies() throws IOException {
    // Arrange
    String newVersion = "TEST_VERSION";
    DependencyRelease dependencyRelease = DependencyRelease.from("a:b:1.0.0");
    Release release = new Release();
    release.setTagName("1.2.3");
    when(operations.getGradleWrapperFromFile()).thenReturn(Optional.of(newVersion));
    when(operations.getLatestGradlePluginVersion(any())).thenReturn(Optional.of(dependencyRelease));
    when(operations.getTheLastDependencyRelease(any())).thenReturn(Optional.of(dependencyRelease));
    // Act
    task.run();
    // Assert
    String result = Files.readString(Path.of(TEST_PATH));
    assertNotEquals(result, originalContent);
    assertTrue(result.contains(newVersion));
  }
}
