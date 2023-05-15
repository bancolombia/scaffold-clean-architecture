package co.com.bancolombia.factory.upgrades.actions;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import co.com.bancolombia.factory.upgrades.UpgradeMainGradle;
import lombok.SneakyThrows;

public class UpgradeY2022M08D10 extends UpgradeMainGradle implements UpgradeAction {

  public static final String VALIDATION = "useJUnitPlatform";

  public static final String JUNIT_PLATFORM_VALUER =
      "\n\ttest {\n" + "        useJUnitPlatform()\n" + "    }\n\n\t";

  public static final String MATCH = "dependencies";

  @Override
  @SneakyThrows
  public boolean up(ModuleBuilder builder) {
    return super.up(builder, MATCH, VALIDATION, JUNIT_PLATFORM_VALUER);
  }

  @Override
  public String name() {
    return "2.4.4->2.4.5";
  }

  @Override
  public String description() {
    return "Append useJUnitPlatform in main.gradle file";
  }
}
