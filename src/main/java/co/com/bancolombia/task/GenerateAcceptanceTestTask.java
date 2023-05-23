package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.utils.Utils;
import java.io.IOException;
import java.util.List;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.options.OptionValues;

public class GenerateAcceptanceTestTask extends CleanArchitectureDefaultTask {
  private String name = "acceptanceTest";
  private String type = "karate";

  @Option(option = "name", description = "Set acceptance Test project name")
  public void setName(String projectName) {
    this.name = projectName;
  }

  @Option(option = "type", description = "Set acceptance Test project type")
  public void setType(String type) {
    this.type = type;
  }

  @OptionValues("type")
  public List<String> getTypes() {
    return super.resolveTypes();
  }

  @TaskAction
  public void generateAcceptanceTestTask() throws IOException, CleanException {
    ModuleFactory moduleFactory = resolveFactory(type);
    long start = System.currentTimeMillis();
    logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
    logger.lifecycle("AcceptanceTest name: {}", name);
    builder.addParam("acceptanceTestPath", name);
    moduleFactory.buildModule(builder);
    builder.persist();
    sendAnalytics(System.currentTimeMillis() - start);
  }

  @Override
  protected String resolvePrefix() {
    return "AcceptanceTest";
  }

  @Override
  protected String resolvePackage() {
    return "co.com.bancolombia.factory.tests.acceptance";
  }
}
