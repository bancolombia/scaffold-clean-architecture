package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.BUILD_GRADLE;

import co.com.bancolombia.Constants;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import org.gradle.api.logging.Logger;

public class ZUpdateSpringVersion implements UpgradeAction {
  @Override
  public boolean up(ModuleBuilder builder) {
    Logger logger = builder.getProject().getLogger();
    builder.updateExpression(
        BUILD_GRADLE,
        "(springBootVersion\\s?=\\s?)'.+'",
        "$1'" + Constants.SPRING_BOOT_VERSION + "'");
    builder.updateExpression(
        BUILD_GRADLE,
        "(springBootVersion\\s?=\\s?)\".+\"",
        "$1'" + Constants.SPRING_BOOT_VERSION + "'");
    logger.lifecycle("Spring version assigned to {}", Constants.SPRING_BOOT_VERSION);
    return true;
  }

  @Override
  public String name() {
    return "Spring update";
  }

  @Override
  public String description() {
    return "Update spring to the latest predefined version";
  }
}
