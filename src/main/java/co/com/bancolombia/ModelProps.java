package co.com.bancolombia;

import org.gradle.api.provider.Property;

public abstract class ModelProps {
  public abstract Property<String> getWhitelistedDependencies();
}
