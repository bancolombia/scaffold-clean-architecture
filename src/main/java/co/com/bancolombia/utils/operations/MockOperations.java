package co.com.bancolombia.utils.operations;

import co.com.bancolombia.models.DependencyRelease;
import co.com.bancolombia.models.Release;
import java.util.Optional;

public class MockOperations implements ExternalOperations {

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
