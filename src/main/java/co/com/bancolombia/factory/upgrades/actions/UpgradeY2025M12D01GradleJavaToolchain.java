package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.GRADLE_PROPERTIES;
import static co.com.bancolombia.Constants.MainFiles.MAIN_GRADLE;

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
            })
        | builder.updateFile(
            MAIN_GRADLE,
            content -> {
              if (builder
                  .findExpressions(MAIN_GRADLE, "java\\s*\\{[^}]*toolchain\\s*\\{")
                  .isEmpty()) {
                String javaVersionMatch =
                    builder
                        .findExpressions(
                            MAIN_GRADLE, "sourceCompatibility = JavaVersion\\.VERSION_(\\d{2})")
                        .stream()
                        .findFirst()
                        .orElse(null);

                if (javaVersionMatch != null) {
                  String versionNumber = javaVersionMatch.replaceAll(".*VERSION_(\\d{2}).*", "$1");
                  String newJavaBlock =
                      "java {\n        toolchain {\n            languageVersion = JavaLanguageVersion.of("
                          + versionNumber
                          + ")\n        }\n    }";
                  content =
                      content.replaceAll(
                          "java\\s*\\{[^}]*sourceCompatibility\\s*=\\s*JavaVersion\\.VERSION_\\d{2}[^}]*}",
                          newJavaBlock);
                }
              }

              // Add withHistory = true to pitest block if not already present
              if (!builder.findExpressions(MAIN_GRADLE, "pitest\\s*\\{").isEmpty()) {
                content =
                    UpdateUtils.insertAfterMatch(
                        content,
                        "useClasspathFile = true",
                        "withHistory",
                        "\n        withHistory = true");
              }

              return content;
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
