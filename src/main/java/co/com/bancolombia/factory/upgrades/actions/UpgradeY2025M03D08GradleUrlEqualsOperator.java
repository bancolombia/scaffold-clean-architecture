package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.MAIN_GRADLE;
import static co.com.bancolombia.Constants.MainFiles.SETTINGS_GRADLE;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import lombok.SneakyThrows;

public class UpgradeY2025M03D08GradleUrlEqualsOperator implements UpgradeAction {

  @Override
  @SneakyThrows
  public boolean up(ModuleBuilder builder) {
    return builder.updateFile(MAIN_GRADLE, this::updateUrl)
        | builder.updateFile(SETTINGS_GRADLE, this::updateUrl);
  }

  private String updateUrl(String content) {
    return content
        .replaceAll("\\burl\\s*=?\\s*uri\\s*\\(\\s*(['\"][^'\"]*['\"])\\s*\\)", "url = $1")
        .replaceAll("\\burl[ \\t]+(?!=)", "url = ");
  }

  @Override
  public String name() {
    return "3.20.6->3.20.15";
  }

  @Override
  public String description() {
    return "Assign the url property using the 'propName = value' syntax";
  }
}
