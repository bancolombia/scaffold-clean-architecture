package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.MAIN_GRADLE;
import static co.com.bancolombia.factory.upgrades.actions.UpdateDependencies.FILES_TO_UPDATE;
import static org.gradle.internal.impldep.org.testng.Assert.assertNotNull;
import static org.mockito.Mockito.*;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UpgradeY2022M05D02Test {
  @Mock private Project project;
  @Mock private Logger logger;
  private ModuleBuilder builder;
  private UpgradeAction updater;

  @Before
  public void setup() throws IOException {
    when(project.getName()).thenReturn("UtilsTest");
    when(project.getLogger()).thenReturn(logger);
    when(project.getProjectDir()).thenReturn(Files.createTempDirectory("sample").toFile());
    builder = spy(new ModuleBuilder(project));
    updater = new UpgradeY2022M05D02();
    assertNotNull(updater.name());
    assertNotNull(updater.description());
  }

  @Test
  public void shouldApplyUpdate() throws IOException {
    // Arrange
    when(builder.getParam(FILES_TO_UPDATE)).thenReturn(List.of(MAIN_GRADLE));
    builder.addFile(MAIN_GRADLE, "compile 'org.springframework:spring-context:1.1.1'");
    // Act
    updater.up(builder);
    // Assert
    verify(builder, atLeast(1))
        .addFile(MAIN_GRADLE, "implementation 'org.springframework:spring-context:1.1.1'");
  }
}
