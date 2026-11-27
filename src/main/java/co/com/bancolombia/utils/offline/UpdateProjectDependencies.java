package co.com.bancolombia.utils.offline;

import co.com.bancolombia.models.DependencyRelease;
import co.com.bancolombia.utils.FileUtils;
import co.com.bancolombia.utils.Utils;
import co.com.bancolombia.utils.operations.DependencyLookup;
import co.com.bancolombia.utils.operations.ExternalOperations;
import co.com.bancolombia.utils.operations.OperationsProvider;
import java.io.File;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.Builder;
import lombok.Setter;
import lombok.SneakyThrows;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

@Setter
@Builder(setterPrefix = "with", toBuilder = true)
public class UpdateProjectDependencies {
  public static final String BUILD_GRADLE_FILE = "build.gradle";
  private static final Logger LOGGER = Logging.getLogger(UpdateProjectDependencies.class);

  @Builder.Default private ExternalOperations operations = OperationsProvider.fromDefault();
  @Builder.Default private List<String> files = List.of(BUILD_GRADLE_FILE);

  public static UpdateProjectDependencies ofDefaults() {
    return UpdateProjectDependencies.builder().build();
  }

  public void run() {
    files.forEach(this::updateDependency);
  }

  @SneakyThrows
  private void updateDependency(String file) {
    File buildGradle = Paths.get(file).toFile();
    String content = FileUtils.readFileAsString(buildGradle, null);
    content = updateDependencies(content);

    if (content.contains("wrapper")) {
      Optional<String> version =
          operations.findLatestGradleVersion().valueOrWarn(LOGGER, "the latest Gradle version");
      if (version.isPresent()) {
        content = new GradleWrapperUpdater(operations).update(content, version.get());
      }
    }

    FileUtils.writeContentToFile(content, buildGradle);
  }

  /** Dependencies and Gradle plugins with a version, looked up together in their repositories. */
  private String updateDependencies(String content) {
    Set<String> declarations =
        new LinkedHashSet<>(Utils.findExpressions(content, DependencyRelease.DECLARATION_REGEX));
    declarations.addAll(Utils.findExpressions(content, DependencyRelease.PLUGIN_DECLARATION_REGEX));
    // Every declaration, also of the same artifact with another version: the lookup keeps the
    // oldest
    List<DependencyRelease> dependencies =
        declarations.stream().map(DependencyRelease::from).toList();
    DependencyLookup.Result result = DependencyLookup.lookup(dependencies, operations, LOGGER);
    String updated = content;
    int applied = 0;
    for (DependencyLookup.Update update : result.updates()) {
      DependencyRelease release = update.latest();
      String next = release.updateOlderDeclarations(updated);
      if (!next.equals(updated)) {
        applied++;
        LOGGER.lifecycle("Dependency updated: {}", release);
      }
      updated = next;
    }
    if (!dependencies.isEmpty()) {
      result.logSummary(LOGGER, applied);
    }
    return updated;
  }
}
