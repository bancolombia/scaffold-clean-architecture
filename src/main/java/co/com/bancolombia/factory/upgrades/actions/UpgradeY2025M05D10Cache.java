package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.GRADLE_PROPERTIES;
import static co.com.bancolombia.Constants.MainFiles.SETTINGS_GRADLE;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpdateUtils;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import lombok.SneakyThrows;

public class UpgradeY2025M05D10Cache implements UpgradeAction {
  private static final String BUILD_CACHE = "buildCache";
  private static final String MATCH_CACHE = "rootProject.name";
  private static final String APPEND_CACHE =
      "buildCache {\n"
          + "    local {\n"
          + "        directory = new File(rootDir, 'build-cache')\n"
          + "    }\n"
          + "}\n\n";

  private static final String PARALLEL_CHECK = "org.gradle.parallel=true";
  private static final String CACHING = "org.gradle.caching=true";
  private static final String CACHING_REGEX = "\\borg\\.gradle\\.caching=false\\b";
  private static final String CONFIGURATION_CACHE = "org.gradle.configuration-cache=true";
  private static final String CONFIGURATION_CACHE_REGEX =
      "\\borg\\.gradle\\.configuration-cache=false\\b";
  private static final String CONFIGURATION_CACHE_PARALLEL =
      "org.gradle.configuration-cache.parallel=true";
  private static final String CONFIGURATION_CACHE_PARALLEL_REGEX =
      "\\borg\\.gradle\\.configuration-cache.parallel=false\\b";
  private static final String CONFIGURATION_CACHE_INTEGRITY =
      "org.gradle.configuration-cache.integrity-check=true";
  private static final String CONFIGURATION_CACHE_INTEGRITY_REGEX =
      "\\borg\\.gradle\\.configuration-cache.integrity-check=false\\b";

  @Override
  @SneakyThrows
  public boolean up(ModuleBuilder builder) {
    return builder.updateFile(
            SETTINGS_GRADLE,
            content ->
                UpdateUtils.insertBeforeMatch(content, MATCH_CACHE, BUILD_CACHE, APPEND_CACHE))
        | builder.updateFile(
            GRADLE_PROPERTIES,
            content -> {
              String modifiedContent = content;

              modifiedContent =
                  updateGradleProperties(
                      builder,
                      modifiedContent,
                      CONFIGURATION_CACHE_INTEGRITY_REGEX,
                      "org.gradle.configuration-cache.integrity-check=false",
                      "org.gradle.configuration-cache.integrity-check=",
                      CONFIGURATION_CACHE_INTEGRITY);

              modifiedContent =
                  updateGradleProperties(
                      builder,
                      modifiedContent,
                      CONFIGURATION_CACHE_PARALLEL_REGEX,
                      "org.gradle.configuration-cache.parallel=false",
                      "org.gradle.configuration-cache.parallel=",
                      CONFIGURATION_CACHE_PARALLEL);

              modifiedContent =
                  updateGradleProperties(
                      builder,
                      modifiedContent,
                      CONFIGURATION_CACHE_REGEX,
                      "org.gradle.configuration-cache=false",
                      "org.gradle.configuration-cache=",
                      CONFIGURATION_CACHE);
              modifiedContent =
                  updateGradleProperties(
                      builder,
                      modifiedContent,
                      CACHING_REGEX,
                      "org.gradle.caching=false",
                      "org.gradle.caching=",
                      CACHING);

              return modifiedContent;
            })
        | UpdateUtils.appendIfNotContains(builder, "./.gitignore", "build-cache", "\nbuild-cache");
  }

  private String updateGradleProperties(
      ModuleBuilder builder,
      String content,
      String regex,
      String previous,
      String containsValue,
      String newValue) {
    return !builder.findExpressions(GRADLE_PROPERTIES, regex).isEmpty()
        ? UpdateUtils.replace(content, previous, newValue)
        : UpdateUtils.insertAfterMatch(
            content, PARALLEL_CHECK, containsValue, "\n".concat(newValue));
  }

  @Override
  public String name() {
    return "3.22.4->3.22.5";
  }

  @Override
  public String description() {
    return "Add block hound validations";
  }
}
