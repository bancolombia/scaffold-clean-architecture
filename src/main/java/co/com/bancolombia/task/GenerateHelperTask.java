package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.task.annotations.CATask;
import co.com.bancolombia.utils.Utils;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.options.OptionValues;

@CATask(
    name = "generateHelper",
    shortcut = "gh",
    description = "Generate helper in infrastructure layer")
public class GenerateHelperTask extends AbstractCleanArchitectureDefaultTask {
  private String name;
  private String type = "generic";

  @Option(option = "name", description = "Set helper name")
  public void setName(String name) {
    this.name = name;
  }

  @Option(option = "type", description = "Helper type")
  public void setType(String type) {
    this.type = type;
  }

  @OptionValues("type")
  public List<String> getTypes() {
    return super.resolveTypes();
  }

  @Override
  public void execute() throws IOException, CleanException {
    ModuleFactory moduleFactory = resolveFactory(type);
    logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
    logger.lifecycle("Helper name: {}", name);
    builder.addParam("task-param-name", name);
    builder.addParam("lombok", builder.isEnableLombok());
    builder.addParam("metrics", builder.withMetrics());
    moduleFactory.buildModule(builder);
    builder.persist();
  }

  @Override
  protected String resolvePrefix() {
    return "Helper";
  }

  @Override
  protected String resolvePackage() {
    return "co.com.bancolombia.factory.helpers";
  }

  @Override
  protected Optional<String> resolveAnalyticsType() {
    return Optional.of(type);
  }
}
