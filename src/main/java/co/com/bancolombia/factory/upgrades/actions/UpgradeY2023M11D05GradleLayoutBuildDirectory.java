package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.APP_BUILD_GRADLE;
import static co.com.bancolombia.Constants.MainFiles.MAIN_GRADLE;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpdateUtils;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import lombok.SneakyThrows;

public class UpgradeY2023M11D05GradleLayoutBuildDirectory implements UpgradeAction {

  @Override
  @SneakyThrows
  public boolean up(ModuleBuilder builder) {
    return builder.updateFile(
            MAIN_GRADLE,
            content -> {
              String res =
                  UpdateUtils.replace(
                      content,
                      "xml.setOutputLocation file(\"${buildDir}/reports/jacoco.xml\")",
                      "xml.setOutputLocation layout.buildDirectory.file(\"reports/jacoco.xml\")");
              res =
                  UpdateUtils.replace(
                      res,
                      "html.setOutputLocation file(\"${buildDir}/reports/jacocoHtml\")",
                      "html.setOutputLocation layout.buildDirectory.dir(\"reports/jacocoHtml\")");
              res =
                  UpdateUtils.replace(
                      res,
                      "reportsDirectory.set(file(\"$buildDir/reports\"))",
                      "reportsDirectory.set(layout.buildDirectory.dir(\"reports\"))");
              return res;
            })
        | builder.updateFile(
            APP_BUILD_GRADLE,
            content ->
                UpdateUtils.replace(
                    content,
                    "into \"${buildDir}/exploded\"",
                    "into layout.buildDirectory.dir(\"exploded\")"));
  }

  @Override
  public String name() {
    return "3.6.4->3.6.5";
  }

  @Override
  public String description() {
    return "Update deprecated Project.buildDir with Project.layout.buildDirectory";
  }
}
