package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.DOCKERFILE;
import static org.gradle.internal.impldep.org.testng.Assert.assertNotNull;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
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
public class UpgradeY2022M05D05Test {
  @Mock private Project project;
  @Mock private Logger logger;
  private ModuleBuilder builder;
  private UpgradeAction updater;

  @BeforeEach
  public void setup() throws IOException {
    when(project.getRootProject()).thenReturn(project);
    when(project.getName()).thenReturn("UtilsTest");
    when(project.getLogger()).thenReturn(logger);
    when(project.getProjectDir()).thenReturn(Files.createTempDirectory("sample").toFile());
    builder = spy(new ModuleBuilder(project));
    updater = new UpgradeY2022M05D05();
    assertNotNull(updater.name());
    assertNotNull(updater.description());
  }

  @Test
  public void shouldApplyUpdate() {
    // Arrange
    builder.addFile(
        DOCKERFILE,
        "COPY *.jar app.jar\nENTRYPOINT [\"sh\",\"-c\",\"java $JAVA_OPTS -jar app.jar\"]");
    // Act
    updater.up(builder);
    // Assert
    verify(builder, atLeast(1))
        .addFile(
            DOCKERFILE,
            "COPY *.jar UtilsTest.jar\nENTRYPOINT [\"sh\",\"-c\",\"java $JAVA_OPTS -jar UtilsTest.jar\"]");
  }
}
