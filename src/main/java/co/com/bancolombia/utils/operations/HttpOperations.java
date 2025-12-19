package co.com.bancolombia.utils.operations;

import co.com.bancolombia.models.DependencyRelease;
import co.com.bancolombia.models.DependencyReleaseXml;
import co.com.bancolombia.models.MavenMetadata;
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
      "https://repo1.maven.org/maven2/%group/%artifact/maven-metadata.xml";
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
    return Stream.of("alpha", "beta", "-M", "-RC")
        .noneMatch(invalid -> release.getTagName().contains(invalid));
  }

  @Override
  public Optional<DependencyRelease> getTheLastDependencyRelease(DependencyRelease dependency) {
    var endpoint = "";
    try {
      endpoint = getDependencyEndpoint(dependency);
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

  @Override
  public Optional<DependencyRelease> getLatestGradlePluginVersion(DependencyRelease dependency) {
    String endpoint = "";
    try {
      endpoint = getGradlePluginEndpoint(dependency);
      DependencyRelease release =
          RestConsumer.getRequest(endpoint, DependencyReleaseXml.class, true)
              .toDependencyRelease()
              .orElse(dependency);
      if (release.isNewest(dependency)) {
        logger.lifecycle("Updating {} to {}", dependency.toString(), release.toString());
        return Optional.of(release);
      }
      return Optional.empty();
    } catch (Exception e) {
      logger.lifecycle(
          "\tx Can't update this dependency {} from {}, reason: {}",
          dependency,
          endpoint,
          e.getMessage());
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
