package co.com.bancolombia.task;

import co.com.bancolombia.task.annotations.CATask;
import org.gradle.work.DisableCachingByDefault;

@CATask(
    name = "generateHelper",
    shortcut = "gh",
    description = "Generate helper in infrastructure layer")
@DisableCachingByDefault(because = "Generates and modifies project files, it has side effects")
public class GenerateHelperTask extends AbstractResolvableTypeTask {

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
