package co.com.bancolombia.factory.upgrades.actions;

import static co.com.bancolombia.Constants.MainFiles.APP_BUILD_GRADLE;
import static co.com.bancolombia.Constants.MainFiles.BUILD_GRADLE;
import static co.com.bancolombia.Constants.MainFiles.GRADLE_PROPERTIES;
import static co.com.bancolombia.Constants.MainFiles.MAIN_GRADLE;
import static co.com.bancolombia.Constants.MainFiles.SETTINGS_GRADLE;
import static co.com.bancolombia.factory.upgrades.actions.UpdateDependencies.FILES_TO_UPDATE;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.upgrades.UpgradeAction;
import co.com.bancolombia.models.Release;
import co.com.bancolombia.utils.FileUtils;
import co.com.bancolombia.utils.operations.ExternalOperations;
import com.github.mustachejava.resolver.DefaultResolver;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.internal.logging.text.StyledTextOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpgradeY2026M11D07ScaffoldFiveTest {

  private static final String MQ_SENDER_BUILD_GRADLE =
      "./infrastructure/driven-adapters/mq-sender/build.gradle";
  private static final String REACTIVE_WEB_BUILD_GRADLE =
      "./infrastructure/entry-points/reactive-web/build.gradle";

  @Mock private Project project;
  @Mock private Logger logger;
  @Mock private ExternalOperations operations;
  @Mock private StyledTextOutput styledTextOutput;

  private ModuleBuilder builder;
  private UpgradeAction updater;
  private DefaultResolver resolver;

  @BeforeEach
  void setup() throws IOException {
    when(project.getName()).thenReturn("ScaffoldFiveTest");
    when(project.getLogger()).thenReturn(logger);
    when(project.getProjectDir()).thenReturn(Files.createTempDirectory("sample").toFile());

    builder = spy(new ModuleBuilder(project, operations));
    builder.setStyledLogger(styledTextOutput);
    lenient().when(styledTextOutput.style(any())).thenReturn(styledTextOutput);
    lenient().when(styledTextOutput.append(any())).thenReturn(styledTextOutput);
    updater = new UpgradeY2026M11D07ScaffoldFive();
    resolver = new DefaultResolver();

    assertNotNull(updater.name());
    assertNotNull(updater.description());
  }

  private void stubLatestRelease(String tagName) {
    Release release = new Release();
    release.setTagName(tagName);
    when(operations.getLatestPluginVersion()).thenReturn(release);
  }

  private String resource(String path) throws IOException {
    return FileUtils.getResourceAsString(resolver, path);
  }

  @Test
  void shouldMigrateProjectToScaffoldFive() throws IOException {
    stubLatestRelease("5.0.0");
    builder.addFile(SETTINGS_GRADLE, resource("scaffold5/settings-before.txt"));
    builder.addFile(BUILD_GRADLE, resource("scaffold5/build-before.txt"));
    builder.addFile(MAIN_GRADLE, resource("scaffold5/main-before.txt"));
    builder.addFile(GRADLE_PROPERTIES, resource("scaffold5/gradle-properties-before.txt"));
    builder.addFile(APP_BUILD_GRADLE, resource("scaffold5/app-service-build-before.txt"));
    builder.addFile(MQ_SENDER_BUILD_GRADLE, resource("scaffold5/mq-sender-build-before.txt"));
    builder.addFile(REACTIVE_WEB_BUILD_GRADLE, resource("scaffold5/reactive-web-build-before.txt"));
    builder.addParam(
        FILES_TO_UPDATE,
        List.of(BUILD_GRADLE, APP_BUILD_GRADLE, MQ_SENDER_BUILD_GRADLE, REACTIVE_WEB_BUILD_GRADLE));

    boolean applied = updater.up(builder);

    assertTrue(applied);
    verify(builder).addFile(SETTINGS_GRADLE, resource("scaffold5/settings-after.txt"));
    verify(builder).addFile(BUILD_GRADLE, resource("scaffold5/build-after.txt"));
    verify(builder).addFile(GRADLE_PROPERTIES, resource("scaffold5/gradle-properties-after.txt"));
    verify(builder).addFile(APP_BUILD_GRADLE, resource("scaffold5/app-service-build-after.txt"));
    verify(builder)
        .addFile(MQ_SENDER_BUILD_GRADLE, resource("scaffold5/mq-sender-build-after.txt"));
    verify(builder)
        .addFile(REACTIVE_WEB_BUILD_GRADLE, resource("scaffold5/reactive-web-build-after.txt"));
    verify(builder).removeDir(MAIN_GRADLE);
  }

  @Test
  void shouldReduceAppServiceToDependenciesBlockWhenFilesToUpdateUsesAbsoluteStylePaths()
      throws IOException {
    stubLatestRelease("5.0.0");
    // FILES_TO_UPDATE from UpdateProjectTask carries absolute paths without a "./" prefix,
    // unlike the Constants.MainFiles.APP_BUILD_GRADLE relative constant used to preload the file.
    String appServicePathWithoutDotSlash = "applications/app-service/build.gradle";
    builder.addFile(SETTINGS_GRADLE, resource("scaffold5/settings-before.txt"));
    builder.addFile(BUILD_GRADLE, resource("scaffold5/build-before.txt"));
    builder.addFile(MAIN_GRADLE, resource("scaffold5/main-before.txt"));
    builder.addFile(GRADLE_PROPERTIES, resource("scaffold5/gradle-properties-before.txt"));
    builder.addFile(
        appServicePathWithoutDotSlash, resource("scaffold5/app-service-build-before.txt"));
    builder.addParam(FILES_TO_UPDATE, List.of(BUILD_GRADLE, appServicePathWithoutDotSlash));

    updater.up(builder);

    verify(builder)
        .addFile(appServicePathWithoutDotSlash, resource("scaffold5/app-service-build-after.txt"));
  }

  @Test
  void shouldOmitSonarBlockWhenExclusionsPropertyIsMissing() throws IOException {
    stubLatestRelease("5.0.0");
    String buildWithoutExclusions =
        resource("scaffold5/build-before.txt")
            .replaceAll("\\s*property \"sonar\\.exclusions\", \"[^\"]*\"\n", "\n");
    builder.addFile(BUILD_GRADLE, buildWithoutExclusions);
    builder.addFile(MAIN_GRADLE, resource("scaffold5/main-before.txt"));
    builder.addFile(SETTINGS_GRADLE, resource("scaffold5/settings-before.txt"));
    builder.addFile(GRADLE_PROPERTIES, resource("scaffold5/gradle-properties-before.txt"));

    updater.up(builder);

    verify(builder)
        .addFile(
            BUILD_GRADLE,
            resource("scaffold5/build-after.txt").replaceAll("(?s)\n\nsonar \\{.*\\}\n", "\n"));
  }

  @Test
  void shouldSkipAwsBomDependencyWhenAbsentFromMainGradle() throws IOException {
    stubLatestRelease("5.0.0");
    String mainWithoutBom =
        resource("scaffold5/main-before.txt")
            .replace("        implementation platform('software.amazon.awssdk:bom:2.54.10')\n", "");
    builder.addFile(BUILD_GRADLE, resource("scaffold5/build-before.txt"));
    builder.addFile(MAIN_GRADLE, mainWithoutBom);
    builder.addFile(SETTINGS_GRADLE, resource("scaffold5/settings-before.txt"));
    builder.addFile(GRADLE_PROPERTIES, resource("scaffold5/gradle-properties-before.txt"));

    updater.up(builder);

    verify(builder)
        .addFile(
            BUILD_GRADLE,
            resource("scaffold5/build-after.txt")
                .replace(
                    "\n\n    dependencies {\n        implementation platform('software.amazon.awssdk:bom:2.54.10')\n    }\n",
                    "\n"));
  }

  @Test
  void shouldNotApplyWhenNoStableFiveReleaseExists() throws IOException {
    stubLatestRelease("5.0.0-beta");
    builder.addFile(GRADLE_PROPERTIES, resource("scaffold5/gradle-properties-before.txt"));

    boolean applied = updater.up(builder);

    assertFalse(applied);
    verify(builder, never()).removeDir(MAIN_GRADLE);
    verify(builder, never()).addFile(eq(SETTINGS_GRADLE), any());
    verify(builder, never()).addFile(eq(BUILD_GRADLE), any());
  }

  @Test
  void shouldForceBetaVersionWhenGradlePropertiesOptsIntoBeta5() throws IOException {
    // No stubLatestRelease: beta5=true must bypass the latest-release lookup entirely.
    builder.addFile(SETTINGS_GRADLE, resource("scaffold5/settings-before.txt"));
    builder.addFile(BUILD_GRADLE, resource("scaffold5/build-before.txt"));
    builder.addFile(MAIN_GRADLE, resource("scaffold5/main-before.txt"));
    builder.addFile(
        GRADLE_PROPERTIES, resource("scaffold5/gradle-properties-before.txt") + "\nbeta5=true");

    boolean applied = updater.up(builder);

    assertTrue(applied);
    verify(builder)
        .addFile(
            SETTINGS_GRADLE,
            resource("scaffold5/settings-after.txt")
                .replace("version '5.0.0'", "version '5.0.0-beta'"));
    verify(builder)
        .addFile(
            GRADLE_PROPERTIES,
            resource("scaffold5/gradle-properties-before.txt")
                    .replace("systemProp.version=4.6.1", "systemProp.version=5.0.0-beta")
                + "\nbeta5=true\njavaVersion=17\n");
  }

  @Test
  void shouldRemoveProjectDependencyDespiteExtraWhitespaceInAppService() throws IOException {
    stubLatestRelease("5.0.0");
    builder.addFile(SETTINGS_GRADLE, resource("scaffold5/settings-before.txt"));
    builder.addFile(BUILD_GRADLE, resource("scaffold5/build-before.txt"));
    builder.addFile(MAIN_GRADLE, resource("scaffold5/main-before.txt"));
    builder.addFile(GRADLE_PROPERTIES, resource("scaffold5/gradle-properties-before.txt"));
    builder.addFile(
        APP_BUILD_GRADLE,
        """
        dependencies {
            implementation project(':model')
            implementation project( ':rest-consumer')
            implementation 'org.springframework.boot:spring-boot-starter'
        }""");
    builder.addParam(FILES_TO_UPDATE, List.of(BUILD_GRADLE, APP_BUILD_GRADLE));

    updater.up(builder);

    verify(builder)
        .addFile(
            APP_BUILD_GRADLE,
            """
            dependencies {
                implementation 'org.springframework.boot:spring-boot-starter'
            }
            """);
  }

  @Test
  void shouldKeepCrossAdapterDependencyInInfrastructureModule() throws IOException {
    stubLatestRelease("5.0.0");
    builder.addFile(SETTINGS_GRADLE, resource("scaffold5/settings-before.txt"));
    builder.addFile(BUILD_GRADLE, resource("scaffold5/build-before.txt"));
    builder.addFile(MAIN_GRADLE, resource("scaffold5/main-before.txt"));
    builder.addFile(GRADLE_PROPERTIES, resource("scaffold5/gradle-properties-before.txt"));
    builder.addFile(
        MQ_SENDER_BUILD_GRADLE,
        """
        dependencies {
            implementation project(':model')
            implementation project(':rest-consumer')
            implementation 'io.micrometer:micrometer-core'
        }""");
    builder.addParam(FILES_TO_UPDATE, List.of(BUILD_GRADLE, MQ_SENDER_BUILD_GRADLE));

    updater.up(builder);

    verify(builder)
        .addFile(
            MQ_SENDER_BUILD_GRADLE,
            """
            dependencies {
                implementation project(':rest-consumer')
                implementation 'io.micrometer:micrometer-core'
            }""");
  }
}
