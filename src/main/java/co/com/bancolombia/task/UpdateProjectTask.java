package co.com.bancolombia.task;

import static co.com.bancolombia.factory.upgrades.actions.UpdateDependencies.DEPENDENCIES_TO_UPDATE;
import static co.com.bancolombia.factory.upgrades.actions.UpdateDependencies.FILES_TO_UPDATE;

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

public class UpdateProjectTask extends CleanArchitectureDefaultTask {
  private final Set<String> dependencies = new HashSet<>();

  @Option(option = "dependencies", description = "Set dependencies to update")
  public void setDependencies(String dependencies) {
    this.dependencies.addAll(Arrays.asList(dependencies.split("[ ,]+")));
  }

  @TaskAction
  public void updateProject() throws IOException, CleanException {
    if (CommandUtils.getDefault().hasGitPendingChanges()) {
      logger.error(
          "ERROR: You have changes pending to be committed, please commit your changes before run this task");
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
