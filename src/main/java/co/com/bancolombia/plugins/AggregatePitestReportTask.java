package co.com.bancolombia.plugins;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.SkipWhenEmpty;
import org.gradle.api.tasks.TaskAction;
import org.pitest.aggregate.ReportAggregator;
import org.pitest.mutationtest.config.DirectoryResultOutputStrategy;
import org.pitest.mutationtest.config.UndatedReportDirCreationStrategy;

public abstract class AggregatePitestReportTask extends DefaultTask {

  @SkipWhenEmpty
  @InputFiles
  @PathSensitive(PathSensitivity.RELATIVE)
  public abstract ConfigurableFileCollection getMutationFiles();

  @InputFiles
  @PathSensitive(PathSensitivity.RELATIVE)
  public abstract ConfigurableFileCollection getLineCoverageFiles();

  @SkipWhenEmpty
  @InputFiles
  @PathSensitive(PathSensitivity.RELATIVE)
  public abstract ConfigurableFileCollection getSourceDirs();

  @SkipWhenEmpty
  @InputFiles
  @Classpath
  public abstract ConfigurableFileCollection getCompiledCodeDirs();

  @OutputDirectory
  public abstract DirectoryProperty getReportDir();

  @OutputFile
  public abstract RegularFileProperty getReportFile();

  @OutputFile
  public abstract RegularFileProperty getXmlReportFile();

  @TaskAction
  public void aggregate() {
    File outputDir = getReportDir().get().getAsFile();
    if (!outputDir.exists() && !outputDir.mkdirs()) {
      throw new IllegalStateException("Could not create directory " + outputDir.getAbsolutePath());
    }

    List<File> mutationFiles = existingFiles(getMutationFiles());
    List<File> lineCoverageFiles = existingFiles(getLineCoverageFiles());

    if (!mutationFiles.isEmpty() && !lineCoverageFiles.isEmpty()) {
      generateNativePitestHtml(outputDir, mutationFiles, lineCoverageFiles);
    }

    writeMergedXml(getXmlReportFile().get().getAsFile(), mutationFiles);
  }

  private void generateNativePitestHtml(
      File reportDir, List<File> mutationFiles, List<File> lineCoverageFiles) {
    try {
      ReportAggregator.Builder builder = ReportAggregator.builder();
      mutationFiles.forEach(builder::addMutationResultsFile);
      lineCoverageFiles.forEach(builder::addLineCoverageFile);
      existingFiles(getSourceDirs()).forEach(builder::addSourceCodeDirectory);
      existingFiles(getCompiledCodeDirs()).forEach(builder::addCompiledCodeDirectory);

      builder
          .resultOutputStrategy(
              new DirectoryResultOutputStrategy(
                  reportDir.getAbsolutePath(), new UndatedReportDirCreationStrategy()))
          .build()
          .aggregateReport();
    } catch (Exception exception) {
      throw new IllegalStateException("Could not generate aggregated PIT HTML report", exception);
    }
  }

  private void writeMergedXml(File output, List<File> mutationFiles) {
    File parent = output.getParentFile();
    if (parent != null && !parent.exists() && !parent.mkdirs()) {
      throw new IllegalStateException("Could not create directory " + parent.getAbsolutePath());
    }

    StringBuilder xml = new StringBuilder("<mutations>\n");
    mutationFiles.stream()
        .sorted(Comparator.comparing(File::getAbsolutePath))
        .forEach(
            report -> {
              String content = readAndNormalizeReport(report);
              if (!content.isEmpty()) {
                xml.append(content).append('\n');
              }
            });
    xml.append("</mutations>");

    try {
      Files.writeString(output.toPath(), xml, StandardCharsets.UTF_8);
    } catch (IOException exception) {
      throw new IllegalStateException("Could not write aggregated pitest xml report", exception);
    }
  }

  private List<File> existingFiles(ConfigurableFileCollection files) {
    return files.getFiles().stream().filter(File::exists).collect(Collectors.toList());
  }

  private String readAndNormalizeReport(File report) {
    try {
      return Files.readString(report.toPath(), StandardCharsets.UTF_8)
          .replaceAll("<\\?xml[^>]*>", "")
          .replaceAll("</?mutations( partial=['\\\"]true['\\\"])?\\s*>", "")
          .trim();
    } catch (IOException exception) {
      throw new IllegalStateException(
          "Could not read pitest report " + report.getAbsolutePath(), exception);
    }
  }
}
