package co.com.bancolombia.factory.upgrades;

import co.com.bancolombia.factory.ModuleBuilder;

public interface UpgradeAction {
  boolean up(ModuleBuilder builder);

  String name();

  String description();
}
