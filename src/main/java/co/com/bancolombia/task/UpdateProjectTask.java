package co.com.bancolombia.task;

import co.com.bancolombia.models.DependencyRelease;
import co.com.bancolombia.models.Release;
import co.com.bancolombia.utils.RestConsumer;
import co.com.bancolombia.utils.Utils;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

public class UpdateProjectTask extends CleanArchitectureDefaultTask {
  public static final String PLUGIN_RELEASES =
      "https://api.github.com/repos/bancolombia/scaffold-clean-architecture/releases";
  public static final String DEPENDENCY_RELEASES =
      "https://search.maven.org/solrsearch/select?q=g:%22%s%22+AND+a:%22%s%22&core=gav&rows=1&wt=json";
  private List<String> dependencies = new LinkedList<>();

  @Option(option = "dependencies", description = "Set dependencies to update")
  public void setDependencies(String dependencies) {
    this.dependencies.addAll(Arrays.asList(dependencies.split("[ ,]+")));
  }

  @TaskAction
  public void updateProject() throws IOException {
    logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
    logger.lifecycle(
        "Dependencies to update: {}",
        (dependencies.isEmpty() ? "all" : String.join(", ", dependencies)));

    Release latestRelease = getLatestPluginVersion();
    if (latestRelease != null) {
      logger.lifecycle("Latest version: {}", latestRelease.getTagName());

      updatePlugin(latestRelease.getTagName());
    }
    updateDependencies();

    builder.persist();
  }

  private Release getLatestPluginVersion() throws IOException {
    try {
      return RestConsumer.callRequest(PLUGIN_RELEASES, Release[].class)[0];
    } catch (Exception e) {
      logger.lifecycle("\tx Can't update the plugin " + e.getMessage());
      return null;
    }
  }

  private DependencyRelease getDependencyReleases(String dependency) throws IOException {
    try {
      return RestConsumer.callRequest(getDependencyEndpoint(dependency), DependencyRelease.class);
    } catch (NullPointerException e) {
      logger.lifecycle("\tx Can't update this dependency " + dependency);
      return null;
    }
  }

  private String getDependencyEndpoint(String dependency) {
    String[] id = dependency.split(":");
    if (id.length >= 2) {
      return DEPENDENCY_RELEASES.replaceFirst("%s", id[0]).replace("%s", id[1]);
    }
    throw new IllegalArgumentException(
        dependency
            + "is not a valid dependency usage: gradle u "
            + "--dependency "
            + "dependency.group:artifact");
  }

  private void updatePlugin(String lastRelease) throws IOException {

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
      // find all dependencies
      for (String gradleFile : gradleFiles) {
        dependencies.addAll(builder.findExpressions(gradleFile, "['\"].+:.+:[^\\$].+['\"]"));
      }
    }
    dependencies = dependencies.stream().distinct().collect(Collectors.toList());
    logger.lifecycle(dependencies.size() + " different dependencies to update");
    for (String dependency : dependencies) {
      DependencyRelease latestDependency = getDependencyReleases(dependency);
      if (latestDependency != null) {
        logger.lifecycle("\t- " + latestDependency.toString());
        for (String gradleFile : gradleFiles) {
          builder.updateExpression(
              gradleFile,
              "['\"]" + dependency + ":[^\\$].+",
              "'" + latestDependency.toString() + "'");
        }
      }
    }
  }
}
