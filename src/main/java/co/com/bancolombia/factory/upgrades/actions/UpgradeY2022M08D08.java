package co.com.bancolombia.factory.upgrades.actions;

import co.com.bancolombia.Constants;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import lombok.SneakyThrows;

public class UpgradeY2022M08D08 implements UpgradeAction {

  @Override
  @SneakyThrows
  public boolean up(ModuleBuilder builder) {
    return builder.updateExpression(
            Constants.MainFiles.MAIN_GRADLE, "reportsDir\\s?=", "reportsDirectory =")
        | builder.updateExpression(
            Constants.MainFiles.MAIN_GRADLE, "(xml|csv|html)\\.enabled", "$1.setRequired")
        | builder.updateExpression(
            Constants.MainFiles.MAIN_GRADLE,
            "(xml|csv|html)\\.destination",
            "$1.setOutputLocation");
  }

  @Override
  public String name() {
    return "2.4.3->2.4.4";
  }

  @Override
  public String description() {
    return "Upgrade jacoco reports config compatible with gradle 8";
  }
}
