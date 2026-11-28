package co.com.bancolombia.utils.operations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import co.com.bancolombia.models.DependencyRelease;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.IntStream;
import org.gradle.api.logging.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DependencyLookupTest {
  private static final String SOURCE = "https://repo/maven-metadata.xml";

  @Mock private Logger logger;

  @Test
  void shouldKeepOnlyNewerReleasesAndLogEachResult() {
    DependencyRelease outdated = DependencyRelease.from("group:outdated:1.0.0");
    DependencyRelease upToDate = DependencyRelease.from("group:up-to-date:2.0.0");
    DependencyRelease missing = DependencyRelease.from("group:missing:1.0.0");
    DependencyRelease broken = DependencyRelease.from("group:broken:1.0.0");
    DependencyRelease newer = DependencyRelease.from("group:outdated:1.1.0");
    Map<DependencyRelease, LookupResult<DependencyRelease>> results =
        Map.of(
            outdated, LookupResult.found(newer),
            upToDate, LookupResult.found(DependencyRelease.from("group:up-to-date:2.0.0")),
            missing, LookupResult.notFound(SOURCE),
            broken, LookupResult.failed(SOURCE, "HTTP 500"));

    DependencyLookup.Result result =
        DependencyLookup.lookup(List.of(outdated, upToDate, missing, broken), results::get, logger);

    assertEquals(List.of(new DependencyLookup.Update(outdated, newer)), result.updates());
    verify(logger).info("{} has a newer version {}", outdated, "1.1.0");
    result.logSummary(logger, result.updates().size());
    verify(logger).info("{} is up to date", upToDate);
    verify(logger).warn("{} not found in {}", missing.toString(), SOURCE);
    verify(logger).warn("Can't check {} in {}: {}", broken.toString(), SOURCE, "HTTP 500");
    verify(logger)
        .lifecycle("Dependencies: {} updated, {} up to date, {} not found, {} failed", 1, 1, 1, 1);
  }

  @Test
  void shouldLookUpInParallelAndKeepTheOrderOfTheDependencies() {
    List<DependencyRelease> dependencies =
        IntStream.range(0, DependencyLookup.MAX_PARALLEL_LOOKUPS * 3)
            .mapToObj(i -> DependencyRelease.from("group:artifact-" + i + ":1.0.0"))
            .toList();
    Set<String> threads = ConcurrentHashMap.newKeySet();
    Function<DependencyRelease, LookupResult<DependencyRelease>> finder =
        dependency -> {
          threads.add(Thread.currentThread().getName());
          return LookupResult.found(
              DependencyRelease.from(
                  dependency.getGroup() + ":" + dependency.getArtifact() + ":2.0.0"));
        };

    List<DependencyLookup.Update> updates =
        DependencyLookup.lookup(dependencies, finder, logger).updates();

    assertEquals(
        dependencies.stream().map(DependencyRelease::getArtifact).toList(),
        updates.stream().map(update -> update.latest().getArtifact()).toList());
    assertTrue(threads.size() <= DependencyLookup.MAX_PARALLEL_LOOKUPS);
  }

  @Test
  void shouldReportALookupThatThrowsAsFailed() {
    DependencyRelease dependency = DependencyRelease.from("group:artifact:1.0.0");

    DependencyLookup.Result result =
        DependencyLookup.lookup(
            List.of(dependency),
            ignored -> {
              throw new IllegalStateException("boom");
            },
            logger);

    assertTrue(result.updates().isEmpty());
    assertEquals(1, result.failed());
  }

  @Test
  void shouldDoNothingWithoutDependencies() {
    assertTrue(DependencyLookup.lookup(List.of(), d -> null, logger).updates().isEmpty());
    verifyNoInteractions(logger);
  }

  @Test
  void shouldLookUpPluginsInThePluginPortalAndDependenciesInMaven() {
    ExternalOperations operations = mock(ExternalOperations.class);
    DependencyRelease plugin = DependencyRelease.from("id org.springframework.boot version 4.1.2");
    DependencyRelease library = DependencyRelease.from("group:library:1.0.0");
    DependencyRelease newerPlugin =
        DependencyRelease.from("id org.springframework.boot version 4.2.0");
    when(operations.findLatestGradlePlugin(plugin)).thenReturn(LookupResult.found(newerPlugin));
    when(operations.findLatestDependency(library)).thenReturn(LookupResult.notFound(SOURCE));

    DependencyLookup.Result result =
        DependencyLookup.lookup(List.of(plugin, library), operations, logger);

    assertEquals(List.of(new DependencyLookup.Update(plugin, newerPlugin)), result.updates());
    assertEquals(1, result.notFound());
  }

  @Test
  void shouldLookUpAnArtifactOnceAgainstItsOldestDeclaredVersion() {
    DependencyRelease older = DependencyRelease.from("group:artifact:1.0.0");
    DependencyRelease newer = DependencyRelease.from("group:artifact:3.0.0");
    java.util.concurrent.atomic.AtomicInteger calls =
        new java.util.concurrent.atomic.AtomicInteger();

    DependencyLookup.Result result =
        DependencyLookup.lookup(
            List.of(newer, older),
            dependency -> {
              calls.incrementAndGet();
              return LookupResult.found(newer);
            },
            logger);

    assertEquals(1, calls.get());
    assertEquals(List.of(new DependencyLookup.Update(older, newer)), result.updates());
    assertEquals("1.0.0", result.updates().get(0).current().getVersion());
  }

  @Test
  void shouldNotCountTheLookupsThatAnInterruptionLeftUnfinished() {
    List<DependencyRelease> dependencies =
        List.of(
            DependencyRelease.from("group:first:1.0.0"),
            DependencyRelease.from("group:second:1.0.0"));
    // Never released: each lookup waits until the interruption cancels it
    CountDownLatch never = new CountDownLatch(1);
    Function<DependencyRelease, LookupResult<DependencyRelease>> slow =
        dependency -> {
          try {
            never.await(5, TimeUnit.SECONDS);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
          return LookupResult.found(dependency);
        };

    Thread.currentThread().interrupt();
    DependencyLookup.Result result;
    try {
      result = DependencyLookup.lookup(dependencies, slow, logger);
    } finally {
      assertTrue(Thread.interrupted());
    }

    assertEquals(new DependencyLookup.Result(List.of(), 0, 0, 0), result);
    verify(logger).warn("Dependency lookup interrupted, {} dependencies were not checked", 2);
  }
}
