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
    name = "generateAcceptanceTest",
    shortcut = "gat",
    description = "Generate subproject by karate framework in deployment layer")
public class GenerateAcceptanceTestTask extends AbstractCleanArchitectureDefaultTask {
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

  @Override
  public void execute() throws IOException, CleanException {
    ModuleFactory moduleFactory = resolveFactory(type);
    logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
    logger.lifecycle("AcceptanceTest name: {}", name);
    builder.addParam("acceptanceTestPath", name);
    moduleFactory.buildModule(builder);
    builder.persist();
  }

  @Override
  protected String resolvePrefix() {
    return "AcceptanceTest";
  }

  @Override
  protected String resolvePackage() {
    return "co.com.bancolombia.factory.tests.acceptance";
  }

  @Override
  protected Optional<String> resolveAnalyticsType() {
    return Optional.of(type);
  }
}
