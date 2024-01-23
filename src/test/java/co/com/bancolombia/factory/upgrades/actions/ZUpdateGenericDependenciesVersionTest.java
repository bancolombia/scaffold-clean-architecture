package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.BUILD_GRADLE;
import static co.com.bancolombia.Constants.MainFiles.MAIN_GRADLE;
import static org.gradle.internal.impldep.org.testng.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.com.bancolombia.Constants;
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
class ZUpdateGenericDependenciesVersionTest {

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
    updater = new ZUpdateGenericDependenciesVersion();
    assertNotNull(updater.name());
    assertNotNull(updater.description());
  }

  @Test
  void shouldUpdate() {
    // Arrange
    builder.addFile(BUILD_GRADLE, "\t\tspringBootVersion = '2.2.4'\n");
    builder.addFile(MAIN_GRADLE, "");
    // Act
    boolean applied = updater.up(builder);
    // Assert
    assertTrue(applied);
    verify(builder, atLeast(1))
        .addFile(BUILD_GRADLE, "\t\tspringBootVersion = '" + Constants.SPRING_BOOT_VERSION + "'\n");
  }

  @Test
  void shouldUpdateWithQuotationMarks() {
    // Arrange
    builder.addFile(BUILD_GRADLE, "\t\tlombokVersion = \"1.0.0\"\n");
    builder.addFile(MAIN_GRADLE, "");
    // Act
    boolean applied = updater.up(builder);
    // Assert
    assertTrue(applied);
    verify(builder, atLeast(1))
        .addFile(BUILD_GRADLE, "\t\tlombokVersion = '" + Constants.LOMBOK_VERSION + "'\n");
  }

  @Test
  void shouldUpdateGradleVersion() {
    // Arrange
    builder.addFile(BUILD_GRADLE, "");
    builder.addFile(MAIN_GRADLE, "tasks.named('wrapper') {\n\t\tgradleVersion = \"6.9.1\"\n}");
    // Act
    boolean applied = updater.up(builder);
    // Assert
    assertTrue(applied);
    verify(builder, atLeast(1))
        .addFile(
            MAIN_GRADLE,
            "tasks.named('wrapper') {\n\t\tgradleVersion = '"
                + Constants.GRADLE_WRAPPER_VERSION
                + "'\n}");
  }
}
