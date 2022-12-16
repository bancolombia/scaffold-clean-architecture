package co.com.bancolombia;

import org.gradle.api.provider.Property;

public interface ModelProps {
  Property<String> getWhitelistedDependencies();
}
