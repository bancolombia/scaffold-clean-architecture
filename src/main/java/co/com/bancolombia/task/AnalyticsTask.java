package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.task.annotations.CATask;
import co.com.bancolombia.utils.FileUtils;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.options.OptionValues;

@CATask(name = "analytics", shortcut = "a", description = "Set analytics state")
public class AnalyticsTask extends AbstractCleanArchitectureDefaultTask {
  private BooleanOption enabled = BooleanOption.TRUE;

  @Option(option = "enabled", description = "Set analytics state")
  public void setAnalyticsState(BooleanOption enabled) {
    this.enabled = enabled;
  }

  @OptionValues("enabled")
  public List<BooleanOption> getInputOptions() {
    return Arrays.asList(BooleanOption.values());
  }

  @Override
  public void execute() throws IOException, CleanException {
    FileUtils.setGradleProperty(
        builder.getProject().getProjectDir().getPath(),
        "analytics",
        enabled == BooleanOption.TRUE ? "true" : "false");
  }
}
