package co.com.bancolombia.utils.offline;

import co.com.bancolombia.models.DependencyRelease;
import co.com.bancolombia.utils.FileUtils;
import co.com.bancolombia.utils.Utils;
import co.com.bancolombia.utils.operations.ExternalOperations;
import co.com.bancolombia.utils.operations.OperationsProvider;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import lombok.Builder;
import lombok.Setter;

@Setter
@Builder(setterPrefix = "with")
public class UpdateProjectDependencies {
  public static final String BUILD_GRADLE_FILE = "build.gradle";

  @Builder.Default private ExternalOperations operations = OperationsProvider.real();
  @Builder.Default private String constantsPath = BUILD_GRADLE_FILE;

  public static UpdateProjectDependencies ofDefaults() {
    return UpdateProjectDependencies.builder().build();
  }

  public void run() throws IOException {
    File buildGradle = Paths.get(constantsPath).toFile();
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

    FileUtils.writeContentToFile(content, buildGradle);
  }
}
