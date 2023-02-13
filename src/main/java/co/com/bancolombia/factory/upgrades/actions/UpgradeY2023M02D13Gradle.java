package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.APP_BUILD_GRADLE;
import static co.com.bancolombia.Constants.MainFiles.MAIN_GRADLE;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpdateUtils;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import lombok.SneakyThrows;

public class UpgradeY2023M02D13Gradle implements UpgradeAction {

  @Override
  @SneakyThrows
  public boolean up(ModuleBuilder builder) {
    return builder.updateFile(
            MAIN_GRADLE,
            content -> {
              String res =
                  UpdateUtils.replace(
                      content,
                      "tasks.withType(JavaCompile) {",
                      "tasks.withType(JavaCompile).configureEach {");
              res =
                  UpdateUtils.replace(
                      res,
                      "task jacocoMergedReport(type: JacocoReport) {",
                      "tasks.register('jacocoMergedReport', JacocoReport) {");
              res =
                  UpdateUtils.replace(
                      res,
                      "reportsDirectory = file(\"$buildDir/reports\")",
                      "reportsDirectory.set(file(\"$buildDir/reports\"))");
              return res;
            })
        | builder.updateFile(
            APP_BUILD_GRADLE,
            content ->
                UpdateUtils.replace(
                    content,
                    "task explodedJar(type: Copy) {",
                    "tasks.register('explodedJar', Copy) {"));
  }

  @Override
  public String name() {
    return "2.4.10->3.0.0";
  }

  @Override
  public String description() {
    return "Update gradle tasks";
  }
}
