package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.task.annotations.CATask;
import co.com.bancolombia.utils.FileUtils;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.options.OptionValues;

@CATask(name = "analytics", shortcut = "a", description = "Set analytics state")
public abstract class AnalyticsTask extends AbstractCleanArchitectureDefaultTask {
  private BooleanOption enabled = BooleanOption.TRUE;

  @Internal
  public abstract Property<String> getProjectPath();

  public AnalyticsTask() {
    getProjectPath().set(projectDir.getAbsolutePath());
  }

  @Option(option = "enabled", description = "Set analytics state")
  public void setAnalyticsState(BooleanOption enabled) {
    this.enabled = enabled;
  }

  @OptionValues("enabled")
  public List<BooleanOption> getInputOptions() {
    return Arrays.asList(BooleanOption.values());
  }

  @Override
  protected void doExecute() throws IOException, CleanException {
    FileUtils.setGradleProperty(
        this.getProjectPath().get(), "analytics", enabled == BooleanOption.TRUE ? "true" : "false");
  }
}
