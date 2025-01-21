package co.com.bancolombia.task;

import co.com.bancolombia.task.annotations.CATask;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.options.OptionValues;

import java.util.Arrays;
import java.util.List;

@CATask(
    name = "generateAcceptanceTest",
    shortcut = "gat",
    description = "Generate subproject by karate framework in deployment layer")
public class GenerateAcceptanceTestTask extends AbstractResolvableTypeTask {

  private BooleanOption toEntryPoint = BooleanOption.FALSE;

  @Option(option = "to-entry-point", description = "Set acceptance test to entry point")
  public void setToEntryPoint(BooleanOption toEntryPoint) {
    this.toEntryPoint = toEntryPoint;
  }

  @OptionValues("to-entry-point")
  public List<BooleanOption> getToEntryPointOptions() {
    return Arrays.asList(BooleanOption.values());
  }

  @Override
  protected void prepareParams() {
    builder.addParam("task-param-to-entry-point", toEntryPoint == BooleanOption.TRUE);
    builder.addParam("acceptanceTestPath", name);
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
  protected String defaultName() {
    return "acceptanceTest";
  }

  @Override
  protected String defaultType() {
    return "karate";
  }
}
