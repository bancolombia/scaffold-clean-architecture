package co.com.bancolombia.plugins;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.tasks.testing.junitplatform.JUnitPlatformOptions;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

class ChildConventionPluginTest {

  @Test
  void shouldApplyCorePluginsAndReactiveDependenciesWhenEnabled() {
    // Arrange
    Project root = ProjectBuilder.builder().withName("root").build();
    root.getExtensions().getExtraProperties().set("reactive", true);
    root.getExtensions().getExtraProperties().set("lombok", true);
    root.getExtensions().getExtraProperties().set("javaVersion", 21);

    Project child = ProjectBuilder.builder().withName("usecase").withParent(root).build();

    // Act
    child.getPluginManager().apply(ChildConventionPlugin.class);

    // Assert
    assertTrue(child.getPluginManager().hasPlugin("java"));
    assertTrue(child.getPluginManager().hasPlugin("jacoco"));

    Set<String> implementationDependencies =
        child.getConfigurations().getByName("implementation").getDependencies().stream()
            .map(Dependency::getName)
            .collect(Collectors.toSet());
    assertTrue(implementationDependencies.contains("reactor-core"));
    assertTrue(implementationDependencies.contains("reactor-extra"));

    Set<String> compileOnlyDependencies =
        child.getConfigurations().getByName("compileOnly").getDependencies().stream()
            .map(Dependency::getName)
            .collect(Collectors.toSet());
    assertTrue(compileOnlyDependencies.contains("lombok"));
  }

  @Test
  void shouldConfigureTestTasksWithJUnitPlatformAndJvmArguments() {
    // Arrange
    Project child = ProjectBuilder.builder().withName("usecase").build();
    child.getExtensions().getExtraProperties().set("javaVersion", 21);

    // Act
    child.getPluginManager().apply(ChildConventionPlugin.class);

    // Assert
    org.gradle.api.tasks.testing.Test test =
        (org.gradle.api.tasks.testing.Test) child.getTasks().getByName("test");
    assertInstanceOf(JUnitPlatformOptions.class, test.getOptions());
    assertTrue(
        test.getJvmArgs()
            .containsAll(
                List.of(
                    "-XX:+AllowRedefinitionToAddDeleteMethods", "-XX:+EnableDynamicAgentLoading")));
    assertEquals("true", test.getSystemProperties().get("jdk.attach.allowAttachSelf"));
  }
}
