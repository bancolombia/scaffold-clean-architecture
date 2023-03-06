package co.com.bancolombia.task;

import co.com.bancolombia.utils.SonarCheck;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.options.OptionValues;

public class InternalTask extends CleanArchitectureDefaultTask {
  private Action action = Action.SONARCHECK;

  @Option(option = "action", description = "Set task action to run")
  public void setAction(Action action) {
    this.action = action;
  }

  @OptionValues("action")
  public List<Action> getInputOptions() {
    return Arrays.asList(Action.values());
  }

  @TaskAction
  public void persistAnalyticsState() {
    if (Objects.requireNonNull(action) == Action.SONARCHECK) {
      getProject().getSubprojects().stream()
          .filter(project -> "app-service".equals(project.getName()))
          .findFirst()
          .ifPresent(
              project -> {
                try {
                  SonarCheck.parse(project);
                } catch (IOException e) {
                  logger.warn("Error parsing report", e);
                }
              });
    }
  }

  public enum Action {
    SONARCHECK
  }
}
