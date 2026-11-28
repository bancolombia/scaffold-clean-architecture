package co.com.bancolombia.utils.operations;

import co.com.bancolombia.models.DependencyRelease;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import lombok.experimental.UtilityClass;
import org.gradle.api.logging.Logger;

/**
 * Looks up the latest release of several dependencies and keeps the ones newer than the current
 * version. The lookups run in parallel with a small pool, so the repositories are not flooded, and
 * every result is logged by what happened: updated, up to date, not found or failed.
 */
@UtilityClass
public class DependencyLookup {
  static final int MAX_PARALLEL_LOOKUPS = 6;

  /** A dependency with a newer release. */
  public record Update(DependencyRelease current, DependencyRelease latest) {}

  /** The dependencies with a newer release, in the order they were given, and how many were not. */
  public record Result(List<Update> updates, int upToDate, int notFound, int failed) {
    static final Result EMPTY = new Result(List.of(), 0, 0, 0);

    /**
     * Logged by the caller once the updates are applied, with how many were written: an update can
     * find nothing to replace, such as a version given by a variable.
     */
    public void logSummary(Logger logger, int updated) {
      logger.lifecycle(
          "Dependencies: {} updated, {} up to date, {} not found, {} failed",
          updated,
          upToDate,
          notFound,
          failed);
    }
  }

  /** Looks up each dependency in the repository of its kind: Gradle plugin portal or Maven. */
  public static Result lookup(
      Collection<DependencyRelease> dependencies, ExternalOperations operations, Logger logger) {
    return lookup(
        dependencies,
        dependency ->
            dependency.isGradlePlugin()
                ? operations.findLatestGradlePlugin(dependency)
                : operations.findLatestDependency(dependency),
        logger);
  }

  public static Result lookup(
      Collection<DependencyRelease> dependencies,
      Function<DependencyRelease, LookupResult<DependencyRelease>> finder,
      Logger logger) {
    if (dependencies.isEmpty()) {
      return Result.EMPTY;
    }
    List<DependencyRelease> current = oldestPerArtifact(dependencies);
    List<LookupResult<DependencyRelease>> results = lookupAll(current, finder);
    Summary summary = new Summary();
    List<Update> updates = new ArrayList<>();
    int notChecked = 0;
    for (int i = 0; i < current.size(); i++) {
      DependencyRelease dependency = current.get(i);
      LookupResult<DependencyRelease> result = results.get(i);
      if (result == null) {
        notChecked++;
        continue;
      }
      classify(dependency, result, logger, summary)
          .ifPresent(latest -> updates.add(new Update(dependency, latest)));
    }
    if (notChecked > 0) {
      logger.warn("Dependency lookup interrupted, {} dependencies were not checked", notChecked);
    }
    return new Result(List.copyOf(updates), summary.upToDate, summary.notFound, summary.failed);
  }

  /**
   * One lookup per artifact. When it is declared with several versions the oldest is kept, so the
   * update is decided against it; the new version is then written over every declaration.
   */
  private static List<DependencyRelease> oldestPerArtifact(
      Collection<DependencyRelease> dependencies) {
    Map<DependencyRelease, DependencyRelease> oldest = new LinkedHashMap<>();
    for (DependencyRelease dependency : dependencies) {
      oldest.merge(
          dependency, dependency, (kept, candidate) -> kept.isNewest(candidate) ? candidate : kept);
    }
    return List.copyOf(oldest.values());
  }

  private static Optional<DependencyRelease> classify(
      DependencyRelease dependency,
      LookupResult<DependencyRelease> result,
      Logger logger,
      Summary summary) {
    if (result instanceof LookupResult.Found<DependencyRelease> found) {
      DependencyRelease latest = found.value();
      if (latest.isNewest(dependency)) {
        // The caller logs it once it is applied
        logger.info("{} has a newer version {}", dependency, latest.getVersion());
        return Optional.of(latest);
      }
      summary.upToDate++;
      logger.info("{} is up to date", dependency);
      return Optional.empty();
    }
    if (result instanceof LookupResult.NotFound<?>) {
      summary.notFound++;
    } else {
      summary.failed++;
    }
    result.valueOrWarn(logger, String.valueOf(dependency));
    return Optional.empty();
  }

  private static List<LookupResult<DependencyRelease>> lookupAll(
      List<DependencyRelease> dependencies,
      Function<DependencyRelease, LookupResult<DependencyRelease>> finder) {
    ExecutorService pool =
        Executors.newFixedThreadPool(Math.min(MAX_PARALLEL_LOOKUPS, dependencies.size()));
    try {
      List<Future<LookupResult<DependencyRelease>>> futures =
          dependencies.stream()
              .map(dependency -> pool.submit(() -> finder.apply(dependency)))
              .toList();
      List<LookupResult<DependencyRelease>> results = new ArrayList<>(futures.size());
      for (Future<LookupResult<DependencyRelease>> future : futures) {
        results.add(await(future));
      }
      return results;
    } finally {
      pool.shutdownNow();
    }
  }

  /**
   * The result of a lookup, or null when the task was interrupted before it finished: a finished
   * lookup is still returned, since get() does not wait for it.
   */
  private static LookupResult<DependencyRelease> await(
      Future<LookupResult<DependencyRelease>> future) {
    try {
      return future.get();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      future.cancel(true);
      return null;
    } catch (ExecutionException e) {
      return LookupResult.failed("the lookup", String.valueOf(e.getCause()));
    }
  }

  private static class Summary {
    private int upToDate;
    private int notFound;
    private int failed;
  }
}
