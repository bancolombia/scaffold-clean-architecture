package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.factory.upgrades.actions.UpdateDependencies.FILES_TO_UPDATE;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpdateUtils;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import java.util.List;

public class UpgradeY2022M05D02 implements UpgradeAction {
  @Override
  @SuppressWarnings("unchecked")
  public boolean up(ModuleBuilder builder) {
    List<String> gradleFiles = (List<String>) builder.getParam(FILES_TO_UPDATE);
    gradleFiles.forEach(file -> applyUpdate(builder, file));
    return true;
  }

  @Override
  public String name() {
    return "Gradle 6.x.x to 7.x.x";
  }

  @Override
  public String description() {
    return "Update of gradle removed configurations";
  }

  private void applyUpdate(ModuleBuilder builder, String file) {
    UpdateUtils.updateConfiguration(builder, file, "compile", "implementation");
    UpdateUtils.updateConfiguration(builder, file, "runtime", "runtimeOnly");
    UpdateUtils.updateConfiguration(builder, file, "testRuntime", "testRuntimeOnly");
    UpdateUtils.updateConfiguration(builder, file, "testCompile", "testImplementation");
    builder.updateExpression(file, "compile.exclude", "implementation.exclude");
  }
}
