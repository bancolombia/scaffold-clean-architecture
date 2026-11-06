package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.GRADLE_PROPERTIES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
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
class UpgradeY2026M11D05GradleToolingParallelTest {
  @Mock private Project project;
  @Mock private Logger logger;

  private ModuleBuilder builder;
  private UpgradeAction updater;

  @BeforeEach
  void setup() throws IOException {
    when(project.getName()).thenReturn("UtilsTest");
    when(project.getLogger()).thenReturn(logger);
    when(project.getProjectDir()).thenReturn(Files.createTempDirectory("sample").toFile());
    builder = new ModuleBuilder(project);
    updater = new UpgradeY2026M11D05GradleToolingParallel();

    assertNotNull(updater.name());
    assertNotNull(updater.description());
  }

  @Test
  void shouldAddPropertyAfterParallel() throws IOException {
    builder.addFile(
        GRADLE_PROPERTIES, "language=java\norg.gradle.parallel=true\norg.gradle.caching=true\n");

    assertTrue(updater.up(builder));

    assertEquals(
        "language=java\norg.gradle.parallel=true\norg.gradle.tooling.parallel=true\n"
            + "org.gradle.caching=true\n",
        builder.readFile(GRADLE_PROPERTIES));
  }

  @Test
  void shouldAppendPropertyWhenParallelIsMissing() throws IOException {
    builder.addFile(GRADLE_PROPERTIES, "language=java");

    assertTrue(updater.up(builder));

    assertEquals(
        "language=java\norg.gradle.tooling.parallel=true\n", builder.readFile(GRADLE_PROPERTIES));
  }

  @Test
  void shouldNotChangeWhenPropertyIsPresent() throws IOException {
    String content = "org.gradle.parallel=true\norg.gradle.tooling.parallel=false\n";
    builder.addFile(GRADLE_PROPERTIES, content);

    assertFalse(updater.up(builder));
    assertEquals(content, builder.readFile(GRADLE_PROPERTIES));
  }

  @Test
  void shouldSkipWhenThereIsNoGradleProperties() {
    assertFalse(updater.up(builder));
  }
}
