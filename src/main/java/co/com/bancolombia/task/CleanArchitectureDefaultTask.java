package co.com.bancolombia.task;

import static org.gradle.internal.logging.text.StyledTextOutput.Style.*;

import co.com.bancolombia.analytics.AnalyticsBody;
import co.com.bancolombia.analytics.AnalyticsExporter;
import co.com.bancolombia.factory.ModuleBuilder;
import java.io.IOException;
import javax.inject.Inject;
import org.gradle.api.DefaultTask;
import org.gradle.api.Task;
import org.gradle.api.internal.tasks.options.OptionReader;
import org.gradle.api.logging.Logger;
import org.gradle.configuration.TaskDetailPrinter;
import org.gradle.execution.TaskSelection;
import org.gradle.internal.logging.text.StyledTextOutput;
import org.gradle.internal.logging.text.StyledTextOutputFactory;

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
      AnalyticsExporter.collectMetric(
          AnalyticsBody.defaults()
              .withEvent(
                  AnalyticsBody.Event.withName("task_executed")
                      .withParams(
                          AnalyticsBody.Event.Params.empty()
                              .with("task_name", getName())
                              .with("type", type)
                              .with(
                                  "project_type", builder.isReactive() ? "reactive" : "imperative")
                              .with("duration", duration))));
    }
  }
}
