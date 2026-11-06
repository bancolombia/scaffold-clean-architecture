package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.GRADLE_PROPERTIES;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import java.io.IOException;
import java.util.regex.Pattern;

/** Explicitly enables org.gradle.tooling.parallel in gradle.properties. */
public class UpgradeY2026M11D05GradleToolingParallel implements UpgradeAction {
  private static final String PROPERTY = "org.gradle.tooling.parallel";
  private static final String LINE = PROPERTY + "=true";
  private static final Pattern PARALLEL = Pattern.compile("(?m)^(org\\.gradle\\.parallel\\s*=.*)$");

  @Override
  public boolean up(ModuleBuilder builder) {
    try {
      return builder.updateFile(GRADLE_PROPERTIES, this::addProperty);
    } catch (IOException e) {
      builder.getLogger().debug("gradle.properties not found, skipping: {}", e.getMessage());
      return false;
    }
  }

  private String addProperty(String content) {
    if (content.contains(PROPERTY)) {
      return content;
    }
    if (PARALLEL.matcher(content).find()) {
      return PARALLEL.matcher(content).replaceFirst("$1\n" + LINE);
    }
    return content + (content.endsWith("\n") ? "" : "\n") + LINE + "\n";
  }

  @Override
  public String name() {
    return "Gradle tooling parallel";
  }

  @Override
  public String description() {
    return "Add org.gradle.tooling.parallel=true to gradle.properties";
  }
}
