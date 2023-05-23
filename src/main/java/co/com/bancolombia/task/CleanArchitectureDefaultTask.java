package co.com.bancolombia.task;

import static org.gradle.internal.logging.text.StyledTextOutput.Style.*;

import co.com.bancolombia.analytics.AnalyticsBody;
import co.com.bancolombia.analytics.AnalyticsExporter;
import co.com.bancolombia.exceptions.InvalidTaskOptionException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import lombok.SneakyThrows;
import org.gradle.api.DefaultTask;
import org.gradle.api.Task;
import org.gradle.api.internal.tasks.options.OptionReader;
import org.gradle.api.logging.Logger;
import org.gradle.configuration.TaskDetailPrinter;
import org.gradle.execution.TaskSelection;
import org.gradle.internal.logging.text.StyledTextOutput;
import org.gradle.internal.logging.text.StyledTextOutputFactory;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

public class CleanArchitectureDefaultTask extends DefaultTask {
  protected final ModuleBuilder builder = new ModuleBuilder(getProject());
  protected final Logger logger = getProject().getLogger();

  public CleanArchitectureDefaultTask() {
    builder.setStyledLogger(getTextOutputFactory().create(CleanArchitectureDefaultTask.class));
  }

  protected void printHelp() {
    StyledTextOutput output =
        this.getTextOutputFactory().create(CleanArchitectureDefaultTask.class);
    final Task task = this;
    TaskSelection selection =
        new TaskSelection(getPath(), getName(), collection -> collection.add(task));
    OptionReader optionReader = this.getOptionReader();
    TaskDetailPrinter taskDetailPrinter = new TaskDetailPrinter(getName(), selection, optionReader);
    taskDetailPrinter.print(output);
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
    return getCandidates(resolvePackage())
        .filter(clazz -> clazz.getSimpleName().replace(resolvePrefix(), "").equalsIgnoreCase(type))
        .findFirst()
        .orElseThrow(
            () ->
                new InvalidTaskOptionException(
                    resolvePrefix()
                        + " of type "
                        + type
                        + " not found, valid values: "
                        + resolveTypes()))
        .getDeclaredConstructor()
        .newInstance();
  }

  @SneakyThrows
  protected List<String> resolveTypes() {
    return getCandidates(resolvePackage())
        .map(clazz -> clazz.getSimpleName().replace(resolvePrefix(), "").toUpperCase())
        .collect(Collectors.toList());
  }

  protected String resolvePrefix() {
    throw new UnsupportedOperationException("Method not implemented");
  }

  protected String resolvePackage() {
    throw new UnsupportedOperationException("Method not implemented");
  }

  private Stream<Class<? extends ModuleFactory>> getCandidates(String packageName) {
    return new Reflections(packageName, Scanners.SubTypes)
        .getSubTypesOf(ModuleFactory.class).stream();
  }

  protected void sendAnalytics(long duration) {
    sendAnalytics("default", duration);
  }

  protected void sendAnalytics(String type, long duration) {
    boolean enabled = true;
    try {
      enabled = builder.analyticsEnabled();
    } catch (IOException ignored) {
      StyledTextOutput output = getTextOutputFactory().create(CleanArchitectureDefaultTask.class);
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
                .with("project_language", builder.isKotlin() ? "kotlin" : "java")
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
}
