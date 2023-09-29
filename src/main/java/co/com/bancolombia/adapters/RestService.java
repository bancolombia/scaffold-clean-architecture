package co.com.bancolombia.adapters;

import co.com.bancolombia.models.DependencyRelease;
import co.com.bancolombia.models.Release;
import co.com.bancolombia.utils.FileUtils;
import co.com.bancolombia.utils.RestConsumer;
import java.util.Arrays;
import java.util.Optional;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

public class RestService {
  private final Operations operations;

  public RestService() {
    if (shouldMock()) {
      this.operations = new SimulatedOperations();
    } else {
      this.operations = new RestOperations();
    }
  }

  public Release getLatestPluginVersion() {
    return operations.getLatestPluginVersion();
  }

  public Optional<DependencyRelease> getTheLastDependencyRelease(DependencyRelease dependency) {
    return operations.getTheLastDependencyRelease(dependency);
  }

  private boolean shouldMock() {
    return FileUtils.readBooleanProperty("simulateRest");
  }

  private static class RestOperations implements Operations {
    public static final String PLUGIN_RELEASES =
        "https://api.github.com/repos/bancolombia/scaffold-clean-architecture/releases";
    public static final String DEPENDENCY_RELEASES =
        "https://search.maven.org/solrsearch/select?q=g:%22%s%22+AND+a:%22%s%22&core=gav&rows=5&wt=json";
    private final Logger logger = Logging.getLogger(RestOperations.class);

    @Override
    public Release getLatestPluginVersion() {
      try {
        return Arrays.stream(RestConsumer.callRequest(PLUGIN_RELEASES, Release[].class))
            .filter(
                release ->
                    !release.getTagName().contains("alpha")
                        && !release.getTagName().contains("beta"))
            .findFirst()
            .map(
                release ->
                    new Release(release.getTagName().replace("v", ""), release.getPublishedAt()))
            .orElse(null);
      } catch (Exception e) {
        logger.lifecycle(
            "\tx Can't check the latest version of the plugin, reason: {}", e.getMessage());
        return null;
      }
    }

    @Override
    public Optional<DependencyRelease> getTheLastDependencyRelease(DependencyRelease dependency) {
      try {
        DependencyRelease release =
            RestConsumer.callRequest(getDependencyEndpoint(dependency), DependencyRelease.class);
        return release.getVersion() != null ? Optional.of(release) : Optional.empty();
      } catch (Exception e) {
        logger.lifecycle(
            "\tx Can't update this dependency {}, reason: {}", dependency, e.getMessage());
        return Optional.empty();
      }
    }

    private String getDependencyEndpoint(DependencyRelease dependency) {
      if (dependency.valid()) {
        if (dependency.getArtifact().equals("okhttp3"))
          logger.lifecycle(
              DEPENDENCY_RELEASES
                  .replaceFirst("%s", dependency.getGroup())
                  .replace("%s", dependency.getArtifact()));
        return DEPENDENCY_RELEASES
            .replaceFirst("%s", dependency.getGroup())
            .replace("%s", dependency.getArtifact());
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
    public Optional<DependencyRelease> getTheLastDependencyRelease(DependencyRelease dependency) {
      return Optional.empty();
    }
  }

  private interface Operations {
    Release getLatestPluginVersion();

    Optional<DependencyRelease> getTheLastDependencyRelease(DependencyRelease dependency);
  }
}
