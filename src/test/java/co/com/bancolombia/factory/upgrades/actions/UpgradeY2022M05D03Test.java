package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.APP_BUILD_GRADLE;
import static co.com.bancolombia.Constants.MainFiles.MAIN_GRADLE;
import static co.com.bancolombia.factory.upgrades.actions.UpdateDependencies.FILES_TO_UPDATE;
import static org.gradle.internal.impldep.org.testng.Assert.assertNotNull;
import static org.mockito.Mockito.*;

import co.com.bancolombia.Constants;
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
public class UpgradeY2022M05D03Test {
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
    updater = new UpgradeY2022M05D03();
    assertNotNull(updater.name());
    assertNotNull(updater.description());
  }

  @Test
  public void shouldApplyUpdate() {
    // Arrange
    String drivenFile = "./infrastructure/driven-adapter/some-aws/build.gradle";
    when(builder.getParam(FILES_TO_UPDATE))
        .thenReturn(List.of(MAIN_GRADLE, drivenFile, APP_BUILD_GRADLE));
    builder.addFile(APP_BUILD_GRADLE, "dependencies {\n\tcompile 'some:dependency:1.1.1'\n}");
    builder.addFile(MAIN_GRADLE, "dependencies {\n\tcompile 'some-other:dependency:1.1.1'\n}");
    builder.addFile(
        drivenFile, "dependencies {\n\timplementation 'software.amazon.awssdk:s3:2.17.155'\n}");
    // Act
    updater.up(builder);
    // Assert
    verify(builder, atLeast(1))
        .addFile(
            MAIN_GRADLE,
            "dependencies {\n\t"
                + Constants.AWS_BOM
                + "\n\tcompile 'some-other:dependency:1.1.1'\n}");
    verify(builder, atLeast(1))
        .addFile(
            APP_BUILD_GRADLE,
            "dependencies {\n\timplementation 'software.amazon.awssdk:sts'\n\tcompile 'some:dependency:1.1.1'\n}");
    verify(builder, atLeast(1))
        .addFile(drivenFile, "dependencies {\n\timplementation 'software.amazon.awssdk:s3'\n}");
  }
}
