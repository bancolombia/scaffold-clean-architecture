package co.com.bancolombia.utils.operations;

import co.com.bancolombia.models.DependencyRelease;
import co.com.bancolombia.models.Release;
import java.util.Optional;

public interface ExternalOperations {

  Release getLatestPluginVersion();

  /**
   * Highest stable release of the given major version. It compares versions, not publication dates,
   * so a newer release of another major version does not hide it.
   */
  Optional<Release> getLatestPluginVersion(int major);

  Optional<DependencyRelease> getTheLastDependencyRelease(DependencyRelease dependency);

  Optional<DependencyRelease> getLatestGradlePluginVersion(DependencyRelease dependency);

  Optional<String> getGradleWrapperFromFile();
}
