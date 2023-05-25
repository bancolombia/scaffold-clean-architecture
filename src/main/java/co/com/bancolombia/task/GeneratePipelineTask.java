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
    name = "generatePipeline",
    shortcut = "gpl",
    description = "Generate CI pipeline as a code in deployment layer")
public class GeneratePipelineTask extends AbstractCleanArchitectureDefaultTask {
  private String type;

  @Option(option = "type", description = "Set type of pipeline to be generated")
  public void setType(String type) {
    this.type = type;
  }

  @OptionValues("type")
  public List<String> getTypes() {
    return super.resolveTypes();
  }

  @Override
  public void execute() throws IOException, CleanException {
    if (type == null) {
      printHelp();
      throw new IllegalArgumentException(
          "No Pipeline type was set, usage: gradle generatePipeline --type "
              + Utils.formatTaskOptions(getTypes()));
    }
    ModuleFactory pipelineFactory = resolveFactory(type);
    logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
    logger.lifecycle("Pipeline type: {}", type);
    pipelineFactory.buildModule(builder);
    builder.persist();
  }

  @Override
  protected String resolvePrefix() {
    return "Pipeline";
  }

  @Override
  protected String resolvePackage() {
    return "co.com.bancolombia.factory.pipelines";
  }

  @Override
  protected Optional<String> resolveAnalyticsType() {
    return Optional.of(type);
  }
}
