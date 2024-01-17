package co.com.bancolombia.factory.upgrades.actions;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import co.com.bancolombia.models.DependencyRelease;
import co.com.bancolombia.utils.http.RestService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.gradle.api.logging.Logger;

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
    List<String> gradleFiles = (List<String>) builder.getParam(FILES_TO_UPDATE);
    Set<String> deps = (Set<String>) builder.getParam(DEPENDENCIES_TO_UPDATE);

    if (deps.isEmpty()) {
      deps =
          gradleFiles.stream()
              .flatMap(file -> builder.findExpressions(file, "['\"].+:.+:[^\\$].+['\"]").stream())
              .collect(Collectors.toSet());
    }

    Set<DependencyRelease> dependencies =
        deps.stream().map(DependencyRelease::from).collect(Collectors.toSet());

    String depsMsg = String.join("\n- ", deps);
    logger.lifecycle("Checking {} dependencies updates: \n- {}", dependencies.size(), depsMsg);

    return dependencies.stream()
        .distinct()
        .map(restService::getTheLastDependencyRelease)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(release -> applyToFile(builder, gradleFiles, release))
        .reduce(false, (current, applied) -> current || applied);
  }

  @Override
  public String name() {
    return "Dependencies update";
  }

  @Override
  public String description() {
    return "Update dependencies to the latest version";
  }

  private boolean applyToFile(
      ModuleBuilder builder, List<String> files, DependencyRelease release) {
    boolean applied =
        files
            .parallelStream()
            .map(file -> update(builder, release, file))
            .reduce(false, (current, itemApplied) -> current || itemApplied);
    if (applied) {
      builder.getProject().getLogger().lifecycle("Dependency updated: {}", release);
    }
    return applied;
  }

  @SneakyThrows
  private boolean update(ModuleBuilder builder, DependencyRelease release, String file) {
    return builder.updateExpression(file, release.toRegex(), release.toString());
  }
}
