package co.com.bancolombia.factory.upgrades.samples;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;

public class AnUpdateNotApplied implements UpgradeAction {
  @Override
  public boolean up(ModuleBuilder builder) {
    return false;
  }

  @Override
  public String name() {
    return "0.0.1->0.0.2";
  }

  @Override
  public String description() {
    return "Sample update";
  }
}
