package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.factory.upgrades.actions.ZUpdateDependencies.DEPENDENCIES_TO_UPDATE;
import static co.com.bancolombia.factory.upgrades.actions.ZUpdateDependencies.FILES_TO_UPDATE;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import co.com.bancolombia.models.DependencyRelease;
import co.com.bancolombia.utils.operations.ExternalOperations;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ZUpdateDependenciesTest {

  @Mock private ExternalOperations operations;
  @Mock private Project project;
  @Mock private Logger logger;
  private ModuleBuilder builder;
  private UpgradeAction updater;

  @BeforeEach
  void setup() throws IOException {
    when(project.getName()).thenReturn("UtilsTest");
    when(project.getLogger()).thenReturn(logger);
    when(project.getProjectDir()).thenReturn(Files.createTempDirectory("sample").toFile());
    builder = spy(new ModuleBuilder(project));
    updater = new ZUpdateDependencies(operations);
    assertNotNull(updater.name());
    assertNotNull(updater.description());
  }

  @Test
  void shouldUpdateSpecific() {
    // Arrange
    String dependency = "group:dependency-name:1.2.3";
    DependencyRelease release = new DependencyRelease();
    release.setGroup("group");
    release.setArtifact("dependency-name");
    release.setVersion("1.2.4");
    String file = "./main.gradle";
    String current = "dependencies {\n\timplementation '" + dependency + "'\n}";
    String expected = "dependencies {\n\timplementation 'group:dependency-name:1.2.4'\n}";
    when(builder.getParam(DEPENDENCIES_TO_UPDATE)).thenReturn(Set.of(dependency));
    when(builder.getParam(FILES_TO_UPDATE)).thenReturn(List.of(file));
    when(operations.getTheLastDependencyRelease(any(DependencyRelease.class)))
        .thenReturn(Optional.of(release));
    // add possible files
    builder.addFile(file, current);

    // Act
    boolean applied = updater.up(builder);
    // Assert
    assertTrue(applied);
    verify(builder).addFile(file, expected);
  }

  @Test
  void shouldUpdateAll() {
    // Arrange
    DependencyRelease release = new DependencyRelease();
    release.setGroup("group");
    release.setArtifact("dependency-name");
    release.setVersion("1.2.4");
    String file = "./main.gradle";
    String current = "dependencies {\n\timplementation 'group:dependency-name:1.2.3'\n}";
    String expected = "dependencies {\n\timplementation 'group:dependency-name:1.2.4'\n}";
    when(builder.getParam(DEPENDENCIES_TO_UPDATE)).thenReturn(Set.of());
    when(builder.getParam(FILES_TO_UPDATE)).thenReturn(List.of(file));
    when(operations.getTheLastDependencyRelease(any(DependencyRelease.class)))
        .thenReturn(Optional.of(release));
    // add possible files
    builder.addFile(file, current);
    // Act
    boolean applied = updater.up(builder);
    // Assert
    assertTrue(applied);
    verify(builder).addFile(file, expected);
  }

  @Test
  void shouldUpdateGradleWrapperWhenUpdatingAll() {
    // Arrange
    String file = "./build.gradle";
    String current =
        """
        tasks.named('wrapper') {
            gradleVersion = '9.8.2'
            distributionSha256Sum = 'old'
        }
        """;
    String expected =
        """
        tasks.named('wrapper') {
            gradleVersion = '9.9.0'
            distributionSha256Sum = '%s'
        }
        """
            .formatted("c".repeat(64));
    when(builder.getParam(DEPENDENCIES_TO_UPDATE)).thenReturn(Set.of());
    when(builder.getParam(FILES_TO_UPDATE)).thenReturn(List.of(file));
    when(operations.getGradleWrapperFromFile()).thenReturn(Optional.of("9.9.0"));
    when(operations.getGradleDistributionSha256("9.9.0", "bin"))
        .thenReturn(Optional.of("c".repeat(64)));
    builder.addFile(file, current);
    // Act
    boolean applied = updater.up(builder);
    // Assert
    assertTrue(applied);
    verify(builder).addFile(file, expected);
  }

  @Test
  void shouldNotUpdateGradleWrapperWhenUpdatingSpecificDependencies() {
    // Arrange
    String file = "./build.gradle";
    when(builder.getParam(DEPENDENCIES_TO_UPDATE))
        .thenReturn(Set.of("group:dependency-name:1.2.3"));
    when(builder.getParam(FILES_TO_UPDATE)).thenReturn(List.of(file));
    builder.addFile(file, "tasks.named('wrapper') {\n    gradleVersion = '9.8.2'\n}\n");
    // Act
    boolean applied = updater.up(builder);
    // Assert
    assertFalse(applied);
    verify(operations, never()).getGradleWrapperFromFile();
  }
}
