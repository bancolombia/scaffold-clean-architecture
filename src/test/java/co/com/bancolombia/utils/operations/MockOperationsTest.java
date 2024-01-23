package co.com.bancolombia.utils.operations;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.com.bancolombia.models.DependencyRelease;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MockOperationsTest {
  private final ExternalOperations operations = new MockOperations();
  @Mock private DependencyRelease dependencyRelease;

  @Test
  void getLatestPluginVersion() {
    // Act
    // Assert
    assertNull(operations.getLatestPluginVersion());
  }

  @Test
  void getGradleWrapperFromFile() {
    // Act
    // Assert
    assertTrue(operations.getGradleWrapperFromFile().isEmpty());
  }

  @Test
  void getLatestGradlePluginVersion() {
    // Act
    // Assert
    assertTrue(operations.getLatestGradlePluginVersion(dependencyRelease).isEmpty());
  }

  @Test
  void shouldGetReal() {
    // Act
    // Assert
    assertTrue(operations.getTheLastDependencyRelease(dependencyRelease).isEmpty());
  }
}
