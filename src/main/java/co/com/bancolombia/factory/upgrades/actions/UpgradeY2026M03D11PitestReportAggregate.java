package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.MAIN_GRADLE;

import co.com.bancolombia.Constants;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpdateUtils;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import lombok.SneakyThrows;

public class UpgradeY2026M03D11PitestReportAggregate implements UpgradeAction {

  private static final String OLD_PITEST_AGGREGATE =
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
              }""";

  private static final String NEW_PITEST_AGGREGATE =
      """
              pitestReportAggregate {
                  def rootReportDir = layout.buildDirectory.dir("reports/pitest")
                  def subprojectReports = subprojects.collect { subproject ->
                      subproject.layout.buildDirectory.file("reports/pitest/mutations.xml")
                  }

                  doLast {
                      def reportDir = rootReportDir.get().asFile
                      def consolidatedReport = new File(reportDir, 'mutations.xml')
                      consolidatedReport.withWriter { writer ->
                          writer.write("<mutations>\\n")
                          subprojectReports.each { reportProvider ->
                              def xmlReport = reportProvider.get().asFile
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
              }""";

  private static final String SPRING_BOOT_PLATFORM =
      "implementation platform(\"org.springframework.boot:spring-boot-dependencies:${springBootVersion}\")";

  @Override
  @SneakyThrows
  public boolean up(ModuleBuilder builder) {
    return builder.updateFile(
        MAIN_GRADLE,
        content -> {
          String partial = UpdateUtils.replace(content, OLD_PITEST_AGGREGATE, NEW_PITEST_AGGREGATE);
          return UpdateUtils.insertAfterMatch(
              partial,
              SPRING_BOOT_PLATFORM,
              "pitest-history-plugin",
              "\n        pitest 'org.pitest:pitest-history-plugin:"
                  .concat(Constants.PITEST_HISTORY_VERSION)
                  .concat("'"));
        });
  }

  @Override
  public String name() {
    return "4.2.0->4.3.0";
  }

  @Override
  public String description() {
    return "Update pitestReportAggregate to declare properties outside doLast for Gradle configuration cache compatibility";
  }
}
