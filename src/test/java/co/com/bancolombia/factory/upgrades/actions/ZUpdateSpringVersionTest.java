package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.BUILD_GRADLE;
import static org.gradle.internal.impldep.org.testng.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import co.com.bancolombia.Constants;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
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
public class ZUpdateSpringVersionTest {

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
    updater = new ZUpdateSpringVersion();
    assertNotNull(updater.name());
    assertNotNull(updater.description());
  }

  @Test
  public void shouldUpdate() {
    // Arrange
    builder.addFile(BUILD_GRADLE, "\t\tspringBootVersion = '2.2.4'\n");
    // Act
    boolean applied = updater.up(builder);
    // Assert
    assertTrue(applied);
    verify(builder, times(2))
        .addFile(BUILD_GRADLE, "\t\tspringBootVersion = '" + Constants.SPRING_BOOT_VERSION + "'\n");
  }

  @Test
  public void shouldUpdateWithQuotationMarks() {
    // Arrange
    builder.addFile(BUILD_GRADLE, "\t\tspringBootVersion = \"2.2.4\"\n");
    // Act
    boolean applied = updater.up(builder);
    // Assert
    assertTrue(applied);
    verify(builder)
        .addFile(BUILD_GRADLE, "\t\tspringBootVersion = '" + Constants.SPRING_BOOT_VERSION + "'\n");
  }
}
