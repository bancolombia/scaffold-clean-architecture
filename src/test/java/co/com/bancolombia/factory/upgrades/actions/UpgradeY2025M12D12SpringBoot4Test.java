package co.com.bancolombia.factory.upgrades.actions;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import java.io.File;
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
class UpgradeY2025M12D12SpringBoot4Test {

  @Mock private Project project;
  @Mock private Logger logger;
  private ModuleBuilder builder;
  private UpgradeAction updater;
  private File tempDir;

  @BeforeEach
  void setup() throws IOException {
    tempDir = Files.createTempDirectory("sample").toFile();
    when(project.getName()).thenReturn("UtilsTest");
    when(project.getLogger()).thenReturn(logger);
    when(project.getProjectDir()).thenReturn(tempDir);

    updater = new UpgradeY2025M12D12SpringBoot4();
    assertNotNull(updater.name());
    assertNotNull(updater.description());
  }

  @Test
  void shouldApplyUpdateJava() throws IOException {
    builder = spy(new ModuleBuilder(project));
    File sampleFile = new File(tempDir, "Sample.java");
    Files.writeString(
        sampleFile.toPath(), "import org.springframework.boot.actuate.health.Health;");
    String file = sampleFile.getAbsolutePath();
    // Act
    boolean applied = updater.up(builder);
    // Assert
    assertTrue(applied);
    verify(builder, times(1))
        .addFile(file, "import org.springframework.boot.health.contributor.Health;");
  }

  @Test
  void shouldApplyUpdateGradle() throws IOException {
    builder = spy(new ModuleBuilder(project));
    File gradleFile = new File(tempDir, "build.gradle");
    Files.writeString(
        gradleFile.toPath(), "testImplementation \"com.fasterxml.jackson.core:jackson-databind\"");
    String file = gradleFile.getAbsolutePath();
    // Act
    boolean applied = updater.up(builder);
    // Assert
    assertTrue(applied);
    verify(builder, times(1))
        .addFile(file, "testImplementation \"tools.jackson.core:jackson-databind\"");
  }
}
