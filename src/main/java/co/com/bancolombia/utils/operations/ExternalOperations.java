package co.com.bancolombia.utils.operations;

import co.com.bancolombia.models.DependencyRelease;
import co.com.bancolombia.models.Release;
import java.util.Optional;

public interface ExternalOperations {

  Release getLatestPluginVersion();

  Optional<DependencyRelease> getTheLastDependencyRelease(DependencyRelease dependency);

  Optional<DependencyRelease> getLatestGradlePluginVersion(DependencyRelease dependency);

  Optional<String> getGradleWrapperFromFile();
}
