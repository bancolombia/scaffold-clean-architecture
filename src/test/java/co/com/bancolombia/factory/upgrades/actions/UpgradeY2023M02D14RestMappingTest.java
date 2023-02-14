package co.com.bancolombia.factory.upgrades.actions;

import static org.gradle.internal.impldep.org.testng.Assert.assertNotNull;
import static org.gradle.internal.impldep.org.testng.Assert.assertTrue;
import static org.mockito.Mockito.*;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import co.com.bancolombia.utils.FileUtils;
import com.github.mustachejava.resolver.DefaultResolver;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UpgradeY2023M02D14RestMappingTest {
  @Mock private Project project;
  @Mock private Logger logger;
  private ModuleBuilder builder;
  private UpgradeAction updater;

  @Before
  public void setup() throws IOException {
    when(project.getName()).thenReturn("UtilsTest");
    when(project.getLogger()).thenReturn(logger);
    when(project.getProjectDir()).thenReturn(Files.createTempDirectory("sample").toFile());
    when(project.getRootDir()).thenReturn(new File("Sample.java"));
    when(project.getName()).thenReturn("api-rest");
    when(project.getSubprojects()).thenReturn(Set.of(project));
    builder = spy(new ModuleBuilder(project));
    updater = new UpgradeY2023M02D14RestMapping();
    assertNotNull(updater.name());
    assertNotNull(updater.description());
  }

  @Test
  public void shouldApplyUpdate() throws IOException {
    String file = new File("Sample.java").getAbsolutePath();
    // Arrange
    DefaultResolver resolver = new DefaultResolver();
    String text = FileUtils.getResourceAsString(resolver, "rest/mapping-before.txt");
    String expectedText = FileUtils.getResourceAsString(resolver, "rest/mapping-after.txt");
    builder.addFile(file, text);
    // Act
    boolean applied = updater.up(builder);
    // Assert
    assertTrue(applied);
    verify(builder, times(1)).addFile(file, expectedText);
  }
}
