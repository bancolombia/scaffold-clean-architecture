package co.com.bancolombia.utils.operations;

import co.com.bancolombia.models.DependencyRelease;
import co.com.bancolombia.models.DependencyReleaseXml;
import co.com.bancolombia.models.Release;
import co.com.bancolombia.utils.FileUtils;
import co.com.bancolombia.utils.operations.http.RestConsumer;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

public class HttpOperations implements ExternalOperations {
  public static final String PLUGIN_RELEASES =
      "https://api.github.com/repos/bancolombia/scaffold-clean-architecture/releases";
  public static final String DEPENDENCY_RELEASES =
      "https://search.maven.org/solrsearch/select?q=g:%22%group%22+AND+a:%22%artifact%22&core=gav&rows=5&wt=json";
  public static final String GRADLE_PLUGINS =
      "https://plugins.gradle.org/m2/%group/%artifact/maven-metadata.xml";
  public static final String SPRING_INITIALIZER = "https://start.spring.io/starter.zip";
  public static final String GRADLE_WRAPPER_PROPERTIES = "gradle/wrapper/gradle-wrapper.properties";
  private final Map<String, String> endpoints;

  public HttpOperations() {
    this.endpoints =
        Map.of(
            PLUGIN_RELEASES,
            PLUGIN_RELEASES,
            DEPENDENCY_RELEASES,
            DEPENDENCY_RELEASES,
            GRADLE_PLUGINS,
            GRADLE_PLUGINS,
            SPRING_INITIALIZER,
            SPRING_INITIALIZER);
  }

  // To enable test mocks injection
  public HttpOperations(Map<String, String> endpoints) {
    this.endpoints = endpoints;
  }

  private final Logger logger = Logging.getLogger(HttpOperations.class);

  @Override
  public Release getLatestPluginVersion() {
    try {
      return Arrays.stream(RestConsumer.getRequest(resolve(PLUGIN_RELEASES), Release[].class))
          .filter(HttpOperations::filterValidVersions)
          .findFirst()
          .map(Release::cleanTagName)
          .orElse(null);
    } catch (Exception e) {
      logger.lifecycle("Can't check the latest version of the plugin, reason: {}", e.getMessage());
      return null;
    }
  }

  private static boolean filterValidVersions(Release release) {
    return Stream.of("alpha", "beta").noneMatch(invalid -> release.getTagName().contains(invalid));
  }

  @Override
  public Optional<DependencyRelease> getTheLastDependencyRelease(DependencyRelease dependency) {
    try {
      DependencyRelease release =
          RestConsumer.getRequest(getDependencyEndpoint(dependency), DependencyRelease.class);
      return release.getVersion() != null ? Optional.of(release) : Optional.empty();
    } catch (Exception e) {
      logger.lifecycle("Can't update this dependency {}, reason: {}", dependency, e.getMessage());
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
    Path path = Path.of("build", "demo.zip");
    try {
      RestConsumer.downloadFile(resolve(SPRING_INITIALIZER), path);
      String content = FileUtils.readFileFromZip(path, GRADLE_WRAPPER_PROPERTIES);
      int start = content.indexOf("gradle-") + 7;
      int end = content.indexOf("-bin", start);
      return Optional.of(content.substring(start, end));
    } catch (Exception e) {
      logger.lifecycle(
          "Can't resolver version of gradle-wrapper.properties from zip", e.getMessage());
      return Optional.empty();
    }
  }

  private String getDependencyEndpoint(DependencyRelease dependency) {
    if (dependency.valid()) {
      String endpoint =
          resolve(DEPENDENCY_RELEASES)
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
          resolve(GRADLE_PLUGINS)
              .replaceFirst("%group", dependency.getGroup().replace('.', '/'))
              .replaceFirst("%artifact", dependency.getArtifact());
      logger.lifecycle(endpoint);
      return endpoint;
    }
    logger.warn("invalid dependency {}", dependency);
    return null;
  }

  private String resolve(String endpoint) {
    return endpoints.get(endpoint);
  }
}
