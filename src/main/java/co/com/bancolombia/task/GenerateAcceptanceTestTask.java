package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.tests.ModuleFactoryTests;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.utils.Utils;
import java.io.IOException;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

public class GenerateAcceptanceTestTask extends CleanArchitectureDefaultTask {
  private String name;

  @Option(option = "name", description = "Set driven adapter name")
  public void setName(String name) {
    this.name = name;
  }

  @TaskAction
  public void generateAcceptanceTestTask() throws IOException, CleanException {
    ModuleFactory moduleFactory = ModuleFactoryTests.getTestsFactory();
    logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
    logger.lifecycle("AcceptanceTest name: {}", name);
    builder.addParam("acceptanceTestPath", name);
    moduleFactory.buildModule(builder);
    builder.persist();
  }
}
