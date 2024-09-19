package co.com.bancolombia.factory.upgrades.actions;

import co.com.bancolombia.Constants;
import co.com.bancolombia.exceptions.InvalidStateException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpdateUtils;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import lombok.SneakyThrows;

public class UpgradeY2023M10D02SonarRules implements UpgradeAction {
  public static final String CONCAT_VALUE =
      "\n" + "        property \"sonar.externalIssuesReportPaths\", \"build/issues.json\"";
  public static final String ISSUES_JSON = "issues.json";
  public static final String JACOCO_TEST_REPORT_XML = "jacocoTestReport.xml\"";
  public static final String JACOCO_MERGED_REPORT_XML = "jacocoMergedReport.xml\"";

  @Override
  @SneakyThrows
  public boolean up(ModuleBuilder builder) {
    return builder.updateFile(Constants.MainFiles.BUILD_GRADLE, this::insert);
  }

  private String insert(String content) {
    try {
      return UpdateUtils.insertAfterMatch(
          content, JACOCO_TEST_REPORT_XML, ISSUES_JSON, CONCAT_VALUE);
    } catch (InvalidStateException ignored) {
      return UpdateUtils.insertAfterMatch(
          content, JACOCO_MERGED_REPORT_XML, ISSUES_JSON, CONCAT_VALUE);
    }
  }

  @Override
  public String name() {
    return "3.6.1->3.6.2";
  }

  @Override
  public String description() {
    return "Add ArchUnit issues export to sonar";
  }
}
