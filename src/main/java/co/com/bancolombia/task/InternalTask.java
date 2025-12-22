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
public class InternalTask extends AbstractCleanArchitectureDefaultTask {
  private Action action = Action.SONAR_CHECK;

  @Internal @Getter private final Property<String> projectPath;
  @Input @Getter private final SetProperty<String> subProjectPath;

  public InternalTask() {
    projectPath = getProject().getObjects().property(String.class);
    projectPath.set(getProject().getProjectDir().getPath());

    this.subProjectPath = getProject().getObjects().setProperty(String.class);
    this.subProjectPath.set(getProject().provider(this::getAllSubProjectPaths));
  }

  private Set<String> getAllSubProjectPaths() {
    Set<String> projectPaths =
        getProject().getSubprojects().stream()
            .map(Project::getProjectDir)
            .map(File::getPath)
            .collect(Collectors.toSet());

    projectPaths.add(projectPath.get());
    return projectPaths;
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
    if (action == Action.UPDATE_DEPENDENCIES) {
      String basePath = projectPath.get();
      List<String> files = Utils.getAllFilesWithGradleExtension(basePath);
      logger.lifecycle(
          "Updating project dependencies from root {} in files \n {}", basePath, files);
      UpdateProjectDependencies.builder().withFiles(files).build().run();
    } else {
      SonarCheck.parse(subProjectPath.get());
    }
    success = true;
  }

  public enum Action {
    SONAR_CHECK,
    UPDATE_DEPENDENCIES
  }
}
