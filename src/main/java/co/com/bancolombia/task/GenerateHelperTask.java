package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.helpers.ModuleFactoryHelpers;
import co.com.bancolombia.utils.Utils;
import java.io.IOException;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

public class GenerateHelperTask extends CleanArchitectureDefaultTask {
  private String name;

  @Option(option = "name", description = "Set driven adapter name")
  public void setName(String name) {
    this.name = name;
  }

  @TaskAction
  public void generateHelperTask() throws IOException, CleanException {
    long start = System.currentTimeMillis();
    ModuleFactory moduleFactory = ModuleFactoryHelpers.getDrivenAdapterFactory();
    logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
    logger.lifecycle("Helper name: {}", name);
    builder.addParam("task-param-name", name);
    builder.addParam("lombok", builder.isEnableLombok());
    builder.addParam("metrics", builder.withMetrics());
    moduleFactory.buildModule(builder);
    builder.persist();
    sendAnalytics(name, System.currentTimeMillis() - start);
  }
}
