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
    name = "generatePerformanceTest",
    shortcut = "gpt",
    description = "Generate performance test")
public class GeneratePerformanceTestTask extends AbstractCleanArchitectureDefaultTask {
  private String type;

  @Option(option = "type", description = "Set type of performance test to be generated")
  public void setType(String type) {
    this.type = type;
  }

  @OptionValues("type")
  public List<String> getTypes() {
    return super.resolveTypes();
  }

  @Override
  public void execute() throws CleanException, IOException {
    if (type == null) {
      printHelp();
      throw new IllegalArgumentException(
          "No Performance test type was set, usage: gradle generatePerformanceTest --type "
              + Utils.formatTaskOptions(getTypes()));
    }
    ModuleFactory moduleFactory = resolveFactory(type);
    logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
    logger.lifecycle("PerformanceTest type: {}", "type");

    moduleFactory.buildModule(builder);
    builder.persist();
  }

  @Override
  protected String resolvePrefix() {
    return "PerformanceTest";
  }

  @Override
  protected String resolvePackage() {
    return "co.com.bancolombia.factory.tests.performance";
  }

  @Override
  protected Optional<String> resolveAnalyticsType() {
    return Optional.of(type);
  }
}
