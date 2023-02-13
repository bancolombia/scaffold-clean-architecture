package co.com.bancolombia.factory.upgrades;

import static co.com.bancolombia.Constants.MainFiles.MAIN_GRADLE;

import co.com.bancolombia.factory.ModuleBuilder;
import lombok.SneakyThrows;

public class UpgradeMainGradle {

  @SneakyThrows
  public boolean up(ModuleBuilder builder, String match, String validation, String value) {
    return builder.updateFile(
        MAIN_GRADLE, content -> UpdateUtils.appendValidate(content, match, validation, value));
  }
}
