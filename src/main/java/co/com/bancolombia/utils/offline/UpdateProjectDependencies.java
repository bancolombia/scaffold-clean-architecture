package co.com.bancolombia.utils.offline;

import co.com.bancolombia.models.DependencyRelease;
import co.com.bancolombia.utils.FileUtils;
import co.com.bancolombia.utils.Utils;
import co.com.bancolombia.utils.operations.ExternalOperations;
import co.com.bancolombia.utils.operations.OperationsProvider;
import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.Setter;
import lombok.SneakyThrows;

@Setter
@Builder(setterPrefix = "with")
public class UpdateProjectDependencies {
  public static final String BUILD_GRADLE_FILE = "build.gradle";

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
    content =
        Utils.findExpressions(content, "['\"].+:.+:[^\\$].+['\"]").stream()
            .map(DependencyRelease::from)
            .distinct()
            .map(operations::getTheLastDependencyRelease)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .reduce(
                content,
                (current, release) ->
                    Utils.replaceExpression(current, release.toRegex(), release.toString()),
                (current, next) -> next);

    content =
        Utils.findExpressions(content, "id\\s+['\"].+['\"]\\s+version\\s+['\"].+['\"]").stream()
            .map(DependencyRelease::from)
            .distinct()
            .map(operations::getLatestGradlePluginVersion)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .reduce(
                content,
                (current, release) ->
                    Utils.replaceExpression(current, release.toRegex(), release.toString()),
                (current, next) -> next);

    if (content.contains("wrapper")) {
      Optional<String> dep = operations.getGradleWrapperFromFile();
      if (dep.isPresent()) {
        content =
            Utils.replaceExpression(
                content, "gradleVersion\\s*=\\s*.*", "gradleVersion = '" + dep.get() + "'");
      }
    }

    FileUtils.writeContentToFile(content, buildGradle);
  }
}
