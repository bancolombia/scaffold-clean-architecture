package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.DOCKERFILE;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import co.com.bancolombia.utils.Utils;
import java.io.IOException;

public class UpgradeY2022M05D05 implements UpgradeAction {
  @Override
  @SuppressWarnings("unchecked")
  public boolean up(ModuleBuilder builder) {
    String name = builder.getProject().getRootProject().getName();
    try {
      return builder.updateFile(
          DOCKERFILE, content -> Utils.replaceExpression(content, "app.jar", name + ".jar"));
    } catch (IOException e) {
      builder.getProject().getLogger().debug("Error reading Dockerfile", e);
    }
    return false;
  }

  @Override
  public String name() {
    return "2.3.2->2.3.3";
  }

  @Override
  public String description() {
    return "Use specific jar name in dockerfile";
  }
}
