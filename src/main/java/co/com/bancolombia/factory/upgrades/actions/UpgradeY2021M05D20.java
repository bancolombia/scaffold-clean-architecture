package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.MAIN_GRADLE;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.SneakyThrows;

public class UpgradeY2021M05D20 implements UpgradeAction {
  public static final String VALIDATION = "compileJava.dependsOn validateStructure";

  @Override
  @SneakyThrows
  public boolean up(ModuleBuilder builder) {
    AtomicBoolean applied = new AtomicBoolean(false);
    builder.updateFile(
        MAIN_GRADLE,
        content -> {
          String res = appendValidate(content);
          if (!content.equals(res)) {
            applied.set(true);
          }
          return res;
        });
    return applied.get();
  }

  @Override
  public String name() {
    return "1.9.3->1.9.4";
  }

  @Override
  public String description() {
    return "Append validate structure task dependency";
  }

  private String appendValidate(String main) {
    if (main.contains(VALIDATION)) {
      return main;
    }
    int start = main.indexOf("dependencies");
    return main.substring(0, start) + VALIDATION + "\n\t" + main.substring(start);
  }
}
