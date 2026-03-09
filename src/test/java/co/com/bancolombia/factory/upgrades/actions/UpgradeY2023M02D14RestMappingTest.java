package co.com.bancolombia.factory.upgrades.actions;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import co.com.bancolombia.utils.FileUtils;
import com.github.mustachejava.resolver.DefaultResolver;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpgradeY2023M02D14RestMappingTest {
  @Mock private Project project;
  @Mock private Project childProject;
  @Mock private Logger logger;
  private ModuleBuilder builder;
  private UpgradeAction updater;
  private File tempDir;

  @BeforeEach
  void setup() throws IOException {
    tempDir = Files.createTempDirectory("sample").toFile();
    File childDir = new File(tempDir, "infrastructure/entry-points/api-rest");
    childDir.mkdirs();
    when(project.getName()).thenReturn("UtilsTest");
    when(project.getLogger()).thenReturn(logger);
    when(project.getProjectDir()).thenReturn(tempDir);
    when(childProject.getProjectDir()).thenReturn(childDir);
    when(childProject.getBuildFile()).thenReturn(new File(childDir, "build.gradle"));
    when(project.getChildProjects()).thenReturn(Map.of("api-rest", childProject));
    builder = spy(new ModuleBuilder(project));
    updater = new UpgradeY2023M02D14RestMapping();
    assertNotNull(updater.name());
    assertNotNull(updater.description());
  }

  @Test
  void shouldApplyUpdate() throws IOException {
    File javaFile = new File(tempDir, "infrastructure/entry-points/api-rest/Sample.java");
    Files.writeString(
        javaFile.toPath(),
        FileUtils.getResourceAsString(new DefaultResolver(), "rest/mapping-before.txt"));
    String file = javaFile.getAbsolutePath();
    // Arrange
    DefaultResolver resolver = new DefaultResolver();
    String expectedText =
        FileUtils.getResourceAsString(resolver, "rest/mapping-after.txt").stripTrailing();
    // Act
    boolean applied = updater.up(builder);
    // Assert
    assertTrue(applied);
    verify(builder, times(1)).addFile(file, expectedText);
  }
}
