package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.MAIN_GRADLE;
import static co.com.bancolombia.Constants.MainFiles.SETTINGS_GRADLE;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import lombok.SneakyThrows;

public class UpgradeY2025M03D08Gradle implements UpgradeAction {

  @Override
  @SneakyThrows
  public boolean up(ModuleBuilder builder) {
    return builder.updateFile(MAIN_GRADLE, content -> updateUrl(builder, MAIN_GRADLE, content))
        | builder.updateFile(
            SETTINGS_GRADLE, content -> updateUrl(builder, SETTINGS_GRADLE, content));
  }

  private String updateUrl(ModuleBuilder builder, String file, String content) {
    if (!builder.findExpressions(file, "url\\s*=\\s*['\"].*?['\"]").isEmpty()) {
      return content;
    }
    return content.replaceAll("url\\s*", "url = ");
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
