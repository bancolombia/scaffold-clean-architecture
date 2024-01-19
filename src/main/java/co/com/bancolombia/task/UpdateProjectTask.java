package co.com.bancolombia.task;

import static co.com.bancolombia.factory.upgrades.actions.UpdateDependencies.DEPENDENCIES_TO_UPDATE;
import static co.com.bancolombia.factory.upgrades.actions.UpdateDependencies.FILES_TO_UPDATE;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.upgrades.UpgradeFactory;
import co.com.bancolombia.task.annotations.CATask;
import co.com.bancolombia.utils.CommandUtils;
import co.com.bancolombia.utils.Utils;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.gradle.api.tasks.options.Option;
import org.gradle.internal.logging.text.StyledTextOutput;

@CATask(
    name = "updateCleanArchitecture",
    shortcut = "u",
    description = "Update project dependencies")
public class UpdateProjectTask extends AbstractCleanArchitectureDefaultTask {
  private final Set<String> dependencies = new HashSet<>();
  private BooleanOption git = BooleanOption.TRUE;

  @Option(option = "dependencies", description = "Set dependencies to update")
  public void setDependencies(String dependencies) {
    this.dependencies.addAll(Arrays.asList(dependencies.split("[ ,]+")));
  }

  @Option(option = "git", description = "Check git before changes")
  public void setGit(BooleanOption git) {
    this.git = git;
  }

  @Override
  public void execute() throws IOException, CleanException {
    if (git == BooleanOption.TRUE && CommandUtils.getDefault().hasGitPendingChanges(logger)) {
      getTextOutputFactory()
          .create(UpdateProjectTask.class)
          .style(StyledTextOutput.Style.Info)
          .println(
              "You have changes pending to be committed, please commit your changes before run this task"
                  + " or pass the '--git false' flag");
      return;
    }
    // Add specific parameters for UpgradeActions
    builder.addParam(DEPENDENCIES_TO_UPDATE, dependencies);
    builder.addParam(FILES_TO_UPDATE, Utils.getAllFilesWithExtension(builder.isKotlin()));
    UpgradeFactory factory = new UpgradeFactory();
    factory.buildModule(builder);
    builder.persist();
  }
}
