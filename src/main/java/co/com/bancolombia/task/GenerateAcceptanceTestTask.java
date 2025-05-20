package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.task.annotations.CATask;
import java.io.IOException;
import java.util.ArrayList;
import lombok.Getter;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;

@CATask(
    name = "generateAcceptanceTest",
    shortcut = "gat",
    description = "Generate subproject by karate framework in deployment layer")
public class GenerateAcceptanceTestTask extends AbstractResolvableTypeTask {

  @Internal @Getter private final Property<String> projectPath;
  @Input @Getter private final SetProperty<String> moduleNames;

  public GenerateAcceptanceTestTask() {
    notCompatibleWithConfigurationCache("This task performs validations that should always run");

    this.projectPath = getProject().getObjects().property(String.class);
    this.projectPath.set(getProject().getProjectDir().getPath());

    this.moduleNames = getProject().getObjects().setProperty(String.class);
    this.moduleNames.set(getProject().getChildProjects().keySet());
  }

  @Override
  public void execute() throws IOException, CleanException {
    super.execute();
    builder.runTask("wrapper", projectPath.get().concat("/deployment/" + name));
  }

  @Override
  protected void prepareParams() {
    var modules = new ArrayList<>(moduleNames.get());

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
