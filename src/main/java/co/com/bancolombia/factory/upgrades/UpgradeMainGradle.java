package co.com.bancolombia.factory.upgrades;

import static co.com.bancolombia.Constants.MainFiles.MAIN_GRADLE;

import co.com.bancolombia.factory.ModuleBuilder;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.SneakyThrows;

public class UpgradeMainGradle implements UpgradeAction {

  @Override
  public boolean up(ModuleBuilder builder) {
    return false;
  }

  @SneakyThrows
  public boolean up(ModuleBuilder builder, String MATCH, String VALIDATION, String VALUE) {
    AtomicBoolean applied = new AtomicBoolean(false);
    builder.updateFile(
        MAIN_GRADLE,
        content -> {
          String res = UpdateUtils.appendValidate(content, MATCH, VALIDATION, VALUE);
          if (!content.equals(res)) {
            applied.set(true);
          }
          return res;
        });
    return applied.get();
  }

  @Override
  public String name() {
    return null;
  }

  @Override
  public String description() {
    return null;
  }
}
