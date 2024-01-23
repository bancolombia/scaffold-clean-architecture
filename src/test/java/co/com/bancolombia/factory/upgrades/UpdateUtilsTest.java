package co.com.bancolombia.factory.upgrades;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.com.bancolombia.exceptions.InvalidStateException;
import co.com.bancolombia.factory.ModuleBuilder;
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
public class UpdateUtilsTest {

  @Mock private Project project;
  @Mock private Logger logger;
  private ModuleBuilder builder;

  @BeforeEach
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
    boolean applied = UpdateUtils.appendIfNotContains(builder, file, check, toAdd);
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
    boolean applied = UpdateUtils.appendIfNotContains(builder, file, check, toAdd);
    // Assert
    verify(builder, times(1)).addFile(file, currentContent);
    assertFalse(applied);
  }

  @Test
  public void shouldNotAppendValidate() {
    // Arrange
    String file = "build.gradle";
    String check = "jar {";
    String match = "jar {enabled = false}";
    String currentContent = "dependencies {}\njar {enabled = false}";
    builder.addFile(file, currentContent);
    // Act
    String result = UpdateUtils.insertBeforeMatch(currentContent, match, check, file);
    // Assert
    assertEquals(currentContent, result);
  }

  @Test
  public void shouldAppendValidate() {
    // Arrange
    String file = "build.gradle\n";
    String check = "jar {enabled = false}";
    String match = "dependencies";
    String currentContent = "dependencies {}\n";
    builder.addFile(file, currentContent);
    // Act
    String expected = "build.gradle\ndependencies {}\n";
    String result = UpdateUtils.insertBeforeMatch(currentContent, match, check, file);
    // Assert
    assertEquals(expected, result);
  }

  @Test
  public void shouldThrowWhenNoMatch() {
    // Arrange
    String file = "build.gradle\n";
    String check = "jar {enabled = false}";
    String match = "nomatch";
    String currentContent = "dependencies {}\n";
    builder.addFile(file, currentContent);
    // Act
    assertThrows(
        InvalidStateException.class,
        () -> UpdateUtils.insertAfterMatch(currentContent, match, check, file));
  }

  @Test
  public void shouldThrowWhenNoMatchBefore() {
    // Arrange
    String file = "build.gradle\n";
    String check = "jar {enabled = false}";
    String match = "nomatch";
    String currentContent = "dependencies {}\n";
    builder.addFile(file, currentContent);
    // Act
    assertThrows(
        InvalidStateException.class,
        () -> UpdateUtils.insertBeforeMatch(currentContent, match, check, file));
  }
}
