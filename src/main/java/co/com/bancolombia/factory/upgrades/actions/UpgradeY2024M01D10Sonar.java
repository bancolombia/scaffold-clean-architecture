package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.BUILD_GRADLE;
import static co.com.bancolombia.factory.upgrades.UpdateUtils.replace;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import lombok.SneakyThrows;

public class UpgradeY2024M01D10Sonar implements UpgradeAction {

  @Override
  @SneakyThrows
  public boolean up(ModuleBuilder builder) {
    return builder.updateFile(BUILD_GRADLE, content -> replace(content, "sonarqube {", "sonar {"));
  }

  @Override
  public String name() {
    return "3.12.1->3.12.2";
  }

  @Override
  public String description() {
    return "Renaming sonarqube task and config to sonar";
  }
}
