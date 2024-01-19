package co.com.bancolombia.utils.operations;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import co.com.bancolombia.models.DependencyRelease;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MockOperationsTest {
  private final ExternalOperations operations = new MockOperations();
  @Mock private DependencyRelease dependencyRelease;

  @Test
  public void getLatestPluginVersion() {
    // Act
    // Assert
    assertNull(operations.getLatestPluginVersion());
  }

  @Test
  public void getGradleWrapperFromFile() {
    // Act
    // Assert
    assertTrue(operations.getGradleWrapperFromFile().isEmpty());
  }

  @Test
  public void getLatestGradlePluginVersion() {
    // Act
    // Assert
    assertTrue(operations.getLatestGradlePluginVersion(dependencyRelease).isEmpty());
  }

  @Test
  public void shouldGetReal() {
    // Act
    // Assert
    assertTrue(operations.getTheLastDependencyRelease(dependencyRelease).isEmpty());
  }
}
