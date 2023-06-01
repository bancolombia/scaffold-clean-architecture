package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.SETTINGS_GRADLE;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpdateUtils;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import lombok.SneakyThrows;

public class UpgradeY2023M01D12 implements UpgradeAction {
  private static final String VALIDATION = "pluginManagement";
  private static final String VALUE =
      "pluginManagement {\n"
          + "    repositories {\n"
          + "        //mavenLocal()\n"
          + "        //maven { url '...' }\n"
          + "        gradlePluginPortal()\n"
          + "    }\n"
          + "}\n\n";
  private static final String MATCH = "";

  @Override
  @SneakyThrows
  public boolean up(ModuleBuilder builder) {
    return builder.updateFile(
        SETTINGS_GRADLE,
        content -> UpdateUtils.insertBeforeMatch(content, MATCH, VALIDATION, VALUE));
  }

  @Override
  public String name() {
    return "2.4.5->2.4.6";
  }

  @Override
  public String description() {
    return "Add pluginManagement in settings.gradle file";
  }
}
