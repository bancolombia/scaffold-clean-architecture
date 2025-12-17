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

  @BeforeEach
  void setup() throws IOException {
    when(project.getName()).thenReturn("UtilsTest");
    when(project.getLogger()).thenReturn(logger);
    when(project.getProjectDir()).thenReturn(Files.createTempDirectory("sample").toFile());

    updater = new UpgradeY2025M12D12SpringBoot4();
    assertNotNull(updater.name());
    assertNotNull(updater.description());
  }

  @Test
  void shouldApplyUpdateJava() {
    when(project.getRootDir()).thenReturn(new File("Sample.java"));
    builder = spy(new ModuleBuilder(project));

    String file = new File("Sample.java").getAbsolutePath();
    // Arrange
    builder.addFile(file, "import org.springframework.boot.actuate.health.Health;");
    // Act
    boolean applied = updater.up(builder);
    // Assert
    assertTrue(applied);
    verify(builder, times(1))
        .addFile(file, "import org.springframework.boot.health.contributor.Health;");
  }

  @Test
  void shouldApplyUpdateGradle() {
    when(project.getRootDir()).thenReturn(new File("build.gradle"));
    builder = spy(new ModuleBuilder(project));

    String file = new File("build.gradle").getAbsolutePath();
    // Arrange
    builder.addFile(file, "testImplementation \"com.fasterxml.jackson.core:jackson-databind\"");
    // Act
    boolean applied = updater.up(builder);
    // Assert
    assertTrue(applied);
    verify(builder, times(1))
        .addFile(file, "testImplementation \"tools.jackson.core:jackson-databind\"");
  }
}
