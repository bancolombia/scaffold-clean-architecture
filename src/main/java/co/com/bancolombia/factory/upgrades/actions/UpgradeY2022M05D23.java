package co.com.bancolombia.factory.upgrades.actions;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpdateUtils;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import lombok.SneakyThrows;

import static co.com.bancolombia.Constants.MainFiles.GRADLE_PROPERTIES;
import static co.com.bancolombia.Constants.MainFiles.MAIN_GRADLE;

public class UpgradeY2022M05D23 implements UpgradeAction {

  public static final String LANGUAGE_PROPERTY = "metrics";
  public static final String LANGUAGE_PROPERTY_VALUER = "\nmetrics=false";

  @Override
  @SneakyThrows
  public boolean up(ModuleBuilder builder) {
    return
        UpdateUtils.appendIfNotContains(
            builder, GRADLE_PROPERTIES, LANGUAGE_PROPERTY, LANGUAGE_PROPERTY_VALUER);
  }

  @Override
  public String name() {
    return "2.3.4->2.4.0";
  }

  @Override
  public String description() {
    return "Append the metrics property,and gradle wrapper";
  }
}
