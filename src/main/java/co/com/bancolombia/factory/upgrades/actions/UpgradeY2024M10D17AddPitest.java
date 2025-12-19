package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.BUILD_GRADLE;
import static co.com.bancolombia.Constants.MainFiles.MAIN_GRADLE;

import co.com.bancolombia.Constants;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpdateUtils;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import lombok.SneakyThrows;

public class UpgradeY2024M10D17AddPitest implements UpgradeAction {
  private static final String PITEST_CONFIG =
      """
                  pitest {
                          targetClasses = ['{{package}}.*']
                          excludedClasses = []
                          excludedTestClasses = []
                          pitestVersion = '1.16.1'
                          verbose = true
                          outputFormats = ['XML', 'HTML']
                          threads = 8
                          exportLineCoverage = true
                          timestampedReports = false
                          //mutators = ['STRONGER', 'DEFAULTS']
                          fileExtensionsToFilter.addAll('xml', 'orbit')
                          junit5PluginVersion = '1.2.1'
                          failWhenNoMutations = false
                          jvmArgs = ["-XX:+AllowRedefinitionToAddDeleteMethods"]
                      }

                     \s""";

  private static final String PITEST_MERGED_CONFIG =
      """

                  pitestReportAggregate {
                      doLast {
                          def reportDir = layout.buildDirectory.dir("reports/pitest").get().asFile
                          def consolidatedReport = new File(reportDir, 'mutations.xml')
                          consolidatedReport.withWriter { writer ->
                              writer.write("<mutations>\\n")
                              subprojects.each { subproject ->
                                  def xmlReport = subproject.layout.buildDirectory.file("reports/pitest/mutations.xml").get().asFile
                                  if (xmlReport.exists()) {
                                      def xmlContent = xmlReport.text
                                      xmlContent = xmlContent.replaceAll("<\\\\?xml[^>]*>", "")
                                      xmlContent = xmlContent.replaceAll("</?mutations( partial=\\"true\\")?>", "")
                                      writer.write(xmlContent.trim() + "\\n")
                                  }
                              }
                              writer.write("</mutations>")
                          }
                      }
                  }

                  """;

  @Override
  @SneakyThrows
  public boolean up(ModuleBuilder builder) {
    boolean appliedToBuildGradle =
        builder.updateFile(
            BUILD_GRADLE,
            content -> {
              String partial =
                  UpdateUtils.insertAfterMatch(
                      content,
                      "ext {",
                      "pitestVersion",
                      "\n        pitestVersion = '" + Constants.GRADLE_PITEST_VERSION + "'");

              partial =
                  UpdateUtils.insertAfterMatch(
                      partial,
                      "plugins {",
                      "info.solidsoft.pitest",
                      "\n    id 'info.solidsoft.pitest' version \"${pitestVersion}\" apply false");

              return UpdateUtils.insertAfterMatch(
                  partial,
                  "jacocoMergedReport.xml\"",
                  "sonar.pitest.reportPaths",
                  "\n        property \"sonar.pitest.reportPaths\", \"build/reports/pitest/mutations.xml\"");
            });
    boolean appliedToMainGradle =
        builder.updateFile(
            MAIN_GRADLE,
            content -> {
              String partial =
                  UpdateUtils.addToStartIfNotContains(
                      content,
                      "info.solidsoft.pitest.aggregator",
                      "apply plugin: 'info.solidsoft.pitest.aggregator'\n\n");

              partial =
                  UpdateUtils.insertAfterMatch(
                      partial,
                      "subprojects {",
                      "'info.solidsoft.pitest'",
                      "\n    apply plugin: 'info.solidsoft.pitest'");

              String packageLoaded = builder.getStringParam("package");
              if (packageLoaded == null) {
                packageLoaded = "your.package";
              }
              String pitest = PITEST_CONFIG.replace("{{package}}", packageLoaded);
              partial =
                  UpdateUtils.insertBeforeMatch(
                      partial, "jacocoTestReport {", "pitestVersion", pitest);

              partial =
                  UpdateUtils.insertAfterMatch(
                      partial,
                      "subprojects.jacocoTestReport",
                      "pitestReportAggregate",
                      ", pitestReportAggregate");

              partial =
                  UpdateUtils.insertAfterMatch(partial, "dependsOn test", "'pitest'", ", 'pitest'");

              partial =
                  UpdateUtils.insertAfterMatch(
                      partial,
                      "dependencies {",
                      "junit-platform-launcher",
                      "\n        testRuntimeOnly 'org.junit.platform:junit-platform-launcher'");

              return UpdateUtils.insertBeforeMatch(
                  partial,
                  "tasks.named('wrapper')",
                  "pitestReportAggregate {",
                  PITEST_MERGED_CONFIG);
            });
    return appliedToBuildGradle || appliedToMainGradle;
  }

  @Override
  public String name() {
    return "3.17.24->3.18";
  }

  @Override
  public String description() {
    return "Add skipCompile for sonar";
  }
}
