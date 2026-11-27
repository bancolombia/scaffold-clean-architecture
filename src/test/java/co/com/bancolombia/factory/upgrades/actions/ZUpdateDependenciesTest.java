package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.factory.upgrades.actions.ZUpdateDependencies.DEPENDENCIES_TO_UPDATE;
import static co.com.bancolombia.factory.upgrades.actions.ZUpdateDependencies.FILES_TO_UPDATE;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import co.com.bancolombia.models.DependencyRelease;
import co.com.bancolombia.utils.operations.ExternalOperations;
import co.com.bancolombia.utils.operations.LookupResult;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
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
    lenient().when(operations.findLatestPluginVersion()).thenReturn(LookupResult.notFound("test"));
    lenient().when(operations.findLatestGradleVersion()).thenReturn(LookupResult.notFound("test"));
    lenient()
        .when(operations.findGradleDistributionSha256(anyString(), anyString()))
        .thenReturn(LookupResult.notFound("test"));
    lenient()
        .when(operations.findLatestDependency(any()))
        .thenReturn(LookupResult.notFound("test"));
    lenient()
        .when(operations.findLatestGradlePlugin(any()))
        .thenReturn(LookupResult.notFound("test"));
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
    when(operations.findLatestDependency(any(DependencyRelease.class)))
        .thenReturn(LookupResult.found(release));
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
    when(operations.findLatestDependency(any(DependencyRelease.class)))
        .thenReturn(LookupResult.found(release));
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
    when(operations.findLatestGradleVersion()).thenReturn(LookupResult.found("9.9.0"));
    when(operations.findGradleDistributionSha256("9.9.0", "bin"))
        .thenReturn(LookupResult.found("c".repeat(64)));
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
    verify(operations, never()).findLatestGradleVersion();
  }

  @Test
  void shouldWriteVersionWithDollarSignLiterally() {
    DependencyRelease release = new DependencyRelease();
    release.setGroup("group");
    release.setArtifact("dependency-name");
    release.setVersion("999.2.4$1");
    String file = "./main.gradle";
    String current = "dependencies {\n\timplementation 'group:dependency-name:1.2.3'\n}";
    String expected = "dependencies {\n\timplementation 'group:dependency-name:999.2.4$1'\n}";
    when(builder.getParam(DEPENDENCIES_TO_UPDATE)).thenReturn(Set.of());
    when(builder.getParam(FILES_TO_UPDATE)).thenReturn(List.of(file));
    when(operations.findLatestDependency(any(DependencyRelease.class)))
        .thenReturn(LookupResult.found(release));
    builder.addFile(file, current);

    assertTrue(updater.up(builder));
    verify(builder).addFile(file, expected);
  }

  @Test
  void shouldUpdateGradlePluginsWithAVersionButNotThePluginsOfThisProject() {
    String file = "./build.gradle";
    String current =
        """
        plugins {
            id 'co.com.bancolombia.cleanArchitecture.settings' version '5.4.0'
            id 'org.springframework.boot' version '4.1.2' apply false
            id 'org.sonarqube'
        }
        """;
    DependencyRelease boot = DependencyRelease.from("id org.springframework.boot version 4.2.0");
    when(builder.getParam(DEPENDENCIES_TO_UPDATE)).thenReturn(Set.of());
    when(builder.getParam(FILES_TO_UPDATE)).thenReturn(List.of(file));
    when(operations.findLatestGradlePlugin(any(DependencyRelease.class)))
        .thenReturn(LookupResult.found(boot));
    builder.addFile(file, current);

    assertTrue(updater.up(builder));

    verify(builder)
        .addFile(
            file, current.replace("version '4.1.2' apply false", "version '4.2.0' apply false"));
    verify(operations).findLatestGradlePlugin(any(DependencyRelease.class));
  }

  @Test
  void shouldUpdateTheOlderDeclarationOfAnArtifactDeclaredInSeveralFiles() {
    String newerFile = "./a.gradle";
    String olderFile = "./b.gradle";
    String newer = "dependencies {\n\timplementation 'group:artifact:1.5.0'\n}";
    String older = "dependencies {\n\timplementation 'group:artifact:1.0.0'\n}";
    when(builder.getParam(DEPENDENCIES_TO_UPDATE)).thenReturn(Set.of());
    when(builder.getParam(FILES_TO_UPDATE)).thenReturn(List.of(newerFile, olderFile));
    when(operations.findLatestDependency(any(DependencyRelease.class)))
        .thenReturn(LookupResult.found(DependencyRelease.from("group:artifact:1.5.0")));
    builder.addFile(newerFile, newer);
    builder.addFile(olderFile, older);

    assertTrue(updater.up(builder));

    verify(builder).addFile(olderFile, newer);
    verify(operations).findLatestDependency(any(DependencyRelease.class));
  }
}
