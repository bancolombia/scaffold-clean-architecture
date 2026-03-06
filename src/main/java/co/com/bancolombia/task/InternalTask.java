package co.com.bancolombia.task;

import co.com.bancolombia.task.annotations.CATask;
import co.com.bancolombia.utils.SonarCheck;
import co.com.bancolombia.utils.Utils;
import co.com.bancolombia.utils.offline.UpdateProjectDependencies;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import org.gradle.api.Project;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.options.OptionValues;

@CATask(name = "internalTask", shortcut = "it", description = "Run non final user task")
public abstract class InternalTask extends AbstractCleanArchitectureDefaultTask {
  private Action action = Action.SONAR_CHECK;

  @Internal
  public abstract Property<String> getProjectPath();

  @Input
  public abstract SetProperty<String> getSubProjectPath();

  public InternalTask() {
    getProjectPath().set(projectDir.getAbsolutePath());

    Set<String> projectPaths =
        getProject().getSubprojects().stream()
            .map(Project::getProjectDir)
            .map(File::getPath)
            .collect(Collectors.toSet());
    projectPaths.add(projectDir.getAbsolutePath());
    getSubProjectPath().set(projectPaths);
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
  protected void doExecute() throws IOException {
    if (action == Action.UPDATE_DEPENDENCIES) {
      String basePath = getProjectPath().get();
      List<String> files = Utils.getAllFilesWithGradleExtension(basePath);
      logger.lifecycle(
          "Updating project dependencies from root {} in files \n {}", basePath, files);
      UpdateProjectDependencies.builder().withFiles(files).build().run();
    } else {
      SonarCheck.parse(getSubProjectPath().get());
    }
    success = true;
  }

  public enum Action {
    SONAR_CHECK,
    UPDATE_DEPENDENCIES
  }
}
