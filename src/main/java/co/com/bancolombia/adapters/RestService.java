package co.com.bancolombia.adapters;

import co.com.bancolombia.models.DependencyRelease;
import co.com.bancolombia.models.Release;
import co.com.bancolombia.utils.RestConsumer;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

public class RestService {
  public static final String PLUGIN_RELEASES =
      "http://api.github.com/repos/bancolombia/scaffold-clean-architecture/releases";
  public static final String DEPENDENCY_RELEASES =
      "https://search.maven.org/solrsearch/select?q=g:%22%s%22+AND+a:%22%s%22&core=gav&rows=1&wt=json";
  private final Logger logger = Logging.getLogger(RestService.class);

  public Release getLatestPluginVersion() {
    try {
      return RestConsumer.callRequest(PLUGIN_RELEASES, Release[].class)[0];
    } catch (Exception e) {
      logger.lifecycle(
          "\tx Can't check the latest version of the plugin, reason: {}", e.getMessage());
      return null;
    }
  }

  public DependencyRelease getDependencyReleases(String dependency) {
    try {
      return RestConsumer.callRequest(getDependencyEndpoint(dependency), DependencyRelease.class);
    } catch (Exception e) {
      logger.lifecycle(
          "\tx Can't update this dependency {}, reason: {}", dependency, e.getMessage());
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
            + " is not a valid dependency usage: gradle u "
            + "--dependency "
            + "dependency.group:artifact");
  }
}
