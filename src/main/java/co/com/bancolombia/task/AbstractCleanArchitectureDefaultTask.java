package co.com.bancolombia.task;

import static org.gradle.internal.logging.text.StyledTextOutput.Style.Description;
import static org.gradle.internal.logging.text.StyledTextOutput.Style.Header;
import static org.gradle.internal.logging.text.StyledTextOutput.Style.Success;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.exceptions.InvalidTaskOptionException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.models.AnalyticsBody;
import co.com.bancolombia.utils.ReflectionUtils;
import co.com.bancolombia.utils.analytics.AnalyticsExporter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import javax.inject.Inject;
import lombok.SneakyThrows;
import org.gradle.api.DefaultTask;
import org.gradle.api.internal.tasks.options.OptionReader;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.logging.text.StyledTextOutput;
import org.gradle.internal.logging.text.StyledTextOutputFactory;

public abstract class AbstractCleanArchitectureDefaultTask extends DefaultTask {
  protected ModuleBuilder builder;
  protected final Logger logger = getLogger();
  protected final File projectDir;
  protected final String projectName;
  protected final Map<String, File> childBuildFiles;
  protected final Map<String, File> childProjectDirs;

  protected AbstractCleanArchitectureDefaultTask() {
    this.projectDir = getProject().getProjectDir();
    this.projectName = getProject().getName();
    this.childBuildFiles = new HashMap<>();
    this.childProjectDirs = new HashMap<>();
    getProject()
        .getChildProjects()
        .forEach(
            (name, child) -> {
              childBuildFiles.put(name, child.getBuildFile());
              childProjectDirs.put(name, child.getProjectDir());
            });
  }

  protected ModuleBuilder createBuilder() {
    ModuleBuilder b = new ModuleBuilder(projectDir, projectName, getLogger());
    b.setChildProjects(childBuildFiles, childProjectDirs);
    return b;
  }

  @Internal
  protected ModuleBuilder getOrCreateBuilder() {
    if (builder == null) {
      builder = createBuilder();
      builder.setStyledLogger(
          getTextOutputFactory().create(AbstractCleanArchitectureDefaultTask.class));
    }
    return builder;
  }

  protected void printHelp() {
    logger.lifecycle("Run 'gradle help --task {}' for more information", getName());
  }

  @TaskAction
  public void executeBaseTask() throws IOException, CleanException {
    long start = System.currentTimeMillis();
    execute();
    afterExecute(
        () -> {
          String type = "After" + builder.getStringParam("type");
          return resolveFactory(resolvePackage(), resolvePrefix(), type);
        });
    afterExecute(
        () -> resolveFactory(getClass().getPackageName(), "", "After" + getCleanedClass()));
    resolveAnalyticsType()
        .ifPresentOrElse(
            type -> sendAnalytics(type, System.currentTimeMillis() - start),
            () -> sendAnalytics(System.currentTimeMillis() - start));
  }

  public void execute() throws IOException, CleanException {
    getOrCreateBuilder();
    doExecute();
  }

  protected abstract void doExecute() throws IOException, CleanException;

  private void afterExecute(Supplier<ModuleFactory> factorySupplier) {
    try {
      ModuleFactory factory = factorySupplier.get();
      logger.lifecycle("Applying {}", factory.getClass().getSimpleName());
      factory.buildModule(builder);
      logger.lifecycle("{} applied", factory.getClass().getSimpleName());
    } catch (UnsupportedOperationException | InvalidTaskOptionException ignored) {
      logger.debug("No ModuleFactory implementation");
    } catch (IOException | CleanException e) {
      logger.warn("Error on afterExecute factory: ", e);
    } catch (Exception e) { // NOSONAR
      logger.debug("Some other error", e);
    }
  }

  protected Optional<String> resolveAnalyticsType() {
    return Optional.empty();
  }

  @Inject
  protected StyledTextOutputFactory getTextOutputFactory() {
    throw new UnsupportedOperationException();
  }

  @Inject
  protected OptionReader getOptionReader() {
    throw new UnsupportedOperationException();
  }

  @SneakyThrows
  protected ModuleFactory resolveFactory(String type) {
    return resolveFactory(resolvePackage(), resolvePrefix(), type);
  }

  @SneakyThrows
  protected ModuleFactory resolveFactory(String packageName, String prefix, String type) {
    logger.info(
        "Finding factory with prefix {} and type {} in package {}", prefix, type, packageName);
    return ReflectionUtils.getModuleFactories(packageName)
        .filter(clazz -> clazz.getSimpleName().replace(prefix, "").equalsIgnoreCase(type))
        .findFirst()
        .orElseThrow(
            () ->
                new InvalidTaskOptionException(
                    prefix + " of type " + type + " not found, valid values:\n" + formatTypes()))
        .getDeclaredConstructor()
        .newInstance();
  }

  private String formatTypes() {
    return String.join("\n", resolveTypes());
  }

  @SneakyThrows
  protected List<String> resolveTypes() {
    return ReflectionUtils.getModuleFactories(resolvePackage())
        .map(clazz -> clazz.getSimpleName().replace(resolvePrefix(), "").toUpperCase())
        .sorted()
        .toList();
  }

  protected String resolvePrefix() {
    throw new UnsupportedOperationException("Method not implemented");
  }

  protected String resolvePackage() {
    throw new UnsupportedOperationException("Method not implemented");
  }

  protected void sendAnalytics(long duration) {
    sendAnalytics("default", duration);
  }

  protected void sendAnalytics(String type, long duration) {
    boolean enabled = true;
    try {
      enabled = builder.analyticsEnabled();
    } catch (IOException ignored) {
      StyledTextOutput output =
          getTextOutputFactory().create(AbstractCleanArchitectureDefaultTask.class);
      output
          .style(Header)
          .println("##########################")
          .println("##   Analytics Notice   ##")
          .println("##########################")
          .append("Analytics are enabled by default ")
          .style(Description)
          .append("if you want to disable it please run: ")
          .withStyle(Success)
          .println("gradle analytics --enabled false");
    }
    if (enabled) {
      try {
        AnalyticsBody.Event.Params params =
            AnalyticsBody.Event.Params.empty()
                .with("task_name", getName())
                .with("type", type)
                .with("project_type", builder.isReactive() ? "reactive" : "imperative")
                .with("project_language", "java")
                .with("duration", duration);

        AnalyticsExporter.collectMetric(
            AnalyticsBody.defaults()
                .withEvent(AnalyticsBody.Event.withName("task_executed").withParams(params)));
      } catch (Exception e) {
        logger.warn("Error sending analytics: {}", e.getMessage());
        logger.info("Error detail", e);
      }
    }
  }

  private String getCleanedClass() {
    String className = getClass().getSimpleName();
    if (className.contains("$")) {
      className = className.split("\\$")[1];
    }
    if (className.contains("_Decorated")) {
      className = className.split("_")[0];
    }
    return className;
  }

  public enum BooleanOption {
    TRUE,
    FALSE
  }
}
