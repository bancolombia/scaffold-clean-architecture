package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.MAIN_GRADLE;
import static co.com.bancolombia.Constants.MainFiles.SETTINGS_GRADLE;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import co.com.bancolombia.utils.FileUtils;
import com.github.mustachejava.resolver.DefaultResolver;
import java.io.IOException;
import java.nio.file.Files;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpgradeY2025M03D08GradleTest {

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
    updater = new UpgradeY2025M03D08Gradle();

    assertNotNull(updater.name());
    assertNotNull(updater.description());
  }

  @Test
  void shouldApplyUpdate() throws IOException {
    DefaultResolver resolver = new DefaultResolver();
    // Arrange
    builder.addFile(
        MAIN_GRADLE, FileUtils.getResourceAsString(resolver, "gradle-8.13-sample/main-before.txt"));
    builder.addFile(
        SETTINGS_GRADLE,
        FileUtils.getResourceAsString(resolver, "gradle-8.13-sample/settings-before.txt"));
    // Act
    boolean applied = updater.up(builder);
    // Assert
    assertTrue(applied);
    verify(builder, atLeast(1))
        .addFile(
            MAIN_GRADLE,
            FileUtils.getResourceAsString(resolver, "gradle-8.13-sample/main-after.txt"));
    verify(builder, atLeast(1))
        .addFile(
            SETTINGS_GRADLE,
            FileUtils.getResourceAsString(resolver, "gradle-8.13-sample/settings-after.txt"));
  }

  @Test
  void shouldNotApplyUpdateIfUrlIsUpdatedExists() throws IOException {
    DefaultResolver resolver = new DefaultResolver();
    // Arrange
    builder.addFile(
        MAIN_GRADLE, FileUtils.getResourceAsString(resolver, "gradle-8.13-sample/main-after.txt"));
    builder.addFile(
        SETTINGS_GRADLE,
        FileUtils.getResourceAsString(resolver, "gradle-8.13-sample/settings-after.txt"));
    // Act
    boolean applied = updater.up(builder);
    // Assert
    assertFalse(applied);
    verify(builder, atLeast(1))
        .addFile(
            MAIN_GRADLE,
            FileUtils.getResourceAsString(resolver, "gradle-8.13-sample/main-after.txt"));
    verify(builder, atLeast(1))
        .addFile(
            SETTINGS_GRADLE,
            FileUtils.getResourceAsString(resolver, "gradle-8.13-sample/settings-after.txt"));
  }
}
