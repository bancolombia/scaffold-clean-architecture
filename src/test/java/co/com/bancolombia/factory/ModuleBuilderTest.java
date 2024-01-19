package co.com.bancolombia.factory;

import static co.com.bancolombia.Constants.MainFiles.APPLICATION_PROPERTIES;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.models.Release;
import co.com.bancolombia.task.GenerateStructureTask;
import co.com.bancolombia.utils.FileUtils;
import co.com.bancolombia.utils.operations.ExternalOperations;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.time.OffsetDateTime;
import org.gradle.api.Project;
import org.gradle.internal.logging.text.StyledTextOutput;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ModuleBuilderTest {
  private ModuleBuilder builder;
  @Mock private StyledTextOutput styledTextOutput;
  @Mock private ExternalOperations operations;

  @Before
  public void init() throws IOException, CleanException {
    File projectDir = new File("build/unitTest");
    Files.createDirectories(projectDir.toPath());
    writeString(
        new File(projectDir, "build.gradle"),
        "plugins {" + "  id('co.com.bancolombia.cleanArchitecture')" + "}");

    Project project =
        ProjectBuilder.builder()
            .withName("cleanArchitecture")
            .withProjectDir(new File("build/unitTest"))
            .build();

    project.getTasks().create("testStructure", GenerateStructureTask.class);
    GenerateStructureTask taskStructure =
        (GenerateStructureTask) project.getTasks().getByName("testStructure");
    taskStructure.execute();

    builder = new ModuleBuilder(project, operations);
    when(styledTextOutput.style(any())).thenReturn(styledTextOutput);
    when(styledTextOutput.append(any())).thenReturn(styledTextOutput);
    builder.setStyledLogger(styledTextOutput);
  }

  @Test
  public void shouldAppendProperties() throws IOException {
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

  @Test(expected = IOException.class)
  public void shouldReturnErrorBecauseNoProperty() throws IOException {
    builder.analyticsEnabled();
  }

  @Test
  public void shouldReturnAnalyticsState() throws IOException {
    FileUtils.setGradleProperty("build/unitTest", "analytics", "true");
    assertTrue(builder.analyticsEnabled());
  }

  @Test
  public void shouldPrintVersionMissMatch() throws IOException {
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

  private void writeString(File file, String string) throws IOException {
    try (Writer writer = new FileWriter(file)) {
      writer.write(string);
    }
  }
}
