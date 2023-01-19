package co.com.bancolombia.task;

import static co.com.bancolombia.factory.upgrades.actions.UpdateDependencies.DEPENDENCIES_TO_UPDATE;
import static co.com.bancolombia.factory.upgrades.actions.UpdateDependencies.FILES_TO_UPDATE;

import co.com.bancolombia.Constants.BooleanOption;
import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.upgrades.UpgradeFactory;
import co.com.bancolombia.utils.CommandUtils;
import co.com.bancolombia.utils.Utils;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.gradle.internal.logging.text.StyledTextOutput;

public class UpdateProjectTask extends CleanArchitectureDefaultTask {
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

  @TaskAction
  public void updateProject() throws IOException, CleanException {
    long start = System.currentTimeMillis();
    if (git == BooleanOption.TRUE && CommandUtils.getDefault().hasGitPendingChanges(logger)) {
      getTextOutputFactory()
          .create(UpdateProjectTask.class)
          .style(StyledTextOutput.Style.Error)
          .append(
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
    sendAnalytics(System.currentTimeMillis() - start);
  }
}
