package co.com.bancolombia.plugins;

import co.com.bancolombia.Constants;
import groovy.lang.GroovyObject;
import java.util.List;
import org.gradle.api.JavaVersion;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.plugins.UnknownPluginException;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.api.tasks.testing.Test;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.gradle.testing.jacoco.tasks.JacocoReport;

public class ChildConventionPlugin implements Plugin<Project> {
  private static final String JAVA_PLUGIN_ID = "java";
  private static final String JACOCO_PLUGIN_ID = "jacoco";
  private static final String DEPENDENCY_MANAGEMENT_PLUGIN_ID = "io.spring.dependency-management";
  private static final String PITEST_PLUGIN_ID = "info.solidsoft.pitest";
  private static final String IMPLEMENTATION_CONFIGURATION = "implementation";
  private static final String TEST_IMPLEMENTATION_CONFIGURATION = "testImplementation";
  private static final String PITEST_CONFIGURATION = "pitest";

  /** Applies shared conventions for generated Java subprojects. */
  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(JAVA_PLUGIN_ID);
    project.getPluginManager().apply(JACOCO_PLUGIN_ID);
    applyOptionalPlugin(project, DEPENDENCY_MANAGEMENT_PLUGIN_ID);
    applyOptionalPlugin(project, PITEST_PLUGIN_ID);

    configureJavaToolchain(project);
    configureTests(project);
    configureDependencies(project);
    configurePitest(project);
    configureJacocoReports(project);
    configureCompilerArgs(project);
    linkValidateStructure(project);
  }

  private void applyOptionalPlugin(Project project, String pluginId) {
    try {
      project.getPluginManager().apply(pluginId);
    } catch (UnknownPluginException exception) {
      project
          .getLogger()
          .debug("Skipping optional plugin {} because it is not available", pluginId);
    }
  }

  private void configureJavaToolchain(Project project) {
    project
        .getExtensions()
        .configure(
            JavaPluginExtension.class,
            javaExtension ->
                javaExtension
                    .getToolchain()
                    .getLanguageVersion()
                    .set(JavaLanguageVersion.of(resolveJavaVersion(project))));
  }

  private int resolveJavaVersion(Project project) {
    Object javaVersion = project.getRootProject().findProperty("javaVersion");
    if (javaVersion == null) {
      return 25;
    }
    return Integer.parseInt(javaVersion.toString());
  }

  private void configureTests(Project project) {
    project.getTasks().withType(Test.class).configureEach(Test::useJUnitPlatform);

    boolean reactive =
        Boolean.parseBoolean(String.valueOf(project.getRootProject().findProperty("reactive")));
    if (reactive && JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_13)) {
      project
          .getTasks()
          .withType(Test.class)
          .configureEach(
              test -> {
                test.getJvmArgs().add("-XX:+AllowRedefinitionToAddDeleteMethods");
                test.getJvmArgs().add("-XX:+EnableDynamicAgentLoading");
                test.getJvmArgs().add("-Djdk.attach.allowAttachSelf=true");
              });
    }

    project
        .getTasks()
        .withType(Test.class)
        .configureEach(test -> test.finalizedBy(project.getTasks().named("jacocoTestReport")));
  }

  private void configureDependencies(Project project) {
    DependencyHandler dependencies = project.getDependencies();
    String springBootBom =
        "org.springframework.boot:spring-boot-dependencies:" + Constants.SPRING_BOOT_VERSION;
    dependencies.add(IMPLEMENTATION_CONFIGURATION, dependencies.platform(springBootBom));
    dependencies.add("testRuntimeOnly", "org.junit.platform:junit-platform-launcher");
    dependencies.add(
        TEST_IMPLEMENTATION_CONFIGURATION, "org.springframework.boot:spring-boot-starter-test");

    boolean reactive =
        Boolean.parseBoolean(String.valueOf(project.getRootProject().findProperty("reactive")));
    if (reactive) {
      dependencies.add(IMPLEMENTATION_CONFIGURATION, "io.projectreactor:reactor-core");
      dependencies.add(IMPLEMENTATION_CONFIGURATION, "io.projectreactor.addons:reactor-extra");
      dependencies.add(TEST_IMPLEMENTATION_CONFIGURATION, "io.projectreactor:reactor-test");
    }

    boolean lombok =
        Boolean.parseBoolean(String.valueOf(project.getRootProject().findProperty("lombok")));
    if (lombok) {
      String lombokDependency = "org.projectlombok:lombok:" + Constants.LOMBOK_VERSION;
      dependencies.add("compileOnly", lombokDependency);
      dependencies.add("annotationProcessor", lombokDependency);
      dependencies.add("testCompileOnly", lombokDependency);
      dependencies.add("testAnnotationProcessor", lombokDependency);
    }

    if (isPitestEnabled(project)
        && project.getConfigurations().findByName(PITEST_CONFIGURATION) != null) {
      dependencies.add(
          PITEST_CONFIGURATION,
          "org.pitest:pitest-history-plugin:" + Constants.PITEST_HISTORY_VERSION);
    }
  }

  private void configurePitest(Project project) {
    if (!isPitestEnabled(project)) {
      return;
    }
    Object pitestExtension = project.getExtensions().findByName(PITEST_CONFIGURATION);
    if (!(pitestExtension instanceof GroovyObject groovyPitestExtension)) {
      project.getLogger().debug("Pitest extension not available for dynamic configuration");
      return;
    }

    setPitestProperty(
        project, groovyPitestExtension, "targetClasses", List.of(resolvePackage(project) + ".*"));
    setPitestProperty(project, groovyPitestExtension, "excludedClasses", List.of());
    setPitestProperty(project, groovyPitestExtension, "excludedTestClasses", List.of());
    setPitestProperty(project, groovyPitestExtension, "pitestVersion", Constants.PITEST_VERSION);
    setPitestProperty(project, groovyPitestExtension, "verbose", Boolean.FALSE);
    setPitestProperty(project, groovyPitestExtension, "outputFormats", List.of("XML", "HTML"));
    setPitestProperty(
        project, groovyPitestExtension, "threads", Runtime.getRuntime().availableProcessors());
    setPitestProperty(project, groovyPitestExtension, "exportLineCoverage", Boolean.TRUE);
    setPitestProperty(project, groovyPitestExtension, "useClasspathFile", Boolean.TRUE);
    setPitestProperty(project, groovyPitestExtension, "withHistory", Boolean.TRUE);
    setPitestProperty(project, groovyPitestExtension, "timestampedReports", Boolean.FALSE);
    setPitestProperty(
        project, groovyPitestExtension, "junit5PluginVersion", Constants.PITEST_JUNIT5_VERSION);
    setPitestProperty(project, groovyPitestExtension, "failWhenNoMutations", Boolean.FALSE);
    setPitestProperty(
        project,
        groovyPitestExtension,
        "jvmArgs",
        List.of("-XX:+AllowRedefinitionToAddDeleteMethods"));
    setPitestProperty(
        project, groovyPitestExtension, "fileExtensionsToFilter", List.of("xml", "orbit"));
  }

  private String resolvePackage(Project project) {
    Object packageName = project.getRootProject().findProperty("package");
    return packageName == null ? "co.com.bancolombia" : packageName.toString();
  }

  private void configureJacocoReports(Project project) {
    project
        .getTasks()
        .withType(JacocoReport.class)
        .configureEach(
            report -> {
              report.dependsOn(project.getTasks().named("test"));
              report.getReports().getXml().getRequired().set(true);
              report
                  .getReports()
                  .getXml()
                  .getOutputLocation()
                  .set(project.getLayout().getBuildDirectory().file("reports/jacoco.xml"));
              report.getReports().getCsv().getRequired().set(false);
              report
                  .getReports()
                  .getHtml()
                  .getOutputLocation()
                  .set(project.getLayout().getBuildDirectory().dir("reports/jacocoHtml"));
            });

    project.afterEvaluate(
        ignored -> {
          if (isPitestEnabled(project)
              && project.getTasks().findByName(PITEST_CONFIGURATION) != null) {
            project
                .getTasks()
                .withType(JacocoReport.class)
                .configureEach(
                    report -> report.dependsOn(project.getTasks().named(PITEST_CONFIGURATION)));
          }
        });
  }

  private void configureCompilerArgs(Project project) {
    project
        .getTasks()
        .withType(JavaCompile.class)
        .matching(task -> "compileJava".equals(task.getName()))
        .configureEach(
            compileTask ->
                compileTask
                    .getOptions()
                    .getCompilerArgs()
                    .add("-Amapstruct.suppressGeneratorTimestamp=true"));
  }

  private boolean isPitestEnabled(Project project) {
    return project.getPluginManager().hasPlugin(PITEST_PLUGIN_ID);
  }

  private void setPitestProperty(
      Project project, GroovyObject extension, String propertyName, Object value) {
    try {
      extension.setProperty(propertyName, value);
    } catch (Exception exception) {
      project
          .getLogger()
          .debug("Skipping pitest property {} due to {}", propertyName, exception.getMessage());
    }
  }

  private void linkValidateStructure(Project project) {
    Project rootProject = project.getRootProject();
    if (rootProject.getTasks().findByName("validateStructure") == null) {
      return;
    }
    project
        .getTasks()
        .named("compileJava")
        .configure(task -> task.dependsOn(rootProject.getTasks().named("validateStructure")));
  }
}
