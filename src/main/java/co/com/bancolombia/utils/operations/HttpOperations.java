package co.com.bancolombia.utils.operations;

import co.com.bancolombia.factory.upgrades.UpdateUtils;
import co.com.bancolombia.models.DependencyRelease;
import co.com.bancolombia.models.GradleVersion;
import co.com.bancolombia.models.MavenMetadata;
import co.com.bancolombia.models.Release;
import co.com.bancolombia.utils.Utils;
import co.com.bancolombia.utils.operations.http.RestConsumer;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

public class HttpOperations implements ExternalOperations {
  private static final String PLUGIN_ID = "co.com.bancolombia.cleanArchitecture";
  public static final String DEPENDENCY_RELEASES =
      "https://repo1.maven.org/maven2/%group/%artifact/maven-metadata.xml";
  public static final String GRADLE_PLUGINS =
      "https://plugins.gradle.org/m2/%group/%artifact/maven-metadata.xml";
  public static final String GRADLE_VERSIONS_API = "https://services.gradle.org/versions/current";
  private final Map<String, String> endpoints;

  public HttpOperations() {
    this.endpoints =
        Map.of(
            DEPENDENCY_RELEASES,
            DEPENDENCY_RELEASES,
            GRADLE_PLUGINS,
            GRADLE_PLUGINS,
            GRADLE_VERSIONS_API,
            GRADLE_VERSIONS_API);
  }

  // To enable test mocks injection
  public HttpOperations(Map<String, String> endpoints) {
    this.endpoints = endpoints;
  }

  private final Logger logger = Logging.getLogger(HttpOperations.class);

  @Override
  public Release getLatestPluginVersion() {
    return getLatestPluginVersion(installedMajor()).orElse(null);
  }

  private static int installedMajor() {
    return Integer.parseInt(Utils.getVersionPlugin().split("\\.")[0]);
  }

  /** Highest stable release of a major version, compared by version and not by publication date. */
  private Optional<Release> getLatestPluginVersion(int major) {
    try {
      var plugin = new DependencyRelease();
      plugin.setGroup(PLUGIN_ID);
      plugin.setArtifact(PLUGIN_ID + ".gradle.plugin");
      var endpoint = getGradlePluginEndpoint(plugin);
      return RestConsumer.getRequest(endpoint, MavenMetadata.class, true)
          .getVersioning()
          .getVersions()
          .stream()
          .filter(version -> version.startsWith(major + ".") && Utils.isStableVersion(version))
          .reduce((current, next) -> UpdateUtils.isNewerVersion(current, next) ? next : current)
          .map(version -> new Release(version, null));
    } catch (Exception e) {
      logger.lifecycle(
          "Can't check the latest {}.x.x version of the plugin, reason: {}", major, e.getMessage());
      return Optional.empty();
    }
  }

  @Override
  public Optional<DependencyRelease> getTheLastDependencyRelease(DependencyRelease dependency) {
    return getTheLastDependencyReleaseFromMavenFormat(dependency, this::getDependencyEndpoint);
  }

  @Override
  public Optional<DependencyRelease> getLatestGradlePluginVersion(DependencyRelease dependency) {
    return getTheLastDependencyReleaseFromMavenFormat(dependency, this::getGradlePluginEndpoint);
  }

  @Override
  public Optional<String> getGradleWrapperFromFile() {
    try {
      GradleVersion release =
          RestConsumer.getRequest(resolve(GRADLE_VERSIONS_API), GradleVersion.class);
      return Optional.ofNullable(release).map(GradleVersion::version);
    } catch (Exception e) {
      logger.lifecycle("Can't resolve Gradle version from API, reason: {}", e.getMessage());
      return Optional.empty();
    }
  }

  private Optional<DependencyRelease> getTheLastDependencyReleaseFromMavenFormat(
      DependencyRelease dependency, Function<DependencyRelease, String> endpointResolver) {
    var endpoint = "";
    try {
      endpoint = endpointResolver.apply(dependency);
      var metadata = RestConsumer.getRequest(endpoint, MavenMetadata.class, true);
      var release = metadata.toDependencyRelease();
      if (release.isNewest(dependency)) {
        logger.lifecycle("Updating {} to {}", dependency.toString(), release.toString());
        return Optional.of(release);
      }
      return Optional.empty();
    } catch (Exception e) {
      logger.lifecycle(
          "Can't update this dependency {} from {}, reason: {}",
          dependency,
          endpoint,
          e.getMessage());
      return Optional.empty();
    }
  }

  private String getDependencyEndpoint(DependencyRelease dependency) {
    if (dependency.valid()) {
      var group = String.join("/", dependency.getGroup().split("\\."));
      var artifact = String.join("/", dependency.getArtifact().split("\\."));
      return resolve(DEPENDENCY_RELEASES)
          .replaceFirst("%group", group)
          .replaceFirst("%artifact", artifact);
    }
    throw new IllegalArgumentException(
        dependency
            + " is not a valid dependency usage: gradle u "
            + "--dependency "
            + "group:artifact");
  }

  private String getGradlePluginEndpoint(DependencyRelease dependency) {
    if (dependency.valid()) {
      return resolve(GRADLE_PLUGINS)
          .replaceFirst("%group", dependency.getGroup().replace('.', '/'))
          .replaceFirst("%artifact", dependency.getArtifact());
    }
    logger.warn("invalid dependency {}", dependency);
    return null;
  }

  private String resolve(String endpoint) {
    return endpoints.get(endpoint);
  }
}
