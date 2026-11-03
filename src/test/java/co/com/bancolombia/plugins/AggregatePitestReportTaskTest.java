package co.com.bancolombia.plugins;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AggregatePitestReportTaskTest {

  @Test
  void shouldGenerateXmlAggregateReport(@TempDir Path tempDir) throws IOException {
    Project project = ProjectBuilder.builder().withProjectDir(tempDir.toFile()).build();

    Path moduleAReport =
        tempDir.resolve("applications/app-service/build/reports/pitest/mutations.xml");
    Path moduleBReport = tempDir.resolve("domain/usecase/build/reports/pitest/mutations.xml");

    Files.createDirectories(moduleAReport.getParent());
    Files.createDirectories(moduleBReport.getParent());

    Files.writeString(
        moduleAReport,
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<mutations partial=\"true\">"
            + "<mutation status='KILLED'></mutation>"
            + "<mutation status='SURVIVED'></mutation>"
            + "</mutations>",
        StandardCharsets.UTF_8);

    Files.writeString(
        moduleBReport,
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<mutations partial='true'>"
            + "<mutation status='NO_COVERAGE'></mutation>"
            + "</mutations>",
        StandardCharsets.UTF_8);

    AggregatePitestReportTask task =
        project.getTasks().create("pitestReportAggregate", AggregatePitestReportTask.class);
    task.getMutationFiles().from(moduleAReport.toFile(), moduleBReport.toFile());
    task.getReportDir().set(project.getLayout().getBuildDirectory().dir("reports/pitest"));
    task.getReportFile()
        .set(project.getLayout().getBuildDirectory().file("reports/pitest/index.html"));
    task.getXmlReportFile()
        .set(project.getLayout().getBuildDirectory().file("reports/pitest/mutations.xml"));

    task.aggregate();

    Path xmlOutput = tempDir.resolve("build/reports/pitest/mutations.xml");

    String xmlContent = Files.readString(xmlOutput, StandardCharsets.UTF_8);

    assertTrue(xmlContent.contains("<mutation status='KILLED'>"));
    assertTrue(xmlContent.contains("<mutation status='SURVIVED'>"));
    assertTrue(xmlContent.contains("<mutation status='NO_COVERAGE'>"));
  }
}
