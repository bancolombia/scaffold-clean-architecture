package co.com.bancolombia.utils.operations;

import java.util.Optional;
import org.gradle.api.logging.Logger;

/**
 * Result of a lookup in an external source. It tells apart a value that was found, a value that
 * does not exist in the source and a lookup that could not be completed, so callers can decide what
 * to do in each case, such as looking in another source only when the value was not found.
 */
public sealed interface LookupResult<T> {

  static <T> LookupResult<T> found(T value) {
    return new Found<>(value);
  }

  static <T> LookupResult<T> notFound(String source) {
    return new NotFound<>(source);
  }

  static <T> LookupResult<T> failed(String source, String reason) {
    return new Failed<>(source, reason);
  }

  /** The value when it was found, for callers that handle the other cases in the same way. */
  default Optional<T> toOptional() {
    return this instanceof Found<T> found ? Optional.of(found.value()) : Optional.empty();
  }

  /** The value when it was found; otherwise warns why there is none, naming what was looked up. */
  default Optional<T> valueOrWarn(Logger logger, String subject) {
    if (this instanceof NotFound<T> notFound) {
      logger.warn("{} not found in {}", subject, notFound.source());
    } else if (this instanceof Failed<T> failed) {
      logger.warn("Can't check {} in {}: {}", subject, failed.source(), failed.reason());
    }
    return toOptional();
  }

  record Found<T>(T value) implements LookupResult<T> {}

  record NotFound<T>(String source) implements LookupResult<T> {}

  record Failed<T>(String source, String reason) implements LookupResult<T> {}
}
