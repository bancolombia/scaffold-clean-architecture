package co.com.bancolombia.factory.upgrades.actions;

import co.com.bancolombia.adapters.RestService;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import co.com.bancolombia.models.DependencyRelease;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.gradle.api.logging.Logger;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class UpdateDependencies implements UpgradeAction {
  public static final String DEPENDENCIES_TO_UPDATE =
      UpdateDependencies.class.getSimpleName() + "dependencies";
  public static final String FILES_TO_UPDATE = UpdateDependencies.class.getSimpleName() + "files";
  private final RestService restService;

  public UpdateDependencies() {
    restService = new RestService();
  }

  @Override
  @SneakyThrows
  @SuppressWarnings("unchecked")
  public boolean up(ModuleBuilder builder) {
    Logger logger = builder.getProject().getLogger();
    logger.lifecycle("Updating dependencies");
    List<String> gradleFiles = (List<String>) builder.getParam(FILES_TO_UPDATE);
    Set<String> dependencies = (Set<String>) builder.getParam(DEPENDENCIES_TO_UPDATE);
    logger.lifecycle(
        "Dependencies to update: {}",
        (dependencies.isEmpty() ? "all" : String.join(", ", dependencies)));

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
        .forEach(applyToFile(gradleFiles, logger, builder));
    return true;
  }

  @Override
  public String name() {
    return "Dependencies update";
  }

  @Override
  public String description() {
    return "Update dependencies to the latest version";
  }

  @NotNull
  private Consumer<Optional<DependencyRelease>> applyToFile(
      List<String> gradleFiles, Logger logger, ModuleBuilder builder) {
    return dependencyRelease ->
        dependencyRelease.ifPresent(
            latestDependency -> {
              logger.lifecycle("\t- " + latestDependency);
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
                              "'" + latestDependency + "'"));
            });
  }
}
