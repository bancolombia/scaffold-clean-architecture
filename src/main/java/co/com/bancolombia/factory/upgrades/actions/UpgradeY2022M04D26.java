package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.GRADLE_PROPERTIES;
import static co.com.bancolombia.Constants.MainFiles.MAIN_GRADLE;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpdateUtils;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import lombok.SneakyThrows;

public class UpgradeY2022M04D26 implements UpgradeAction {
  public static final String TASKS_NAMED_WRAPPER = "tasks.named('wrapper')";
  public static final String GRADLE_VERSION_WRAPPER =
      "\n\n" + "tasks.named('wrapper') {" + "gradleVersion = '7.4.2'\n" + "}\n";
  public static final String LANGUAGE_PROPERTY = "language";
  public static final String LANGUAGE_PROPERTY_VALUER = "\nlanguage=java";

  @Override
  @SneakyThrows
  public boolean up(ModuleBuilder builder) {
    return Boolean.logicalOr(
        UpdateUtils.appendIfNotContains(
            builder, MAIN_GRADLE, TASKS_NAMED_WRAPPER, GRADLE_VERSION_WRAPPER),
        UpdateUtils.appendIfNotContains(
            builder, GRADLE_PROPERTIES, LANGUAGE_PROPERTY, LANGUAGE_PROPERTY_VALUER));
  }

  @Override
  public String name() {
    return "1.9.9->2.0.0";
  }

  @Override
  public String description() {
    return "Append the language property,and gradle wrapper";
  }
}
