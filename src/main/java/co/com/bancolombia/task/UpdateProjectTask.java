package co.com.bancolombia.task;

import co.com.bancolombia.adapters.RestService;
import co.com.bancolombia.models.DependencyRelease;
import co.com.bancolombia.models.Release;
import co.com.bancolombia.utils.Utils;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.jetbrains.annotations.NotNull;

public class UpdateProjectTask extends CleanArchitectureDefaultTask {
  private Set<String> dependencies = new HashSet<>();
  private final RestService restService = new RestService();

  @Option(option = "dependencies", description = "Set dependencies to update")
  public void setDependencies(String dependencies) {
    this.dependencies.addAll(Arrays.asList(dependencies.split("[ ,]+")));
  }

  @TaskAction
  public void updateProject() throws IOException {
    logger.lifecycle(
        "Dependencies to update: {}",
        (dependencies.isEmpty() ? "all" : String.join(", ", dependencies)));

    Release latestRelease = (Release) builder.getParam("latestRelease");
    if (latestRelease != null) {
      logger.lifecycle("Latest version: {}", latestRelease.getTagName());

      updatePlugin(latestRelease.getTagName());
    }
    updateDependencies();

    builder.persist();
  }

  private void updatePlugin(String lastRelease) {
    logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());

    if (lastRelease.equals(Utils.getVersionPlugin())) {
      logger.lifecycle("You are already using the latest version of the plugin");
      return;
    }
    logger.lifecycle("Updating the plugin ");

    if (builder.isKotlin()) {
      builder.updateExpression(
          "build.gradle.kts",
          "(id\\(\"co.com.bancolombia.cleanArchitecture\"\\)\\s?version\\s?).+",
          "$1\"" + lastRelease + "\"");
      return;
    }
    builder.updateExpression(
        "gradle.properties", "(systemProp.version\\s?=\\s?).+", "$1" + lastRelease);
    builder.updateExpression(
        "build.gradle", "(cleanArchitectureVersion\\s?=\\s?)'.+'", "$1'" + lastRelease + "'");

    logger.lifecycle("Plugin updated");
  }

  private void updateDependencies() throws IOException {
    logger.lifecycle("Updating dependencies");
    List<String> gradleFiles = Utils.getAllFilesWithExtension(builder.isKotlin());

    if (dependencies.isEmpty()) {
      dependencies =
          gradleFiles.stream()
              .flatMap(
                  gradleFile ->
                      builder.findExpressions(gradleFile, "['\"].+:.+:[^\\$].+['\"]").stream())
              .collect(Collectors.toSet());
    }
    logger.lifecycle(dependencies.size() + " different dependencies to update");

    dependencies.stream()
        .map(restService::getTheLastDependencyRelease)
        .forEach(updateDependencyInFiles(gradleFiles));
  }

  @NotNull
  private Consumer<Optional<DependencyRelease>> updateDependencyInFiles(List<String> gradleFiles) {
    return dependencyRelease ->
        dependencyRelease.ifPresent(
            latestDependency -> {
              logger.lifecycle("\t- " + latestDependency.toString());
              gradleFiles
                  .parallelStream()
                  .forEach(
                      gradleFile ->
                          builder.updateExpression(
                              gradleFile,
                              "['\"]"
                                  + latestDependency.getGroup()
                                  + ":"
                                  + latestDependency.getArtifact()
                                  + ":[^\\$].+",
                              "'" + latestDependency.toString() + "'"));
            });
  }
}
