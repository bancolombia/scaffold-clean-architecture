package co.com.bancolombia.factory.upgrades.actions;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeMainGradle;
import lombok.SneakyThrows;

public class UpgradeY2022M08D10 extends UpgradeMainGradle {

  public static final String VALIDATION = "useJUnitPlatform";

  public static final String JUNIT_PLATFORM_VALUER =
      "\n    test {\n" + "        useJUnitPlatform()\n" + "    }\n\n";

  public static final String MATCH = "\tdependencies";

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
