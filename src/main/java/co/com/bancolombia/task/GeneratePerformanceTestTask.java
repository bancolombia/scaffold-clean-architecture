package co.com.bancolombia.task;

import co.com.bancolombia.task.annotations.CATask;
import java.util.ArrayList;
import lombok.Getter;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Input;

@CATask(
    name = "generatePerformanceTest",
    shortcut = "gpt",
    description = "Generate performance test")
public class GeneratePerformanceTestTask extends AbstractResolvableTypeTask {
  @Input @Getter private final SetProperty<String> moduleNames;

  public GeneratePerformanceTestTask() {
    this.moduleNames = getProject().getObjects().setProperty(String.class);
    this.moduleNames.set(getProject().getChildProjects().keySet());
  }

  @Override
  protected void prepareParams() {
    var modules = new ArrayList<>(moduleNames.get());

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
