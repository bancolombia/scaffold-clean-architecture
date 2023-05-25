package co.com.bancolombia.task;

import co.com.bancolombia.task.annotations.CATask;

@CATask(
    name = "generateAcceptanceTest",
    shortcut = "gat",
    description = "Generate subproject by karate framework in deployment layer")
public class GenerateAcceptanceTestTask extends AbstractResolvableTypeTask {

  @Override
  protected void prepareParams() {
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
