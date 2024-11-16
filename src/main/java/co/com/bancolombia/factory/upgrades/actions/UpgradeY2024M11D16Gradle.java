package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.MAIN_GRADLE;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpdateUtils;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import lombok.SneakyThrows;

public class UpgradeY2024M11D16Gradle implements UpgradeAction {

  @Override
  @SneakyThrows
  public boolean up(ModuleBuilder builder) {
    return builder.updateFile(
        MAIN_GRADLE,
        content ->
            UpdateUtils.replace(
                content,
                "sourceCompatibility = JavaVersion.VERSION_17",
                "\n\tjava {\n" + "\t\tsourceCompatibility = JavaVersion.VERSION_17\n" + "\t}"));
  }

  @Override
  public String name() {
    return "3.18.2->3.18.3";
  }

  @Override
  public String description() {
    return "Add java { } configuration block, backed by JavaPluginExtension";
  }
}
