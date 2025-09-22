package co.com.bancolombia.factory.upgrades.actions;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpgradeY2023M01D12Test {
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
    updater = new UpgradeY2023M01D12();
    assertNotNull(updater.name());
    assertNotNull(updater.description());
  }

  @Test
  void shouldApplyUpdate() throws IOException {
    // Arrange
    builder.addFile(
        Constants.MainFiles.SETTINGS_GRADLE, readResourceFile("settings-gradle-sample/before.txt"));
    // Act
    boolean applied = updater.up(builder);
    // Assert
    assertTrue(applied);
    verify(builder, times(1))
        .addFile(
            Constants.MainFiles.SETTINGS_GRADLE,
            readResourceFile("settings-gradle-sample/after.txt"));
  }

  private String readResourceFile(String location) throws IOException {
    ClassLoader classLoader = getClass().getClassLoader();
    return Files.readString(
        Path.of(Objects.requireNonNull(classLoader.getResource(location)).getPath()));
  }
}
