package co.com.bancolombia.factory.upgrades;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import co.com.bancolombia.factory.ModuleBuilder;
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
public class UtilsTest {

  @Mock private Project project;
  @Mock private Logger logger;
  private ModuleBuilder builder;

  @Before
  public void setup() throws IOException {
    when(project.getName()).thenReturn("UtilsTest");
    when(project.getLogger()).thenReturn(logger);
    when(project.getProjectDir()).thenReturn(Files.createTempDirectory("sample").toFile());
    builder = spy(new ModuleBuilder(project));
  }

  @Test
  public void shouldAppendIfNotContains() throws IOException {
    // Arrange
    String file = "build.gradle";
    String check = "jar {";
    String toAdd = "jar {enabled = false}";
    String currentContent = "dependencies {}\n";
    builder.addFile(file, currentContent);
    String expectedContent = "dependencies {}\njar {enabled = false}";
    // Act
    boolean applied = Utils.appendIfNotContains(builder, file, check, toAdd);
    // Assert
    verify(builder).addFile(file, expectedContent);
    assertTrue(applied);
  }

  @Test
  public void shouldNotAppendWhenContains() throws IOException {
    // Arrange
    String file = "build.gradle";
    String check = "jar {";
    String toAdd = "jar {enabled = false}";
    String currentContent = "dependencies {}\njar {enabled = false}";
    builder.addFile(file, currentContent);
    // Act
    boolean applied = Utils.appendIfNotContains(builder, file, check, toAdd);
    // Assert
    verify(builder, times(2)).addFile(file, currentContent);
    assertFalse(applied);
  }
}
