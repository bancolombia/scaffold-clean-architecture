package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.task.annotations.CATask;
import java.io.IOException;
import java.util.ArrayList;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;

@CATask(
    name = "generateAcceptanceTest",
    shortcut = "gat",
    description = "Generate subproject by karate framework in deployment layer")
public abstract class GenerateAcceptanceTestTask extends AbstractResolvableTypeTask {

  @Internal
  public abstract Property<String> getProjectPath();

  @Input
  public abstract SetProperty<String> getModuleNames();

  public GenerateAcceptanceTestTask() {
    getProjectPath().set(projectDir.getAbsolutePath());
    getModuleNames().set(new ArrayList<>(childProjectDirs.keySet()));
  }

  @Override
  protected void doExecute() throws IOException, CleanException {
    super.doExecute();
    builder.runTask("wrapper", getProjectPath().get().concat("/deployment/" + name));
  }

  @Override
  protected void prepareParams() {
    var modules = new ArrayList<>(getModuleNames().get());

    builder.addParam(
        "task-param-exist-api-rest",
        modules.stream()
            .anyMatch(value -> value.equals("reactive-web") || value.equals("api-rest")));
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
