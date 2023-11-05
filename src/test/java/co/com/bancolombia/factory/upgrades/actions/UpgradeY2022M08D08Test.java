package co.com.bancolombia.factory.upgrades.actions;

import static org.gradle.internal.impldep.org.testng.Assert.assertNotNull;
import static org.gradle.internal.impldep.org.testng.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.com.bancolombia.Constants;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UpgradeY2022M08D08Test {
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
    updater = new UpgradeY2022M08D08();
    assertNotNull(updater.name());
    assertNotNull(updater.description());
  }

  @Test
  public void shouldApplyUpdate() throws IOException {
    // Arrange
    builder.addFile(
        Constants.MainFiles.MAIN_GRADLE, readResourceFile("gradle-8-sample/8.0/main/before.txt"));
    // Act
    boolean applied = updater.up(builder);
    // Assert
    assertTrue(applied);
    verify(builder, times(1))
        .addFile(
            Constants.MainFiles.MAIN_GRADLE,
            readResourceFile("gradle-8-sample/8.0/main/after.txt"));
  }

  private String readResourceFile(String location) throws IOException {
    ClassLoader classLoader = getClass().getClassLoader();
    return Files.readString(
        Path.of(Objects.requireNonNull(classLoader.getResource(location)).getPath()));
  }
}
