package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.PLUGIN_ID;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import co.com.bancolombia.models.DependencyRelease;
import co.com.bancolombia.utils.offline.GradleWrapperUpdater;
import co.com.bancolombia.utils.operations.DependencyLookup;
import co.com.bancolombia.utils.operations.ExternalOperations;
import co.com.bancolombia.utils.operations.OperationsProvider;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.gradle.api.logging.Logger;

@AllArgsConstructor
public class ZUpdateDependencies implements UpgradeAction {
  public static final String DEPENDENCIES_TO_UPDATE =
      ZUpdateDependencies.class.getSimpleName() + "dependencies";
  public static final String FILES_TO_UPDATE = ZUpdateDependencies.class.getSimpleName() + "files";
  private final ExternalOperations operations;

  public ZUpdateDependencies() {
    operations = OperationsProvider.fromDefault();
  }

  @Override
  @SneakyThrows
  @SuppressWarnings("unchecked")
  public boolean up(ModuleBuilder builder) {
    Logger logger = builder.getLogger();
    List<String> gradleFiles = (List<String>) builder.getParam(FILES_TO_UPDATE);
    Set<String> deps = (Set<String>) builder.getParam(DEPENDENCIES_TO_UPDATE);

    boolean updateAll = deps.isEmpty();
    if (updateAll) {
      deps =
          gradleFiles.stream()
              .flatMap(file -> declarations(builder, file).stream())
              .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    Set<DependencyRelease> dependencies =
        deps.stream()
            .map(DependencyRelease::from)
            .collect(Collectors.toCollection(LinkedHashSet::new));

    String depsMsg = String.join("\n- ", deps);
    logger.lifecycle("Checking {} dependencies updates: \n- {}", dependencies.size(), depsMsg);

    DependencyLookup.Result result = DependencyLookup.lookup(dependencies, operations, logger);
    boolean dependenciesUpdated =
        result.updates().stream()
            .map(DependencyLookup.Update::latest)
            .map(release -> applyToFile(builder, gradleFiles, release))
            .reduce(false, (current, applied) -> current || applied);
    if (!dependencies.isEmpty()) {
      result.logSummary(logger);
    }
    return updateWrapper(builder, gradleFiles, updateAll) || dependenciesUpdated;
  }

  /**
   * Dependencies and Gradle plugins with a version written in the file. The plugins of this project
   * are left out: UpdatePluginVersion keeps them in the same major version.
   */
  private static Set<String> declarations(ModuleBuilder builder, String file) {
    Set<String> declarations =
        new LinkedHashSet<>(builder.findExpressions(file, DependencyRelease.DECLARATION_REGEX));
    builder.findExpressions(file, DependencyRelease.PLUGIN_DECLARATION_REGEX).stream()
        .filter(plugin -> !DependencyRelease.from(plugin).getGroup().startsWith(PLUGIN_ID))
        .forEach(declarations::add);
    return declarations;
  }

  private boolean updateWrapper(ModuleBuilder builder, List<String> files, boolean updateAll) {
    if (!updateAll) {
      return false; // only the dependencies requested by the user must change
    }
    return operations
        .findLatestGradleVersion()
        .valueOrWarn(builder.getLogger(), "the latest Gradle version")
        .map(
            version -> {
              GradleWrapperUpdater updater = new GradleWrapperUpdater(operations);
              boolean applied =
                  files.stream()
                      .map(file -> updateWrapper(builder, updater, version, file))
                      .reduce(false, (current, itemApplied) -> current || itemApplied);
              if (applied) {
                builder.getLogger().lifecycle("Gradle wrapper updated to {}", version);
              }
              return applied;
            })
        .orElse(false);
  }

  @SneakyThrows
  private boolean updateWrapper(
      ModuleBuilder builder, GradleWrapperUpdater updater, String version, String file) {
    return builder.updateFile(file, content -> updater.update(content, version));
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
        files.parallelStream()
            .map(file -> update(builder, release, file))
            .reduce(false, (current, itemApplied) -> current || itemApplied);
    if (applied) {
      builder.getLogger().lifecycle("Dependency updated: {}", release);
    }
    return applied;
  }

  @SneakyThrows
  private boolean update(ModuleBuilder builder, DependencyRelease release, String file) {
    return builder.updateExpression(
        file, release.toRegex(), Matcher.quoteReplacement(release.toString()));
  }
}
