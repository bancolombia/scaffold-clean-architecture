package co.com.bancolombia.utils.operations;

import co.com.bancolombia.models.DependencyRelease;
import co.com.bancolombia.models.Release;

/**
 * Lookups in external sources. They only query: deciding whether a result is newer than the current
 * version is up to the caller.
 */
public interface ExternalOperations {

  LookupResult<Release> findLatestPluginVersion();

  LookupResult<DependencyRelease> findLatestDependency(DependencyRelease dependency);

  LookupResult<DependencyRelease> findLatestGradlePlugin(DependencyRelease dependency);

  LookupResult<String> findLatestGradleVersion();

  LookupResult<String> findGradleDistributionSha256(String version, String distributionType);
}
