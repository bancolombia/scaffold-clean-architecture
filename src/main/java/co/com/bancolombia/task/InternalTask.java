package co.com.bancolombia.task;

import co.com.bancolombia.task.annotations.CATask;
import co.com.bancolombia.utils.SonarCheck;
import co.com.bancolombia.utils.Utils;
import co.com.bancolombia.utils.offline.UpdateProjectDependencies;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.options.OptionValues;

@CATask(name = "internalTask", shortcut = "it", description = "Run non final user task")
public class InternalTask extends AbstractCleanArchitectureDefaultTask {
  private Action action = Action.SONAR_CHECK;

  @Internal @Getter private final Property<String> projectPath;

  public InternalTask() {
    notCompatibleWithConfigurationCache("This task performs validations that should always run");
    projectPath = getProject().getObjects().property(String.class);
    projectPath.set(getProject().getProjectDir().getPath());
  }

  @Option(option = "action", description = "Set task action to run")
  public void setAction(Action action) {
    this.action = action;
  }

  @OptionValues("action")
  public List<Action> getInputOptions() {
    return Arrays.asList(Action.values());
  }

  @Getter @Internal private boolean success = false;

  @Override
  public void execute() throws IOException {
    switch (Objects.requireNonNull(action)) {
      case SONAR_CHECK:
        SonarCheck.parse(projectPath.get());
        break;
      case UPDATE_DEPENDENCIES:
        String basePath = projectPath.get();
        List<String> files = Utils.getAllFilesWithGradleExtension(basePath);
        logger.lifecycle(
            "Updating project dependencies from root {} in files \n {}", basePath, files);
        UpdateProjectDependencies.builder().withFiles(files).build().run();
        break;
    }
    success = true;
  }

  public enum Action {
    SONAR_CHECK,
    UPDATE_DEPENDENCIES
  }
}
