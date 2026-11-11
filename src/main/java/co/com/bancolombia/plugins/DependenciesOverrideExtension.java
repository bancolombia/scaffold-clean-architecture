package co.com.bancolombia.plugins;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.gradle.api.artifacts.DependencyResolveDetails;

public class DependenciesOverrideExtension {
  private static final String JACKSON_3_GROUP = "tools.jackson.core";
  private static final String JACKSON_2_GROUP = "com.fasterxml.jackson.core";
  private static final String JACKSON_ANNOTATIONS = "jackson-annotations";
  private static final Pattern JACKSON_3_VERSION = Pattern.compile("^3\\.(\\d+)(\\..*)?$");
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
    if (version == null && isJacksonAnnotations(group, details.getRequested().getName())) {
      version = jacksonAnnotationsVersion();
    }
    if (version != null) {
      details.useVersion(version);
    }
  }

  private static boolean isJacksonAnnotations(String group, String name) {
    return JACKSON_2_GROUP.equals(group) && JACKSON_ANNOTATIONS.equals(name);
  }

  /**
   * Jackson 3.x requires jackson-annotations 2.(20 + minor), but the Spring Boot BOM pins an older
   * one. When Jackson 3 is overridden and jackson-annotations is not, the latter is aligned so the
   * jackson-databind classes can be loaded.
   */
  private String jacksonAnnotationsVersion() {
    for (String name : new String[] {null, "jackson-databind", "jackson-core"}) {
      String jackson3 = overrides.get(new DependencyKey(JACKSON_3_GROUP, name));
      Matcher matcher = jackson3 == null ? null : JACKSON_3_VERSION.matcher(jackson3);
      if (matcher != null && matcher.matches()) {
        return "2." + (20 + Integer.parseInt(matcher.group(1)));
      }
    }
    return null;
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
