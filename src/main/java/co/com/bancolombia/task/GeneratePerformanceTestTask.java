package co.com.bancolombia.task;

import co.com.bancolombia.task.annotations.CATask;
import java.util.ArrayList;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Input;
import org.gradle.work.DisableCachingByDefault;

@CATask(
    name = "generatePerformanceTest",
    shortcut = "gpt",
    description = "Generate performance test")
@DisableCachingByDefault(because = "Generates and modifies project files, it has side effects")
public abstract class GeneratePerformanceTestTask extends AbstractResolvableTypeTask {
  @Input
  public abstract SetProperty<String> getModuleNames();

  public GeneratePerformanceTestTask() {
    getModuleNames().set(new ArrayList<>(childProjectDirs.keySet()));
  }

  @Override
  protected void prepareParams() {
    var modules = new ArrayList<>(getModuleNames().get());

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
