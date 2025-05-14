package co.com.bancolombia.task;

import co.com.bancolombia.task.annotations.CATask;

@CATask(
    name = "generateHelper",
    shortcut = "gh",
    description = "Generate helper in infrastructure layer")
public class GenerateHelperTask extends AbstractResolvableTypeTask {

  public GenerateHelperTask() {
    notCompatibleWithConfigurationCache("This task performs validations that should always run");
  }

  @Override
  protected void prepareParams() {
    // no additional params required
  }

  @Override
  protected String resolvePrefix() {
    return "Helper";
  }

  @Override
  protected String resolvePackage() {
    return "co.com.bancolombia.factory.helpers";
  }

  @Override
  protected String defaultType() {
    return "generic";
  }
}
