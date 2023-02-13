package co.com.bancolombia.factory.upgrades.samples;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import java.io.IOException;

public class AnUpdate implements UpgradeAction {
  @Override
  public boolean up(ModuleBuilder builder) {
    try {
      return builder.updateFile("build.gradle", content -> content + "modified");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String name() {
    return "0.0.1->0.0.2";
  }

  @Override
  public String description() {
    return "Sample update";
  }
}
