package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.factory.upgrades.actions.UpdateDependencies.DEPENDENCIES_TO_UPDATE;
import static co.com.bancolombia.factory.upgrades.actions.UpdateDependencies.FILES_TO_UPDATE;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import co.com.bancolombia.adapters.RestService;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import co.com.bancolombia.models.DependencyRelease;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UpdateDependenciesTest {

  @Mock private RestService restService;
  @Mock private Project project;
  @Mock private Logger logger;
  private ModuleBuilder builder;
  private UpgradeAction updater;

  @Before
  public void setup() throws IOException {
    when(project.getName()).thenReturn("UtilsTest");
    when(project.getLogger()).thenReturn(logger);
    when(project.getProjectDir()).thenReturn(Files.createTempDirectory("sample").toFile());
    builder = spy(new ModuleBuilder(project));
    updater = new UpdateDependencies(restService);
    assertNotNull(updater.name());
    assertNotNull(updater.description());
  }

  @Test
  public void shouldUpdateSpecific() {
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
    when(restService.getTheLastDependencyRelease(any(DependencyRelease.class)))
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
  public void shouldUpdateAll() {
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
    when(restService.getTheLastDependencyRelease(any(DependencyRelease.class)))
        .thenReturn(Optional.of(release));
    // add possible files
    builder.addFile(file, current);
    // Act
    boolean applied = updater.up(builder);
    // Assert
    assertTrue(applied);
    verify(builder).addFile(file, expected);
  }
}
