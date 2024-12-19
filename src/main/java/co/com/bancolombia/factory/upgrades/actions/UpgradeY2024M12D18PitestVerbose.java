package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.MAIN_GRADLE;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpdateUtils;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import lombok.SneakyThrows;

public class UpgradeY2024M12D18PitestVerbose implements UpgradeAction {

  @Override
  @SneakyThrows
  public boolean up(ModuleBuilder builder) {
    return builder.updateFile(
        MAIN_GRADLE, content -> UpdateUtils.replace(content, "verbose = true", "verbose = false"));
  }

  @Override
  public String name() {
    return "3.20.5->3.20.6";
  }

  @Override
  public String description() {
    return "Set verbose to false in pitest";
  }
}
