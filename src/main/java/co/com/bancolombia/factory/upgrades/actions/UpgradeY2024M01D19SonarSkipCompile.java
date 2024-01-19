package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.GRADLE_PROPERTIES;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpdateUtils;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import lombok.SneakyThrows;

public class UpgradeY2024M01D19SonarSkipCompile implements UpgradeAction {
  public static final String SKIP_COMPILE = "systemProp.sonar.gradle.skipCompile";
  public static final String SKIP_COMPILE_VALUE = "\nsystemProp.sonar.gradle.skipCompile=true";

  @Override
  @SneakyThrows
  public boolean up(ModuleBuilder builder) {
    return UpdateUtils.appendIfNotContains(
        builder, GRADLE_PROPERTIES, SKIP_COMPILE, SKIP_COMPILE_VALUE);
  }

  @Override
  public String name() {
    return "3.13.0->3.13.1";
  }

  @Override
  public String description() {
    return "Add skipCompile for sonar";
  }
}
