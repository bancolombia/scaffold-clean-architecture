package co.com.bancolombia.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.testing.jacoco.tasks.JacocoReport;

public class RootQualityConventions {
  private static final String PITEST_AGGREGATE_TASK_NAME = "pitestReportAggregate";
  private static final String JACOCO_MERGED_TASK_NAME = "jacocoMergedReport";
  private static final String PITEST_PLUGIN_ID = "info.solidsoft.pitest";

  public void configure(Project project) {
    boolean pitestEnabled =
        project.getSubprojects().stream()
            .anyMatch(subproject -> subproject.getPluginManager().hasPlugin(PITEST_PLUGIN_ID));
    registerPitestAggregateTask(project, pitestEnabled);
    registerJacocoMergedReport(project, pitestEnabled);
    wireSonarDependencies(project, pitestEnabled);
    configureSonarProperties(project);
  }

  private void registerPitestAggregateTask(Project project, boolean pitestEnabled) {
    if (!pitestEnabled || project.getTasks().findByName(PITEST_AGGREGATE_TASK_NAME) != null) {
      return;
    }
    project
        .getTasks()
        .register(
            PITEST_AGGREGATE_TASK_NAME,
            AggregatePitestReportTask.class,
            task -> {
              task.getReportDir()
                  .set(project.getLayout().getBuildDirectory().dir("reports/pitest"));
              task.getReportFile()
                  .set(project.getLayout().getBuildDirectory().file("reports/pitest/index.html"));
              task.getXmlReportFile()
                  .set(
                      project.getLayout().getBuildDirectory().file("reports/pitest/mutations.xml"));
              project
                  .getSubprojects()
                  .forEach(
                      subproject -> {
                        task.getMutationFiles()
                            .from(
                                subproject
                                    .getLayout()
                                    .getBuildDirectory()
                                    .file("reports/pitest/mutations.xml"));
                        task.getLineCoverageFiles()
                            .from(
                                subproject
                                    .getLayout()
                                    .getBuildDirectory()
                                    .file("reports/pitest/linecoverage.xml"));
                        SourceSetContainer sourceSets =
                            subproject.getExtensions().findByType(SourceSetContainer.class);
                        if (sourceSets != null) {
                          SourceSet mainSourceSet =
                              sourceSets.findByName(SourceSet.MAIN_SOURCE_SET_NAME);
                          if (mainSourceSet != null) {
                            task.getSourceDirs().from(mainSourceSet.getAllSource().getSrcDirs());
                            task.getCompiledCodeDirs()
                                .from(mainSourceSet.getOutput().getClassesDirs());
                          }
                        }
                        task.dependsOn(
                            subproject.getTasks().matching(t -> "pitest".equals(t.getName())));
                      });
            });
  }

  private void registerJacocoMergedReport(Project project, boolean pitestEnabled) {
    if (project.getTasks().findByName(JACOCO_MERGED_TASK_NAME) != null) {
      return;
    }

    TaskProvider<JacocoReport> reportProvider =
        project
            .getTasks()
            .register(
                JACOCO_MERGED_TASK_NAME,
                JacocoReport.class,
                report -> {
                  report.getReports().getXml().getRequired().set(true);
                  report.getReports().getHtml().getRequired().set(true);
                  report.getReports().getCsv().getRequired().set(false);
                });

    project.afterEvaluate(
        ignored ->
            reportProvider.configure(
                report -> {
                  List<String> testTaskPaths = new ArrayList<>();
                  List<String> jacocoTaskPaths = new ArrayList<>();

                  project
                      .getSubprojects()
                      .forEach(
                          subproject -> {
                            collectTaskPath(subproject, "test", testTaskPaths);
                            collectTaskPath(subproject, "jacocoTestReport", jacocoTaskPaths);
                            configureReportInputs(report, subproject);
                          });

                  configureAggregationDependencies(
                      project, report, pitestEnabled, testTaskPaths, jacocoTaskPaths);
                }));
  }

  private void collectTaskPath(Project subproject, String taskName, List<String> taskPaths) {
    if (subproject.getTasks().findByName(taskName) != null) {
      taskPaths.add(subproject.getPath() + ":" + taskName);
    }
  }

  private void configureReportInputs(JacocoReport report, Project subproject) {
    SourceSetContainer sourceSets = subproject.getExtensions().findByType(SourceSetContainer.class);
    if (sourceSets != null) {
      SourceSet mainSourceSet = sourceSets.findByName(SourceSet.MAIN_SOURCE_SET_NAME);
      if (mainSourceSet != null) {
        report.getAdditionalSourceDirs().from(mainSourceSet.getAllSource().getSrcDirs());
        report.getSourceDirectories().from(mainSourceSet.getAllSource().getSrcDirs());
        report.getClassDirectories().from(mainSourceSet.getOutput().getClassesDirs());
      }
    }
    report
        .getExecutionData()
        .from(
            subproject
                .fileTree(subproject.getLayout().getBuildDirectory())
                .include("jacoco/*.exec", "jacoco/**/*.exec"));
  }

  private void configureAggregationDependencies(
      Project project,
      JacocoReport report,
      boolean pitestEnabled,
      List<String> testTaskPaths,
      List<String> jacocoTaskPaths) {
    report.dependsOn(testTaskPaths);
    report.dependsOn(jacocoTaskPaths);
    if (pitestEnabled && project.getTasks().findByName(PITEST_AGGREGATE_TASK_NAME) != null) {
      report.dependsOn(PITEST_AGGREGATE_TASK_NAME);
    }
  }

  private void wireSonarDependencies(Project project, boolean pitestEnabled) {
    project
        .getTasks()
        .matching(task -> "sonar".equals(task.getName()))
        .configureEach(
            task -> {
              task.dependsOn(JACOCO_MERGED_TASK_NAME);
              if (pitestEnabled
                  && project.getTasks().findByName(PITEST_AGGREGATE_TASK_NAME) != null) {
                task.dependsOn(PITEST_AGGREGATE_TASK_NAME);
              }
            });
  }

  private void configureSonarProperties(Project project) {
    Object sonarExtension = project.getExtensions().findByName("sonar");
    if (sonarExtension == null) {
      return;
    }

    String modules =
        project.getSubprojects().stream()
            .map(
                subproject ->
                    project
                        .getProjectDir()
                        .toPath()
                        .relativize(subproject.getProjectDir().toPath()))
            .map(path -> path.toString().replace('\\', '/'))
            .collect(Collectors.joining(","));
    String moduleBuildGradleSources =
        project.getSubprojects().stream()
            .map(
                subproject ->
                    project
                        .getProjectDir()
                        .toPath()
                        .relativize(subproject.getProjectDir().toPath()))
            .map(path -> path.toString().replace('\\', '/') + "/build.gradle")
            .collect(Collectors.joining(","));

    invokeSonarProperties(
        project,
        sonarExtension,
        sonarProperties -> {
          setSonarProperty(project, sonarProperties, "sonar.sourceEncoding", "UTF-8");
          setSonarProperty(project, sonarProperties, "sonar.modules", modules);
          setSonarProperty(
              project,
              sonarProperties,
              "sonar.sources",
              "src,deployment,settings.gradle,build.gradle," + moduleBuildGradleSources);
          setSonarProperty(
              project,
              sonarProperties,
              "sonar.exclusions",
              "**/MainApplication.java,**/**.bin,**/deployment/**");
          setSonarProperty(project, sonarProperties, "sonar.tests", "src/test");
          setSonarProperty(
              project, sonarProperties, "sonar.java.binaries", "**/build/classes/java/main");
          setSonarProperty(
              project, sonarProperties, "sonar.junit.reportsPath", "**/build/test-results/test");
          setSonarProperty(project, sonarProperties, "sonar.java.coveragePlugin", "jacoco");
          setSonarProperty(
              project,
              sonarProperties,
              "sonar.coverage.jacoco.xmlReportPaths",
              "build/reports/jacocoMergedReport/jacocoMergedReport.xml");
          setSonarProperty(
              project,
              sonarProperties,
              "sonar.pitest.reportPaths",
              "build/reports/pitest/mutations.xml");
          setSonarProperty(
              project, sonarProperties, "sonar.externalIssuesReportPaths", "build/issues.json");
        });
  }

  private void invokeSonarProperties(
      Project project, Object sonarExtension, Action<Object> action) {
    for (java.lang.reflect.Method method : sonarExtension.getClass().getMethods()) {
      if ("properties".equals(method.getName()) && method.getParameterCount() == 1) {
        try {
          method.invoke(sonarExtension, action);
        } catch (Exception exception) {
          project.getLogger().debug("Unable to configure sonar properties", exception);
        }
        return;
      }
    }
  }

  private void setSonarProperty(Project project, Object sonarProperties, String key, String value) {
    try {
      sonarProperties
          .getClass()
          .getMethod("property", String.class, Object.class)
          .invoke(sonarProperties, key, value);
    } catch (Exception exception) {
      project
          .getLogger()
          .debug("Skipping sonar property {} due to {}", key, exception.getMessage());
    }
  }
}
