package co.com.bancolombia;

import org.gradle.api.Action;
import org.gradle.api.tasks.Nested;

public interface CleanPluginExtension {

  @Nested
  ModelProps getModelProps();

  default void modelProps(Action<? super ModelProps> action) {
    action.execute(getModelProps());
  }
}
