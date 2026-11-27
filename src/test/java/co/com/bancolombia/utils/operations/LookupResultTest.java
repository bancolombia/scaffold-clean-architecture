package co.com.bancolombia.utils.operations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.Optional;
import org.gradle.api.logging.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LookupResultTest {
  @Mock private Logger logger;

  @Test
  void shouldGiveTheValueOnlyWhenFound() {
    assertEquals(Optional.of("9.4.0"), LookupResult.found("9.4.0").toOptional());
    assertTrue(LookupResult.notFound("source").toOptional().isEmpty());
    assertTrue(LookupResult.failed("source", "timeout").toOptional().isEmpty());
  }

  @Test
  void shouldWarnWhyThereIsNoValue() {
    assertEquals(Optional.of("9.4.0"), LookupResult.found("9.4.0").valueOrWarn(logger, "Gradle"));
    verifyNoInteractions(logger);

    assertTrue(LookupResult.notFound("source").valueOrWarn(logger, "Gradle").isEmpty());
    verify(logger).warn("{} not found in {}", "Gradle", "source");

    assertTrue(LookupResult.failed("source", "timeout").valueOrWarn(logger, "Gradle").isEmpty());
    verify(logger).warn("Can't check {} in {}: {}", "Gradle", "source", "timeout");
  }
}
