package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.BUILD_GRADLE;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import lombok.SneakyThrows;

public class UpgradeY2025M08D01Sonar implements UpgradeAction {
  private static final String SONAR_TESTS_REGEX = "\"sonar\\.test\"";
  private static final String SONAR_JAVA_BINARIES_REGEX =
      "\"sonar\\.java\\.binaries\",\\s*\"([^\"]*)\"";
  private static final String SONAR_JUNIT_REPORTS_PATH_REGEX =
      "\"sonar\\.junit\\.reportsPath\",\\s*\"([^\"]*)\"";

  @Override
  @SneakyThrows
  public boolean up(ModuleBuilder builder) {
    return builder.updateFile(
            BUILD_GRADLE, content -> content.replaceAll(SONAR_TESTS_REGEX, "\"sonar.tests\""))
        | builder.updateFile(
            BUILD_GRADLE,
            content ->
                content.replaceAll(
                    SONAR_JAVA_BINARIES_REGEX,
                    "\"sonar.java.binaries\", \"**/build/classes/java/main\""))
        | builder.updateFile(
            BUILD_GRADLE,
            content ->
                content.replaceAll(
                    SONAR_JUNIT_REPORTS_PATH_REGEX,
                    "\"sonar.junit.reportsPath\", \"**/build/test-results/test\""));
  }

  @Override
  public String name() {
    return "3.23.2->3.23.3";
  }

  @Override
  public String description() {
    return "Add block hound validations";
  }
}
