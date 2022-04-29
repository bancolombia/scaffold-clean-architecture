package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.factory.upgrades.actions.UpdateDependencies.DEPENDENCIES_TO_UPDATE;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import co.com.bancolombia.adapters.RestService;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import co.com.bancolombia.models.DependencyRelease;
import java.io.IOException;
import java.nio.file.Files;
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
    doReturn(false).when(builder).isKotlin();
    updater = new UpdateDependencies(restService);
    assertNotNull(updater.name());
    assertNotNull(updater.description());
    builder.addFile("./build.gradle", "");
    builder.addFile("./applications/app-service/build.gradle", "");
    builder.addFile("./domain/model/build.gradle", "");
    builder.addFile("./domain/usecase/build.gradle", "");
  }

  @Test
  public void shouldUpdateSpecific() {
    // Arrange
    String dependency = "group:dependency-name:1.2.3";
    DependencyRelease release = new DependencyRelease();
    release.setGroup("group");
    release.setArtifact("dependency-name");
    release.setVersion("1.2.4");
    String current = "dependencies {\n\timplementation '" + dependency + "'\n}";
    String expected = "dependencies {\n\timplementation 'group:dependency-name:1.2.4'\n}";
    when(builder.getParam(DEPENDENCIES_TO_UPDATE)).thenReturn(Set.of(dependency));
    when(restService.getTheLastDependencyRelease(dependency)).thenReturn(Optional.of(release));
    // add possible files
    builder.addFile("./main.gradle", current);

    // Act
    boolean applied = updater.up(builder);
    // Assert
    assertTrue(applied);
    verify(builder).addFile("./main.gradle", expected);
  }

  @Test
  public void shouldUpdateAll() {
    // Arrange
    DependencyRelease release = new DependencyRelease();
    release.setGroup("group");
    release.setArtifact("dependency-name");
    release.setVersion("1.2.4");
    String current = "dependencies {\n\timplementation 'group:dependency-name:1.2.3'\n}";
    String expected = "dependencies {\n\timplementation 'group:dependency-name:1.2.4'\n}";
    when(builder.getParam(DEPENDENCIES_TO_UPDATE)).thenReturn(Set.of());
    when(restService.getTheLastDependencyRelease(anyString())).thenReturn(Optional.of(release));
    // add possible files
    builder.addFile("./main.gradle", current);
    // Act
    boolean applied = updater.up(builder);
    // Assert
    assertTrue(applied);
    verify(builder).addFile("./main.gradle", expected);
  }
}
