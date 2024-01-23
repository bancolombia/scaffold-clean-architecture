package co.com.bancolombia.factory;

import static co.com.bancolombia.Constants.MainFiles.APPLICATION_PROPERTIES;
import static co.com.bancolombia.TestUtils.deleteStructure;
import static co.com.bancolombia.TestUtils.getTask;
import static co.com.bancolombia.TestUtils.getTestDir;
import static co.com.bancolombia.TestUtils.setupProject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.models.Release;
import co.com.bancolombia.task.GenerateStructureTask;
import co.com.bancolombia.utils.FileUtils;
import co.com.bancolombia.utils.operations.ExternalOperations;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import org.gradle.api.Project;
import org.gradle.internal.logging.text.StyledTextOutput;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ModuleBuilderTest {
  private static final String TEST_DIR = getTestDir(ModuleBuilderTest.class);
  private ModuleBuilder builder;
  @Mock private StyledTextOutput styledTextOutput;
  @Mock private ExternalOperations operations;

  @BeforeEach
  public void setup() throws IOException, CleanException {
    deleteStructure(Path.of(TEST_DIR));
    Project project = setupProject(ModuleBuilderTest.class, GenerateStructureTask.class);

    GenerateStructureTask taskStructure = getTask(project, GenerateStructureTask.class);
    taskStructure.setType(GenerateStructureTask.ProjectType.IMPERATIVE);
    taskStructure.setLanguage(GenerateStructureTask.Language.JAVA);
    taskStructure.execute();

    ProjectBuilder.builder()
        .withName("app-service")
        .withProjectDir(new File(TEST_DIR + "/applications/app-service"))
        .withParent(project)
        .build();

    builder = new ModuleBuilder(project, operations);
    builder.setStyledLogger(styledTextOutput);
  }

  @AfterAll
  public static void tearDown() {
    deleteStructure(Path.of(TEST_DIR));
  }

  private void setupMocks() {
    when(styledTextOutput.style(any())).thenReturn(styledTextOutput);
    when(styledTextOutput.append(any())).thenReturn(styledTextOutput);
  }

  @Test
  public void shouldAppendProperties() throws IOException {
    setupMocks();
    builder.appendToProperties("spring.datasource").put("url", "mydburl");
    builder.appendToProperties("").put("test", "myUnitTes");
    builder.appendToProperties("server").put("port", 8000);
    builder.removeDir(null);
    builder.persist();
    builder.updateFile(
        APPLICATION_PROPERTIES,
        content -> {
          assertTrue(content.contains("myUnitTes"));
          return content;
        });
  }

  @Test
  public void shouldReturnErrorBecauseNoProperty() {
    assertThrows(IOException.class, () -> builder.analyticsEnabled());
  }

  @Test
  public void shouldReturnAnalyticsState() throws IOException {
    FileUtils.setGradleProperty(TEST_DIR, "analytics", "true");
    assertTrue(builder.analyticsEnabled());
  }

  @Test
  public void shouldPrintVersionMissMatch() {
    setupMocks();
    Release release = new Release();
    release.setTagName("1.1.1");
    release.setPublishedAt(OffsetDateTime.now());
    when(operations.getLatestPluginVersion()).thenReturn(release);
    // Act
    Release loaded = builder.getLatestRelease();
    // Assert
    assertEquals(release, loaded);
    verify(styledTextOutput, atLeastOnce()).append("gradle u");
  }
}
