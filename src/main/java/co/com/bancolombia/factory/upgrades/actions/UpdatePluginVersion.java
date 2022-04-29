package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.BUILD_GRADLE;
import static co.com.bancolombia.Constants.MainFiles.GRADLE_PROPERTIES;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import co.com.bancolombia.models.Release;
import co.com.bancolombia.utils.Utils;
import org.gradle.api.logging.Logger;

public class UpdatePluginVersion implements UpgradeAction {
  @Override
  public boolean up(ModuleBuilder builder) {
    Logger logger = builder.getProject().getLogger();
    logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
    Release latestRelease = builder.getLatestRelease();
    if (latestRelease == null) {
      logger.warn("Could not load latest plugin version");
    } else {
      logger.lifecycle("Latest version: {}", latestRelease.getTagName());
      return updatePlugin(latestRelease.getTagName(), logger, builder);
    }
    return false;
  }

  @Override
  public String name() {
    return "Plugin update";
  }

  @Override
  public String description() {
    return "Update the plugin to the latest version";
  }

  private boolean updatePlugin(String lastRelease, Logger logger, ModuleBuilder builder) {
    if (lastRelease.equals(Utils.getVersionPlugin())) {
      logger.lifecycle("You are already using the latest version of the plugin");
      return false;
    }
    logger.lifecycle("Updating the plugin ");

    if (builder.isKotlin()) {
      builder.updateExpression(
          "build.gradle.kts",
          "(id\\(\"co.com.bancolombia.cleanArchitecture\"\\)\\s?version\\s?).+",
          "$1\"" + lastRelease + "\"");
      return true;
    }
    builder.updateExpression(
        GRADLE_PROPERTIES, "(systemProp.version\\s?=\\s?).+", "$1" + lastRelease);
    builder.updateExpression(
        BUILD_GRADLE, "(cleanArchitectureVersion\\s?=\\s?)'.+'", "$1'" + lastRelease + "'");
    builder.updateExpression(
        BUILD_GRADLE, "(cleanArchitectureVersion\\s?=\\s?)\".+\"", "$1'" + lastRelease + "'");

    logger.lifecycle("Plugin updated");
    return true;
  }
}
