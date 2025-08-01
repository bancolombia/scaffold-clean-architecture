package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.MAIN_GRADLE;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpdateUtils;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import lombok.SneakyThrows;

public class UpgradeY2024M11D16Gradle implements UpgradeAction {
  private static final String JAVA_BLOCK_CONFIG =
      "java {\n        {{sourceCompatibilityLoaded}}\n    }";

  @Override
  @SneakyThrows
  public boolean up(ModuleBuilder builder) {
    return builder.updateFile(
        MAIN_GRADLE,
        content -> {
          if (!builder.findExpressions(MAIN_GRADLE, "java\\s*\\{").isEmpty()) {
            return content;
          }

          String sourceCompatibilityLoaded =
              builder
                  .findExpressions(
                      MAIN_GRADLE, "sourceCompatibility = JavaVersion.VERSION_(\\d{2})")
                  .stream()
                  .findFirst()
                  .orElse(null);

          if (sourceCompatibilityLoaded == null) {
            sourceCompatibilityLoaded = "sourceCompatibility = JavaVersion.{{javaVersion}}";
          }

          return UpdateUtils.replace(
              content,
              sourceCompatibilityLoaded,
              JAVA_BLOCK_CONFIG.replace(
                  "{{sourceCompatibilityLoaded}}", sourceCompatibilityLoaded));
        });
  }

  @Override
  public String name() {
    return "3.18.2->3.19.0";
  }

  @Override
  public String description() {
    return "Add java { } configuration block, backed by JavaPluginExtension";
  }
}
