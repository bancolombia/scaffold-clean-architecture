package co.com.bancolombia.plugins;

import java.util.LinkedHashMap;
import java.util.Map;
import org.gradle.api.artifacts.DependencyResolveDetails;

public class DependenciesOverrideExtension {
  private final Map<DependencyKey, String> overrides = new LinkedHashMap<>();

  /** Registers a dependency version override using group:name:version or group:version notation. */
  public void ensure(String dependencyNotation) {
    if (dependencyNotation == null) {
      throw invalidNotation();
    }

    String[] coordinates = dependencyNotation.strip().split(":", -1);
    if (coordinates.length == 2) {
      putOverride(coordinates[0], null, coordinates[1]);
      return;
    }
    if (coordinates.length == 3) {
      putOverride(coordinates[0], coordinates[1], coordinates[2]);
      return;
    }
    throw invalidNotation();
  }

  void applyTo(DependencyResolveDetails details) {
    String group = details.getRequested().getGroup();
    String version = overrides.get(new DependencyKey(group, details.getRequested().getName()));
    if (version == null) {
      version = overrides.get(new DependencyKey(group, null));
    }
    if (version != null) {
      details.useVersion(version);
    }
  }

  private void putOverride(String group, String name, String version) {
    if (group.isBlank() || version.isBlank() || (name != null && name.isBlank())) {
      throw invalidNotation();
    }
    overrides.put(
        new DependencyKey(group.strip(), name == null ? null : name.strip()), version.strip());
  }

  private IllegalArgumentException invalidNotation() {
    return new IllegalArgumentException(
        "Dependency override must use group:name:version or group:version notation");
  }

  private record DependencyKey(String group, String name) {}
}
