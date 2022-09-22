package co.com.bancolombia.factory.upgrades.actions;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import co.com.bancolombia.factory.upgrades.UpgradeMainGradle;
import lombok.SneakyThrows;

public class UpgradeY2021M05D20 extends UpgradeMainGradle   implements UpgradeAction {
  public static final String VALIDATION = "compileJava.dependsOn validateStructure";
  public static final String VALUE = VALIDATION + "\n\t";
  public static final String MATCH = "dependencies";

  @Override
  public boolean up(ModuleBuilder builder) {
    return super.up(builder, MATCH, VALIDATION, VALUE);
  }

  @Override
  public String name() {
    return "1.9.3->1.9.4";
  }

  @Override
  public String description() {
    return "Append validate structure task dependency";
  }
}
