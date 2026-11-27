package co.com.bancolombia.utils.operations;

import co.com.bancolombia.Constants;
import co.com.bancolombia.models.DependencyRelease;
import co.com.bancolombia.models.Release;
import co.com.bancolombia.utils.Utils;

/**
 * Used with simulateRest: nothing is queried. Dependencies are reported at their current version
 * and Gradle at the version that the plugin generates, with its checksum, so a simulated run only
 * raises an older wrapper to it, and the plugin is the latest release.
 */
public class MockOperations implements ExternalOperations {
  static final String SOURCE = "simulateRest";

  @Override
  public LookupResult<Release> findLatestPluginVersion() {
    return LookupResult.found(new Release(Utils.getVersionPlugin(), null));
  }

  @Override
  public LookupResult<DependencyRelease> findLatestDependency(DependencyRelease dependency) {
    return LookupResult.found(dependency);
  }

  @Override
  public LookupResult<DependencyRelease> findLatestGradlePlugin(DependencyRelease dependency) {
    return LookupResult.found(dependency);
  }

  @Override
  public LookupResult<String> findLatestGradleVersion() {
    return LookupResult.found(Constants.GRADLE_WRAPPER_VERSION);
  }

  @Override
  public LookupResult<String> findGradleDistributionSha256(
      String version, String distributionType) {
    if (Constants.GRADLE_WRAPPER_VERSION.equals(version) && "bin".equals(distributionType)) {
      return LookupResult.found(Constants.GRADLE_WRAPPER_SHA256);
    }
    return LookupResult.notFound(SOURCE);
  }
}
