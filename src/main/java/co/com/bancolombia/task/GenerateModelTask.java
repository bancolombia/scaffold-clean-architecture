package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.ParamNotFoundException;
import co.com.bancolombia.task.annotations.CATask;
import co.com.bancolombia.utils.Utils;
import java.io.IOException;
import java.util.Optional;
import org.gradle.api.tasks.options.Option;

@CATask(name = "generateModel", shortcut = "gm", description = "Generate model in domain layer")
public class GenerateModelTask extends AbstractCleanArchitectureDefaultTask {
  private String name = "";

  @Option(option = "name", description = "Set the model name")
  public void setName(String modelName) {
    this.name = modelName;
  }

  @Override
  public void execute() throws IOException, ParamNotFoundException {
    if (name == null || name.isEmpty()) {
      printHelp();
      throw new IllegalArgumentException(
          "No model name, usage: gradle generateModel --name [name]");
    }
    name = Utils.capitalize(name);
    logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
    logger.lifecycle("Model Name: {}", name);
    builder.addParam("modelName", name.toLowerCase());
    builder.addParam("modelClassName", name);

    builder.setupFromTemplate("model");
    builder.persist();
  }

  @Override
  protected Optional<String> resolveAnalyticsType() {
    return Optional.of(name);
  }
}
