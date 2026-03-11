package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.LOMBOK_CONFIG;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpdateUtils;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import lombok.SneakyThrows;

public class UpgradeY2026M03D11LombokJacksonVersion implements UpgradeAction {

  private static final String JACKSON_VERSION_PROPERTY = "lombok.jacksonized.jacksonVersion += 3";

  @Override
  @SneakyThrows
  public boolean up(ModuleBuilder builder) {
    return builder.updateFile(
        LOMBOK_CONFIG,
        content ->
            UpdateUtils.insertAfterMatch(
                content,
                "lombok.addLombokGeneratedAnnotation = true",
                JACKSON_VERSION_PROPERTY,
                "\n" + JACKSON_VERSION_PROPERTY));
  }

  @Override
  public String name() {
    return "4.2.0->4.3.0";
  }

  @Override
  public String description() {
    return "Add lombok.jacksonized.jacksonVersion += 3 to lombok.config for Jackson 3 compatibility";
  }
}
