package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.BUILD_GRADLE;
import static co.com.bancolombia.Constants.MainFiles.MAIN_GRADLE;

import co.com.bancolombia.Constants;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpdateUtils;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import lombok.SneakyThrows;

public class UpgradeY2024M10D17AddPitest implements UpgradeAction {
  private final String pitestConfig =
      "pitest {\n"
          + "        targetClasses = ['{{package}}.*']\n"
          + "        excludedClasses = []\n"
          + "        excludedTestClasses = []\n"
          + "        pitestVersion = '1.16.1'\n"
          + "        verbose = true\n"
          + "        outputFormats = ['XML', 'HTML']\n"
          + "        threads = 8\n"
          + "        exportLineCoverage = true\n"
          + "        timestampedReports = false\n"
          + "        //mutators = ['STRONGER', 'DEFAULTS']\n"
          + "        fileExtensionsToFilter.addAll('xml', 'orbit')\n"
          + "        junit5PluginVersion = '1.2.1'\n"
          + "        failWhenNoMutations = false\n"
          + "        jvmArgs = [\"-XX:+AllowRedefinitionToAddDeleteMethods\"]\n"
          + "    }\n"
          + "\n"
          + "    ";

  private final String pitestMergedConfig =
      "\npitestReportAggregate {\n"
          + "    doLast {\n"
          + "        def reportDir = layout.buildDirectory.dir(\"reports/pitest\").get().asFile\n"
          + "        def consolidatedReport = new File(reportDir, 'mutations.xml')\n"
          + "        consolidatedReport.withWriter { writer ->\n"
          + "            writer.write(\"<mutations>\\n\")\n"
          + "            subprojects.each { subproject ->\n"
          + "                def xmlReport = subproject.layout.buildDirectory.file(\"reports/pitest/mutations.xml\").get().asFile\n"
          + "                if (xmlReport.exists()) {\n"
          + "                    def xmlContent = xmlReport.text\n"
          + "                    xmlContent = xmlContent.replaceAll(\"<\\\\?xml[^>]*>\", \"\")\n"
          + "                    xmlContent = xmlContent.replaceAll(\"</?mutations( partial=\\\"true\\\")?>\", \"\")\n"
          + "                    writer.write(xmlContent.trim() + \"\\n\")\n"
          + "                }\n"
          + "            }\n"
          + "            writer.write(\"</mutations>\")\n"
          + "        }\n"
          + "    }\n"
          + "}\n\n";

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
                      "\n        pitestVersion = '" + Constants.PITEST_VERSION + "'");

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
              String pitest = pitestConfig.replace("{{package}}", packageLoaded);
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

              return UpdateUtils.insertBeforeMatch(
                  partial, "tasks.named('wrapper')", "pitestReportAggregate {", pitestMergedConfig);
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
