package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.MAIN_GRADLE;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpdateUtils;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.SneakyThrows;

public class UpgradeY2022M08D10 implements UpgradeAction {

  public static final String VALIDATION = "useJUnitPlatform";

  public static final String JUNIT_PLATFORM_VALUER =
      "\n    test {\n" + "        useJUnitPlatform()\n" + "    }\n\n";

  public static final String MATCH = "\tdependencies";

  @Override
  @SneakyThrows
  public boolean up(ModuleBuilder builder) {
    AtomicBoolean applied = new AtomicBoolean(false);
    builder.updateFile(
        MAIN_GRADLE,
        content -> {
          String res =
              UpdateUtils.appendValidate(content, MATCH, VALIDATION, JUNIT_PLATFORM_VALUER);
          if (!content.equals(res)) {
            applied.set(true);
          }
          return res;
        });
    return applied.get();
  }

  @Override
  public String name() {
    return "2.4.4->2.4.5";
  }

  @Override
  public String description() {
    return "Append useJUnitPlatform in main.gradle file";
  }
}
