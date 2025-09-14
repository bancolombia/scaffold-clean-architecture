package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.task.annotations.CATask;
import co.com.bancolombia.utils.FileUtils;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.options.OptionValues;

@CATask(name = "analytics", shortcut = "a", description = "Set analytics state")
public class AnalyticsTask extends AbstractCleanArchitectureDefaultTask {
  private BooleanOption enabled = BooleanOption.TRUE;

  @Internal @Getter private final Property<String> projectPath;

  public AnalyticsTask() {
    this.projectPath = getProject().getObjects().property(String.class);

    // Configure lazy providers - capture information during configuration
    this.projectPath.set(getProject().provider(this::getAbsoluteProjectPath));
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
  public void execute() throws IOException, CleanException {
    FileUtils.setGradleProperty(
        this.projectPath.get(), "analytics", enabled == BooleanOption.TRUE ? "true" : "false");
  }

  private String getAbsoluteProjectPath() {
    return getProject().getLayout().getProjectDirectory().getAsFile().getAbsolutePath();
  }
}
