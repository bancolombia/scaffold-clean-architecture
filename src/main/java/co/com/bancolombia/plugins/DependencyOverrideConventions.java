package co.com.bancolombia.plugins;

import org.gradle.api.Project;

public class DependencyOverrideConventions {
  private static final String EXTENSION_NAME = "dependenciesOverride";

  /** Registers dependency version overrides for every project in the build. */
  public void configure(Project project) {
    DependenciesOverrideExtension extension =
        project.getExtensions().create(EXTENSION_NAME, DependenciesOverrideExtension.class);
    project.allprojects(
        subproject ->
            subproject
                .getConfigurations()
                .configureEach(
                    configuration ->
                        configuration.getResolutionStrategy().eachDependency(extension::applyTo)));
  }
}
