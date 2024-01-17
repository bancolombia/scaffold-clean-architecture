package co.com.bancolombia.utils.http;

import co.com.bancolombia.models.DependencyRelease;
import co.com.bancolombia.models.DependencyReleaseXml;
import co.com.bancolombia.models.Release;
import co.com.bancolombia.utils.FileUtils;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

public class RestService {
  private final Operations operations;

  public RestService(boolean ignored) {
    this.operations = new RestOperations();
  }

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

  public Optional<DependencyRelease> getLatestGradlePluginVersion(DependencyRelease dependency) {
    return operations.getLatestGradlePluginVersion(dependency);
  }

  public Optional<String> getGradleWrapperFromFile() {
    return operations.getGradleWrapperFromFile();
  }

  private boolean shouldMock() {
    return FileUtils.readBooleanProperty("simulateRest");
  }

  private static class RestOperations implements Operations {
    public static final String PLUGIN_RELEASES =
        "https://api.github.com/repos/bancolombia/scaffold-clean-architecture/releases";
    public static final String DEPENDENCY_RELEASES =
        "https://search.maven.org/solrsearch/select?q=g:%22%group%22+AND+a:%22%artifact%22&core=gav&rows=5&wt=json";
    public static final String GRADLE_PLUGINS =
        "https://plugins.gradle.org/m2/%group/%artifact/maven-metadata.xml";
    public static final String SPRING_INITIALIZER = "https://start.spring.io/starter.zip";
    private final Logger logger = Logging.getLogger(RestOperations.class);

    @Override
    public Release getLatestPluginVersion() {
      try {
        return Arrays.stream(RestConsumer.getRequest(PLUGIN_RELEASES, Release[].class))
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
            RestConsumer.getRequest(getDependencyEndpoint(dependency), DependencyRelease.class);
        return release.getVersion() != null ? Optional.of(release) : Optional.empty();
      } catch (Exception e) {
        logger.lifecycle(
            "\tx Can't update this dependency {}, reason: {}", dependency, e.getMessage());
        return Optional.empty();
      }
    }

    @Override
    public Optional<DependencyRelease> getLatestGradlePluginVersion(DependencyRelease dependency) {
      try {
        return RestConsumer.getRequest(
                getGradlePluginEndpoint(dependency), DependencyReleaseXml.class, true)
            .toDependencyRelease();
      } catch (Exception e) {
        logger.lifecycle(
            "\tx Can't update this dependency {}, reason: {}", dependency, e.getMessage());
        return Optional.empty();
      }
    }

    @Override
    public Optional<String> getGradleWrapperFromFile() {
      try {
        Path path = Path.of("build", "demo.zip");
        RestConsumer.downloadFile(SPRING_INITIALIZER, path);
        String content =
            FileUtils.readFileFromZip(path, "gradle/wrapper/gradle-wrapper.properties");
        int start = content.indexOf("gradle-") + 7;
        int end = content.indexOf("-bin", start);
        return Optional.of(content.substring(start, end));
      } catch (Exception e) {
        logger.lifecycle("\tx Can't download demo from spring initializer", e.getMessage());
        return Optional.empty();
      }
    }

    private String getDependencyEndpoint(DependencyRelease dependency) {
      if (dependency.valid()) {
        String endpoint =
            DEPENDENCY_RELEASES
                .replaceFirst("%group", dependency.getGroup())
                .replaceFirst("%artifact", dependency.getArtifact());
        logger.lifecycle(endpoint);
        return endpoint;
      }
      throw new IllegalArgumentException(
          dependency
              + " is not a valid dependency usage: gradle u "
              + "--dependency "
              + "group:artifact");
    }

    private String getGradlePluginEndpoint(DependencyRelease dependency) {
      if (dependency.valid()) {
        String endpoint =
            GRADLE_PLUGINS
                .replaceFirst("%group", dependency.getGroup().replace('.', '/'))
                .replaceFirst("%artifact", dependency.getArtifact());
        logger.lifecycle(endpoint);
        return endpoint;
      }
      logger.warn("invalid dependency {}", dependency);
      return null;
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

    @Override
    public Optional<DependencyRelease> getLatestGradlePluginVersion(DependencyRelease dependency) {
      return Optional.empty();
    }

    @Override
    public Optional<String> getGradleWrapperFromFile() {
      return Optional.empty();
    }
  }

  private interface Operations {
    Release getLatestPluginVersion();

    Optional<DependencyRelease> getTheLastDependencyRelease(DependencyRelease dependency);

    Optional<DependencyRelease> getLatestGradlePluginVersion(DependencyRelease dependency);

    Optional<String> getGradleWrapperFromFile();
  }
}
