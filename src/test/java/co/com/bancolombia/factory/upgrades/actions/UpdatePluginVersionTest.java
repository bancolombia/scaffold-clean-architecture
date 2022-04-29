package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.BUILD_GRADLE;
import static co.com.bancolombia.Constants.MainFiles.GRADLE_PROPERTIES;
import static org.gradle.internal.impldep.org.testng.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import co.com.bancolombia.models.Release;
import co.com.bancolombia.utils.Utils;
import java.io.IOException;
import java.nio.file.Files;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UpdatePluginVersionTest {

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
    updater = new UpdatePluginVersion();
    assertNotNull(updater.name());
    assertNotNull(updater.description());
  }

  @Test
  public void shouldNotUpdateWhenErrorGettingLatestVersion() {
    // Arrange
    when(builder.getLatestRelease()).thenReturn(null);
    // Act
    boolean applied = updater.up(builder);
    // Assert
    assertFalse(applied);
  }

  @Test
  public void shouldNotUpdateBecauseTheCurrentVersion() {
    // Arrange
    Release release = new Release();
    release.setTagName(Utils.getVersionPlugin());
    when(builder.getLatestRelease()).thenReturn(release);
    // Act
    boolean applied = updater.up(builder);
    // Assert
    assertFalse(applied);
  }

  @Test
  public void shouldUpdate() {
    // Arrange
    Release release = new Release();
    release.setTagName(Utils.getVersionPlugin() + ".1");
    doReturn(release).when(builder).getLatestRelease();
    doReturn(false).when(builder).isKotlin();
    builder.addFile(BUILD_GRADLE, "\t\tcleanArchitectureVersion = '2.2.4'\n");
    builder.addFile(GRADLE_PROPERTIES, "systemProp.version=2.2.4");
    // Act
    boolean applied = updater.up(builder);
    // Assert
    assertTrue(applied);
    verify(builder, times(2))
        .addFile(BUILD_GRADLE, "\t\tcleanArchitectureVersion = '" + release.getTagName() + "'\n");
    verify(builder).addFile(GRADLE_PROPERTIES, "systemProp.version=" + release.getTagName());
  }

  @Test
  public void shouldUpdateWithQuotationMarks() {
    // Arrange
    Release release = new Release();
    release.setTagName(Utils.getVersionPlugin() + ".1");
    doReturn(release).when(builder).getLatestRelease();
    doReturn(false).when(builder).isKotlin();
    builder.addFile(BUILD_GRADLE, "\t\tcleanArchitectureVersion = \"2.2.4\"\n");
    builder.addFile(GRADLE_PROPERTIES, "systemProp.version=2.2.4");
    // Act
    boolean applied = updater.up(builder);
    // Assert
    assertTrue(applied);
    verify(builder)
        .addFile(BUILD_GRADLE, "\t\tcleanArchitectureVersion = '" + release.getTagName() + "'\n");
  }
}
