package co.com.bancolombia.utils.operations;

import static co.com.bancolombia.Constants.PLUGIN_ID;

import co.com.bancolombia.factory.upgrades.UpdateUtils;
import co.com.bancolombia.models.DependencyRelease;
import co.com.bancolombia.models.GradleVersion;
import co.com.bancolombia.models.MavenMetadata;
import co.com.bancolombia.models.Release;
import co.com.bancolombia.utils.Utils;
import co.com.bancolombia.utils.operations.http.HttpStatusException;
import co.com.bancolombia.utils.operations.http.RestConsumer;
import java.io.IOException;
import java.util.Map;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

public class HttpOperations implements ExternalOperations {
  public static final String DEPENDENCY_RELEASES =
      "https://repo1.maven.org/maven2/%group/%artifact/maven-metadata.xml";
  public static final String GRADLE_PLUGINS =
      "https://plugins.gradle.org/m2/%group/%artifact/maven-metadata.xml";
  public static final String GRADLE_VERSIONS_API = "https://services.gradle.org/versions/current";
  public static final String GRADLE_DISTRIBUTION_SHA256 =
      "https://services.gradle.org/distributions/gradle-%version-%type.zip.sha256";
  private static final Pattern SHA256_PATTERN = Pattern.compile("[0-9a-f]{64}");
  private static final Pattern SAFE_VERSION = Pattern.compile("[\\w.+-]+");
  private final Map<String, String> endpoints;

  public HttpOperations() {
    this.endpoints =
        Map.of(
            DEPENDENCY_RELEASES,
            DEPENDENCY_RELEASES,
            GRADLE_PLUGINS,
            GRADLE_PLUGINS,
            GRADLE_VERSIONS_API,
            GRADLE_VERSIONS_API,
            GRADLE_DISTRIBUTION_SHA256,
            GRADLE_DISTRIBUTION_SHA256);
  }

  // To enable test mocks injection
  public HttpOperations(Map<String, String> endpoints) {
    this.endpoints = endpoints;
  }

  private final Logger logger = Logging.getLogger(HttpOperations.class);

  @Override
  public LookupResult<Release> findLatestPluginVersion() {
    return guarded(GRADLE_PLUGINS, this::latestPluginVersion);
  }

  private LookupResult<Release> latestPluginVersion() {
    String version = Utils.getVersionPlugin();
    OptionalInt major = Utils.majorVersionOf(version);
    if (major.isEmpty()) {
      // Local builds use a placeholder version, there is no release line to compare with
      return LookupResult.notFound("releases of the plugin version " + version);
    }
    var plugin = new DependencyRelease();
    plugin.setGroup(PLUGIN_ID);
    plugin.setArtifact(PLUGIN_ID + ".gradle.plugin");
    String endpoint = getGradlePluginEndpoint(plugin);
    LookupResult<MavenMetadata> metadata =
        lookup(endpoint, () -> RestConsumer.getRequest(endpoint, MavenMetadata.class, true));
    if (!(metadata instanceof LookupResult.Found<MavenMetadata> found)) {
      return cast(metadata);
    }
    // Highest stable release of the major version, compared by version and not by publication date
    return found.value().getVersioning().getVersions().stream()
        .filter(
            candidate ->
                candidate.startsWith(major.getAsInt() + ".") && Utils.isStableVersion(candidate))
        .reduce((current, next) -> UpdateUtils.isNewerVersion(current, next) ? next : current)
        .map(latest -> LookupResult.found(new Release(latest, null)))
        .orElseGet(() -> LookupResult.notFound(endpoint));
  }

  @Override
  public LookupResult<DependencyRelease> findLatestDependency(DependencyRelease dependency) {
    return guarded(
        DEPENDENCY_RELEASES,
        () ->
            findLatestInMavenFormat(dependency, DEPENDENCY_RELEASES, this::getDependencyEndpoint));
  }

  @Override
  public LookupResult<DependencyRelease> findLatestGradlePlugin(DependencyRelease dependency) {
    return guarded(
        GRADLE_PLUGINS,
        () -> findLatestInMavenFormat(dependency, GRADLE_PLUGINS, this::getGradlePluginEndpoint));
  }

  @Override
  public LookupResult<String> findLatestGradleVersion() {
    return guarded(GRADLE_VERSIONS_API, this::latestGradleVersion);
  }

  private LookupResult<String> latestGradleVersion() {
    String endpoint = resolve(GRADLE_VERSIONS_API);
    LookupResult<GradleVersion> release =
        lookup(endpoint, () -> RestConsumer.getRequest(endpoint, GradleVersion.class));
    if (!(release instanceof LookupResult.Found<GradleVersion> found)) {
      return cast(release);
    }
    String version = found.value().version();
    if (version == null || !SAFE_VERSION.matcher(version).matches()) {
      return LookupResult.failed(endpoint, "unexpected Gradle version " + version);
    }
    return LookupResult.found(version);
  }

  @Override
  public LookupResult<String> findGradleDistributionSha256(
      String version, String distributionType) {
    return guarded(
        GRADLE_DISTRIBUTION_SHA256, () -> gradleDistributionSha256(version, distributionType));
  }

  private LookupResult<String> gradleDistributionSha256(String version, String distributionType) {
    String endpoint =
        resolve(GRADLE_DISTRIBUTION_SHA256)
            .replace("%version", version)
            .replace("%type", distributionType);
    LookupResult<String> checksum = lookup(endpoint, () -> RestConsumer.getTextRequest(endpoint));
    if (!(checksum instanceof LookupResult.Found<String> found)) {
      return checksum;
    }
    String value = found.value().trim();
    if (!SHA256_PATTERN.matcher(value).matches()) {
      return LookupResult.failed(endpoint, "invalid checksum");
    }
    return LookupResult.found(value);
  }

  private LookupResult<DependencyRelease> findLatestInMavenFormat(
      DependencyRelease dependency,
      String endpointKey,
      Function<DependencyRelease, String> endpointResolver) {
    String endpoint;
    try {
      endpoint = endpointResolver.apply(dependency);
    } catch (IllegalArgumentException e) {
      return LookupResult.failed(source(endpointKey), e.getMessage());
    }
    LookupResult<MavenMetadata> metadata =
        lookup(endpoint, () -> RestConsumer.getRequest(endpoint, MavenMetadata.class, true));
    if (!(metadata instanceof LookupResult.Found<MavenMetadata> found)) {
      return cast(metadata);
    }
    try {
      var release = found.value().toDependencyRelease();
      release.setGradlePlugin(dependency.isGradlePlugin());
      if (!isSafeRelease(dependency, release)) {
        return LookupResult.failed(endpoint, "unexpected release " + release);
      }
      return LookupResult.found(release);
    } catch (RuntimeException e) {
      return LookupResult.failed(endpoint, "invalid metadata: " + e.getMessage());
    }
  }

  /**
   * A lookup never throws: an unexpected error, such as incomplete metadata or a missing endpoint,
   * is a failure of the lookup and not of the task that asked for it.
   */
  private <T> LookupResult<T> guarded(String endpointKey, Supplier<LookupResult<T>> lookup) {
    try {
      return lookup.get();
    } catch (RuntimeException e) {
      logger.debug("Lookup in {} failed", source(endpointKey), e);
      return LookupResult.failed(source(endpointKey), describe(e));
    }
  }

  /** Where a lookup goes, to name it in a failure: the configured endpoint or its default. */
  private String source(String endpointKey) {
    String endpoint = resolve(endpointKey);
    return endpoint == null ? endpointKey : endpoint;
  }

  /** Runs a request: 404 means that the source does not have it, any other error is a failure. */
  private <T> LookupResult<T> lookup(String endpoint, Request<T> request) {
    try {
      T value = request.execute();
      return value == null ? LookupResult.notFound(endpoint) : LookupResult.found(value);
    } catch (HttpStatusException e) {
      return e.isNotFound()
          ? LookupResult.notFound(endpoint)
          : LookupResult.failed(endpoint, e.getMessage());
    } catch (Exception e) {
      logger.debug("Lookup in {} failed", endpoint, e);
      return LookupResult.failed(endpoint, describe(e));
    }
  }

  private static String describe(Exception e) {
    return e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
  }

  /** NotFound and Failed carry no value, so they keep their meaning for another value type. */
  private static <T> LookupResult<T> cast(LookupResult<?> result) {
    if (result instanceof LookupResult.NotFound<?> notFound) {
      return LookupResult.notFound(notFound.source());
    }
    if (result instanceof LookupResult.Failed<?> failed) {
      return LookupResult.failed(failed.source(), failed.reason());
    }
    throw new IllegalArgumentException("Found results have a value");
  }

  @FunctionalInterface
  private interface Request<T> {
    T execute() throws IOException;
  }

  /**
   * The release is written into build files, so it must be the requested artifact and its version
   * must be a plain version string.
   */
  private static boolean isSafeRelease(DependencyRelease requested, DependencyRelease release) {
    return release.getVersion() != null
        && SAFE_VERSION.matcher(release.getVersion()).matches()
        && requested.getGroup().equals(release.getGroup())
        && requested.getArtifact().equals(release.getArtifact());
  }

  private String getDependencyEndpoint(DependencyRelease dependency) {
    if (dependency.valid()) {
      var group = String.join("/", dependency.getGroup().split("\\."));
      var artifact = dependency.getArtifact();
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
    throw new IllegalArgumentException(dependency + " is not a valid Gradle plugin");
  }

  private String resolve(String endpoint) {
    return endpoints.get(endpoint);
  }
}
