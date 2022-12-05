package co.com.bancolombia;

import org.gradle.api.Action;
import org.gradle.api.tasks.Nested;

abstract class CleanPluginExtension {

  public CleanPluginExtension() {}

  @Nested
  public abstract ModelProps getModelProps();

  public void modelProps(Action<? super ModelProps> action) {
    action.execute(getModelProps());
  }
}
