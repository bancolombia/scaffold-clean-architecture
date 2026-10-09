package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.GRADLE_PROPERTIES;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpdateUtils;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import lombok.SneakyThrows;

public class UpgradeY2025M12D01GradleJavaToolchain implements UpgradeAction {
  private static final String CONFIGURATION_CACHE_INTEGRITY =
      "org.gradle.configuration-cache.integrity-check=false";
  private static final String CONFIGURATION_CACHE_READ_ONLY =
      "org.gradle.configuration-cache.read-only=false";

  @Override
  @SneakyThrows
  public boolean up(ModuleBuilder builder) {
    return builder.updateFile(
        GRADLE_PROPERTIES,
        content -> {
          content =
              UpdateUtils.replace(
                  content,
                  "org.gradle.configuration-cache.integrity-check=true",
                  CONFIGURATION_CACHE_INTEGRITY);
          return UpdateUtils.insertAfterMatch(
              content,
              CONFIGURATION_CACHE_INTEGRITY,
              "org.gradle.configuration-cache.read-only",
              "\n".concat(CONFIGURATION_CACHE_READ_ONLY));
        });
  }

  @Override
  public String name() {
    return "3.28.0->4.0.0";
  }

  @Override
  public String description() {
    return "Add pitest withHistory property and configure Java toolchain";
  }
}
