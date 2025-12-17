package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.GRADLE_PROPERTIES;
import static co.com.bancolombia.Constants.MainFiles.SETTINGS_GRADLE;
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
class UpgradeY2025M05D10GradleCacheTest {
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
    updater = new UpgradeY2025M05D10GradleCache();

    assertNotNull(updater.name());
    assertNotNull(updater.description());
  }

  @Test
  void shouldApplyUpdate() throws IOException {
    DefaultResolver resolver = new DefaultResolver();
    // Arrange
    builder.addFile(
        SETTINGS_GRADLE,
        FileUtils.getResourceAsString(resolver, "gradle-8.14-gradle-cache/settings-before.txt"));
    builder.addFile(
        GRADLE_PROPERTIES,
        FileUtils.getResourceAsString(
            resolver, "gradle-8.14-gradle-cache/gradle.properties-before.txt"));
    builder.addFile(
        "./.gitignore",
        FileUtils.getResourceAsString(resolver, "gradle-8.14-gradle-cache/.gitignore-before.txt"));
    // Act
    boolean applied = updater.up(builder);
    // Assert
    assertTrue(applied);
    verify(builder, atLeast(1))
        .addFile(
            SETTINGS_GRADLE,
            FileUtils.getResourceAsString(resolver, "gradle-8.14-gradle-cache/settings-after.txt"));
    verify(builder, atLeast(1))
        .addFile(
            GRADLE_PROPERTIES,
            FileUtils.getResourceAsString(
                resolver, "gradle-8.14-gradle-cache/gradle.properties-after.txt"));
    verify(builder, atLeast(1))
        .addFile(
            "./.gitignore",
            FileUtils.getResourceAsString(
                resolver, "gradle-8.14-gradle-cache/.gitignore-after.txt"));
  }
}
