package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.utils.Utils;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.options.OptionValues;

public abstract class AbstractResolvableTypeTask extends AbstractCleanArchitectureDefaultTask {
  protected String name;
  protected String type;

  @Option(option = "name", description = "Set name")
  public void setName(String name) {
    this.name = name;
  }

  @Option(option = "type", description = "Set type")
  public void setType(String type) {
    this.type = type;
  }

  @OptionValues("type")
  public List<String> getTypes() {
    return super.resolveTypes();
  }

  @Override
  public void execute() throws IOException, CleanException {
    type = type == null ? defaultType() : type;
    name = name == null ? defaultName() : name;
    if (type == null) {
      printHelp();
      throw new IllegalArgumentException(
          "No "
              + resolvePrefix()
              + " type is set, usage: gradle "
              + getName()
              + " --type "
              + Utils.formatTaskOptions(getTypes()));
    }
    ModuleFactory moduleFactory = resolveFactory(type);
    logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
    logger.lifecycle("{} name: {}", resolvePrefix(), name);
    builder.addParam("task-param-name", name);
    builder.addParam("type", type);
    prepareParams();
    moduleFactory.buildModule(builder);
    builder.persist();
  }

  protected abstract void prepareParams();

  protected String defaultType() {
    return null;
  }

  protected String defaultName() {
    return null;
  }

  @Override
  protected Optional<String> resolveAnalyticsType() {
    return Optional.of(type);
  }
}
