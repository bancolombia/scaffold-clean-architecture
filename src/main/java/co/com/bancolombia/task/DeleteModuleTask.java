package co.com.bancolombia.task;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

import co.com.bancolombia.utils.Utils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.options.OptionValues;

public class DeleteModuleTask extends CleanArchitectureDefaultTask {
  private String module;

  @Option(option = "module", description = "Set module name to delete")
  public void setModule(String module) {
    this.module = module;
  }

  @OptionValues("module")
  public List<String> getModules() {
    return new ArrayList<>(getProject().getChildProjects().keySet());
  }

  @TaskAction
  public void deleteModule() throws IOException {
    long start = System.currentTimeMillis();
    if (module == null || !getProject().getChildProjects().containsKey(module)) {
      printHelp();
      throw new IllegalArgumentException(
          "No valid module name is set, usage: gradle deleteModule --module "
              + Utils.formatTaskOptions(getModules()));
    }
    String dependency = buildImplementationFromProject(builder.isKotlin(), ":" + module);
    builder.deleteModule(module);
    builder.removeFromSettings(module);
    builder.removeDependencyFromModule(APP_SERVICE, dependency);
    builder.persist();
    sendAnalytics(module, System.currentTimeMillis() - start);
  }
}
