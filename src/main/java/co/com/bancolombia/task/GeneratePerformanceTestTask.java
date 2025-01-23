package co.com.bancolombia.task;

import co.com.bancolombia.task.annotations.CATask;
import java.util.ArrayList;

@CATask(
    name = "generatePerformanceTest",
    shortcut = "gpt",
    description = "Generate performance test")
public class GeneratePerformanceTestTask extends AbstractResolvableTypeTask {

  @Override
  protected void prepareParams() {
    var modules = new ArrayList<>(getProject().getChildProjects().keySet());

    builder.addParam(
        "task-param-exist-api-rest",
        modules.stream()
            .anyMatch(value -> value.equals("reactive-web") || value.equals("api-rest")));
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
