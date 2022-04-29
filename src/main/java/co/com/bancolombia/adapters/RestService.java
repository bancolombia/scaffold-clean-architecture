package co.com.bancolombia.adapters;

import co.com.bancolombia.models.DependencyRelease;
import co.com.bancolombia.models.Release;
import co.com.bancolombia.utils.FileUtils;
import co.com.bancolombia.utils.RestConsumer;
import java.io.IOException;
import java.util.Optional;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

public class RestService {
  private final Operations operations;

  public RestService() {
    Operations definedOperations = null;
    try {
      if ("true".equals(FileUtils.readProperties(".", "simulateRest"))) {
        definedOperations = new SimulatedOperations();
      }
    } catch (IOException ignored) {
    }
    if (definedOperations == null) {
      definedOperations = new RestOperations();
    }
    this.operations = definedOperations;
  }

  public Release getLatestPluginVersion() {
    return operations.getLatestPluginVersion();
  }

  public Optional<DependencyRelease> getTheLastDependencyRelease(String dependency) {
    return operations.getTheLastDependencyRelease(dependency);
  }

  private static class RestOperations implements Operations {
    public static final String PLUGIN_RELEASES =
        "http://api.github.com/repos/bancolombia/scaffold-clean-architecture/releases";
    public static final String DEPENDENCY_RELEASES =
        "https://search.maven.org/solrsearch/select?q=g:%22%s%22+AND+a:%22%s%22&core=gav&rows=1&wt=json";
    private final Logger logger = Logging.getLogger(RestService.class);

    @Override
    public Release getLatestPluginVersion() {
      try {
        return RestConsumer.callRequest(PLUGIN_RELEASES, Release[].class)[0];
      } catch (Exception e) {
        logger.lifecycle(
            "\tx Can't check the latest version of the plugin, reason: {}", e.getMessage());
        return null;
      }
    }

    @Override
    public Optional<DependencyRelease> getTheLastDependencyRelease(String dependency) {
      try {
        return Optional.of(
            RestConsumer.callRequest(getDependencyEndpoint(dependency), DependencyRelease.class));
      } catch (Exception e) {
        logger.lifecycle(
            "\tx Can't update this dependency {}, reason: {}", dependency, e.getMessage());
        return Optional.empty();
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

  private static class SimulatedOperations implements Operations {

    @Override
    public Release getLatestPluginVersion() {
      return null;
    }

    @Override
    public Optional<DependencyRelease> getTheLastDependencyRelease(String dependency) {
      return Optional.empty();
    }
  }

  private interface Operations {
    Release getLatestPluginVersion();

    Optional<DependencyRelease> getTheLastDependencyRelease(String dependency);
  }
}
