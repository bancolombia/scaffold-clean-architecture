package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.task.annotations.CATask;
import java.io.IOException;
import java.util.ArrayList;

@CATask(
    name = "generateAcceptanceTest",
    shortcut = "gat",
    description = "Generate subproject by karate framework in deployment layer")
public class GenerateAcceptanceTestTask extends AbstractResolvableTypeTask {

  @Override
  public void execute() throws IOException, CleanException {
    super.execute();
    builder.runTask(
        "wrapper", getProject().getProjectDir().getPath().concat("/deployment/" + name));
  }

  @Override
  protected void prepareParams() {
    var modules = new ArrayList<>(getProject().getChildProjects().keySet());

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
