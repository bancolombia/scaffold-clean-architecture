package co.com.bancolombia.task;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.task.annotations.CATask;
import co.com.bancolombia.utils.Utils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.options.OptionValues;

@CATask(name = "deleteModule", shortcut = "dm", description = "Delete gradle module")
public class DeleteModuleTask extends AbstractCleanArchitectureDefaultTask {
  private String module;

  @Internal private final ListProperty<String> availableModules;

  public DeleteModuleTask() {
    this.availableModules = getProject().getObjects().listProperty(String.class);

    // Configure lazy providers - capture information during configuration
    this.availableModules.set(getProject().provider(this::getChildProjectNames));
  }

  @Option(option = "module", description = "Set module name to delete")
  public void setModule(String module) {
    this.module = module;
  }

  @OptionValues("module")
  public List<String> getModules() {
    return availableModules.get();
  }

  @Override
  public void execute() throws IOException, CleanException {
    if (module == null || !availableModules.get().contains(module)) {
      printHelp();
      throw new IllegalArgumentException(
          "No valid module name is set, usage: gradle deleteModule --module "
              + Utils.formatTaskOptions(getModules()));
    }
    String dependency = buildImplementationFromProject(":" + module);
    builder.deleteModule(module);
    builder.removeFromSettings(module);
    builder.removeDependencyFromModule(APP_SERVICE, dependency);
    builder.persist();
  }

  @Override
  protected Optional<String> resolveAnalyticsType() {
    return Optional.of(module);
  }

  public List<String> getChildProjectNames() {
    return new ArrayList<>(getProject().getChildProjects().keySet());
  }
}
