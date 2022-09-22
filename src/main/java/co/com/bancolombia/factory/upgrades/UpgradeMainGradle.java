package co.com.bancolombia.factory.upgrades;

import static co.com.bancolombia.Constants.MainFiles.MAIN_GRADLE;

import co.com.bancolombia.factory.ModuleBuilder;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.SneakyThrows;

public class UpgradeMainGradle {

  @SneakyThrows
  public boolean up(ModuleBuilder builder, String match, String validation, String value) {
    AtomicBoolean applied = new AtomicBoolean(false);
    builder.updateFile(
        MAIN_GRADLE,
        content -> {
          String res = UpdateUtils.appendValidate(content, match, validation, value);
          if (!content.equals(res)) {
            applied.set(true);
          }
          return res;
        });
    return applied.get();
  }
}
