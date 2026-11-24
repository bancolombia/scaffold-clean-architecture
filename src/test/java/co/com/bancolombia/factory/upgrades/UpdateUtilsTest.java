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
class UpdateUtilsTest {

  @Mock private Project project;
  @Mock private Logger logger;
  private ModuleBuilder builder;

  @BeforeEach
  void setup() throws IOException {
    when(project.getName()).thenReturn("UtilsTest");
    when(project.getLogger()).thenReturn(logger);
    when(project.getProjectDir()).thenReturn(Files.createTempDirectory("sample").toFile());
    builder = spy(new ModuleBuilder(project));
  }

  @Test
  void shouldAppendIfNotContains() throws IOException {
    String file = "build.gradle";
    String check = "jar {";
    String toAdd = "jar {enabled = false}";
    String currentContent = "dependencies {}\n";
    builder.addFile(file, currentContent);
    String expectedContent = "dependencies {}\njar {enabled = false}";
    boolean applied = UpdateUtils.appendIfNotContains(builder, file, check, toAdd);
    verify(builder).addFile(file, expectedContent);
    assertTrue(applied);
  }

  @Test
  void shouldNotAppendWhenContains() throws IOException {
    String file = "build.gradle";
    String check = "jar {";
    String toAdd = "jar {enabled = false}";
    String currentContent = "dependencies {}\njar {enabled = false}";
    builder.addFile(file, currentContent);
    boolean applied = UpdateUtils.appendIfNotContains(builder, file, check, toAdd);
    verify(builder, times(1)).addFile(file, currentContent);
    assertFalse(applied);
  }

  @Test
  void shouldNotAppendValidate() {
    String file = "build.gradle";
    String check = "jar {";
    String match = "jar {enabled = false}";
    String toInsert = "ignored when check is already present";
    String currentContent = "dependencies {}\njar {enabled = false}";
    builder.addFile(file, currentContent);
    String result = UpdateUtils.insertBeforeMatch(currentContent, match, check, toInsert);
    assertEquals(currentContent, result);
  }

  @Test
  void shouldAppendValidate() {
    String file = "build.gradle";
    String check = "jar {enabled = false}";
    String match = "dependencies";
    String toInsert = "jar {enabled = false}\n";
    String currentContent = "dependencies {}\n";
    builder.addFile(file, currentContent);
    String expected = "jar {enabled = false}\ndependencies {}\n";
    String result = UpdateUtils.insertBeforeMatch(currentContent, match, check, toInsert);
    assertEquals(expected, result);
  }

  @Test
  void shouldAddToStartIfNotContains() {
    String file = "build.gradle";
    String check = "info.solidsoft.pitest.aggregator";
    String prefix = "apply plugin: 'info.solidsoft.pitest.aggregator'\n\n";
    String currentContent = "allprojects {}\n";
    builder.addFile(file, currentContent);
    String expected = prefix + currentContent;
    String result = UpdateUtils.addToStartIfNotContains(currentContent, check, prefix);
    assertEquals(expected, result);
  }

  @Test
  void shouldThrowWhenNoMatch() {
    String check = "jar {enabled = false}";
    String match = "nomatch";
    String toInsert = "jar {enabled = false}";
    String currentContent = "dependencies {}\n";
    assertThrows(
        InvalidStateException.class,
        () -> UpdateUtils.insertAfterMatch(currentContent, match, check, toInsert));
  }

  @Test
  void shouldThrowWhenNoMatchBefore() {
    String check = "jar {enabled = false}";
    String match = "nomatch";
    String toInsert = "jar {enabled = false}";
    String currentContent = "dependencies {}\n";
    assertThrows(
        InvalidStateException.class,
        () -> UpdateUtils.insertBeforeMatch(currentContent, match, check, toInsert));
  }

  @Test
  void shouldCompareVersions() {
    assertTrue(UpdateUtils.isNewerVersion("1.2.3", "1.2.4"));
    assertTrue(UpdateUtils.isNewerVersion("1.2.3", "1.2.3.1"));
    assertFalse(UpdateUtils.isNewerVersion("1.2.3.4", "1.2.3.4"));
    assertFalse(UpdateUtils.isNewerVersion("1.3.0", "1.1.1"));
    assertFalse(UpdateUtils.isNewerVersion("1.1.1.Alpha", "1.1.1.Beta"));
  }

  @Test
  void shouldCheckOneOfAll() {
    assertTrue(UpdateUtils.oneOfAll(true, true, true));
    assertTrue(UpdateUtils.oneOfAll(false, false, true));
    assertFalse(UpdateUtils.oneOfAll(false, false, false));
  }
}
