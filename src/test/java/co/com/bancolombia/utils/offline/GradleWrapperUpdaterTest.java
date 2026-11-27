package co.com.bancolombia.utils.offline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import co.com.bancolombia.utils.operations.ExternalOperations;
import co.com.bancolombia.utils.operations.LookupResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GradleWrapperUpdaterTest {
  private static final String SHA256 = "b".repeat(64);
  private static final String WRAPPER =
      """
      tasks.named('wrapper') {
          gradleVersion = '%s'
          distributionSha256Sum = '%s'
      }
      """;

  @Mock private ExternalOperations operations;

  @Test
  void shouldUpdateToANewerVersionWithItsChecksum() {
    when(operations.findGradleDistributionSha256("9.9.3", "bin"))
        .thenReturn(LookupResult.found(SHA256));

    String updated =
        new GradleWrapperUpdater(operations).update(WRAPPER.formatted("9.9.2", "a"), "9.9.3");

    assertEquals(WRAPPER.formatted("9.9.3", SHA256), updated);
  }

  @Test
  void shouldNotDowngradeTheWrapper() {
    String current = WRAPPER.formatted("9.9.3", "a");

    assertEquals(current, new GradleWrapperUpdater(operations).update(current, "9.2.0"));
    assertEquals(current, new GradleWrapperUpdater(operations).update(current, "9.9.3"));
    verifyNoInteractions(operations);
  }
}
