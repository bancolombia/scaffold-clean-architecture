package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.tests.performance.ModuleFactoryPerformanceTests;
import co.com.bancolombia.factory.tests.performance.ModuleFactoryPerformanceTests.PerformanceTestType;
import co.com.bancolombia.utils.Utils;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.options.OptionValues;

public class GeneratePerformanceTests extends CleanArchitectureDefaultTask {

  private PerformanceTestType type;

  @Option(option = "type", description = "Set type of performance test to be generated")
  public void setType(PerformanceTestType type) {
    this.type = type;
  }

  @OptionValues("type")
  public List<PerformanceTestType> getTypes() {
    return Arrays.asList(PerformanceTestType.values());
  }

  @TaskAction
  public void generateAcceptanceTestTask() throws CleanException, IOException {
    if (type == null) {
      printHelp();
      throw new IllegalArgumentException(
          "No Performance test type was set, usage: gradle generatePerformanceTest --type "
              + Utils.formatTaskOptions(getTypes()));
    }
    ModuleFactory moduleFactory = ModuleFactoryPerformanceTests.getPerformanceTestsFactory(type);
    logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
    logger.lifecycle("PerformanceTest type: {}", "type");

    moduleFactory.buildModule(builder);
    builder.persist();
  }
}
