package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.BUILD_GRADLE;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpdateUtils;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import lombok.SneakyThrows;

public class UpgradeY2023M12D15Gradle implements UpgradeAction {

  @Override
  @SneakyThrows
  public boolean up(ModuleBuilder builder) {
    return builder.updateFile(
        BUILD_GRADLE,
        content -> {
          String res =
              UpdateUtils.replace(
                  content,
                  "property \"sonar.sourceEnconding\", \"UTF-8\"",
                  "property \"sonar.sourceEncoding\", \"UTF-8\"");
          res =
              UpdateUtils.replace(
                  res,
                  "property \"sonar.coverage.jacoco.xmlReportPaths\", \"build/reports/jacoco/test/jacocoTestReport.xml\"",
                  "property \"sonar.coverage.jacoco.xmlReportPaths\", \"build/reports/jacocoMergedReport/jacocoMergedReport.xml\"");
          return res;
        });
  }

  @Override
  public String name() {
    return "3.8.0->3.8.1";
  }

  @Override
  public String description() {
    return "Renaming bad sonar property with sonar.sourceEncoding";
  }
}
