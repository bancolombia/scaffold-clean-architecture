package co.com.bancolombia.task;

import co.com.bancolombia.Constants.BooleanOption;
import co.com.bancolombia.utils.FileUtils;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.options.OptionValues;

public class AnalyticsTask extends CleanArchitectureDefaultTask {
  private BooleanOption enabled = BooleanOption.TRUE;

  @Option(option = "enabled", description = "Set analytics state")
  public void setAnalyticsState(BooleanOption enabled) {
    this.enabled = enabled;
  }

  @OptionValues("enabled")
  public List<BooleanOption> getInputOptions() {
    return Arrays.asList(BooleanOption.values());
  }

  @TaskAction
  public void persistAnalyticsState() throws IOException {
    FileUtils.setGradleProperty(
        builder.getProject().getProjectDir().getPath(),
        "analytics",
        enabled == BooleanOption.TRUE ? "true" : "false");
  }
}
