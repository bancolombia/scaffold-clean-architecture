package co.com.bancolombia.factory.upgrades.actions;

import co.com.bancolombia.Constants;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import lombok.SneakyThrows;

public class UpgradeY2023M10D02JacocoMerged implements UpgradeAction {
  public static final String BEFORE = "dependsOn = subprojects.jacocoTestReport";
  public static final String AFTER = "dependsOn = [test, subprojects.jacocoTestReport]";

  @Override
  @SneakyThrows
  public boolean up(ModuleBuilder builder) {
    return builder.updateExpression(Constants.MainFiles.MAIN_GRADLE, BEFORE, AFTER);
  }

  @Override
  public String name() {
    return "3.6.1->3.6.2";
  }

  @Override
  public String description() {
    return "Add jacocoMergedReport dependency on test";
  }
}
