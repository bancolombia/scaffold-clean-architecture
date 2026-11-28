package co.com.bancolombia.utils.operations;

import static org.junit.jupiter.api.Assertions.assertEquals;

import co.com.bancolombia.Constants;
import co.com.bancolombia.models.DependencyRelease;
import co.com.bancolombia.utils.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MockOperationsTest {
  private final ExternalOperations operations = new MockOperations();
  @Mock private DependencyRelease dependencyRelease;

  @Test
  void shouldReportEverythingAsUpToDate() {
    assertEquals(
        Utils.getVersionPlugin(), found(operations.findLatestPluginVersion()).getTagName());
    assertEquals(
        LookupResult.found(Constants.GRADLE_WRAPPER_VERSION), operations.findLatestGradleVersion());
    assertEquals(
        LookupResult.found(Constants.GRADLE_WRAPPER_SHA256),
        operations.findGradleDistributionSha256(Constants.GRADLE_WRAPPER_VERSION, "bin"));
    assertEquals(
        LookupResult.found(dependencyRelease), operations.findLatestDependency(dependencyRelease));
    assertEquals(
        LookupResult.found(dependencyRelease),
        operations.findLatestGradlePlugin(dependencyRelease));
  }

  @Test
  void shouldNotFindTheChecksumOfAnotherGradleVersion() {
    assertEquals(
        LookupResult.notFound(MockOperations.SOURCE),
        operations.findGradleDistributionSha256("0.0.0", "bin"));
  }

  private static <T> T found(LookupResult<T> result) {
    return result.toOptional().orElseThrow();
  }
}
