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
public class UpgradeY2023M10D02SonarRulesTest {
  @Mock private Project project;
  @Mock private Logger logger;
  private ModuleBuilder builder;
  private UpgradeAction updater;

  @BeforeEach
  public void setup() throws IOException {
    when(project.getName()).thenReturn("UtilsTest");
    when(project.getLogger()).thenReturn(logger);
    when(project.getProjectDir()).thenReturn(Files.createTempDirectory("sample").toFile());
    builder = spy(new ModuleBuilder(project));
    updater = new UpgradeY2023M10D02SonarRules();
    assertNotNull(updater.name());
    assertNotNull(updater.description());
  }

  @Test
  void shouldApplyUpdate() throws IOException {
    String file = Constants.MainFiles.BUILD_GRADLE;
    // Arrange
    DefaultResolver resolver = new DefaultResolver();
    String text = FileUtils.getResourceAsString(resolver, "sonar-rules/before.txt");
    String expectedText = FileUtils.getResourceAsString(resolver, "sonar-rules/after.txt");
    builder.addFile(file, text);
    // Act
    boolean applied = updater.up(builder);
    // Assert
    assertTrue(applied);
    verify(builder, times(1)).addFile(file, expectedText);
  }
}
