package co.com.bancolombia.task;

import co.com.bancolombia.task.annotations.CATask;

@CATask(
    name = "generatePerformanceTest",
    shortcut = "gpt",
    description = "Generate performance test")
public class GeneratePerformanceTestTask extends AbstractResolvableTypeTask {

  @Override
  protected void prepareParams() {
    // No additional params required
  }

  @Override
  protected String resolvePrefix() {
    return "PerformanceTest";
  }

  @Override
  protected String resolvePackage() {
    return "co.com.bancolombia.factory.tests.performance";
  }
}
