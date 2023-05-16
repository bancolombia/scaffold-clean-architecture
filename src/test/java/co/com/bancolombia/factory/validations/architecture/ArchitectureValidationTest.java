package co.com.bancolombia.factory.validations.architecture;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.FileUtilsTest.deleteStructure;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.task.GenerateStructureTask;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Stream;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.ResolvedConfiguration;
import org.gradle.api.artifacts.ResolvedDependency;
import org.gradle.internal.logging.text.StyledTextOutput;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ArchitectureValidationTest {
  public static final String BASE_PATH = "build/unitTest/applications/app-service";
  @Mock private StyledTextOutput styledTextOutput;
  @Mock private ConfigurationContainer configurations;
  @Mock private Configuration configuration;
  @Mock private ResolvedConfiguration resolvedConfiguration;
  @Mock private ResolvedDependency resolvedDependency;
  private Project project;
  private Project appService;

  @Before
  public void setup() throws IOException, CleanException {
    deleteStructure(Path.of("build/unitTest"));
    project =
        spy(
            ProjectBuilder.builder()
                .withName("cleanArchitecture")
                .withProjectDir(new File("build/unitTest"))
                .build());

    project.getTasks().create("ca", GenerateStructureTask.class);
    GenerateStructureTask generateStructureTask =
        (GenerateStructureTask) project.getTasks().getByName("ca");
    generateStructureTask.generateStructureTask();

    appService =
        spy(
            ProjectBuilder.builder()
                .withName(APP_SERVICE)
                .withProjectDir(new File(BASE_PATH))
                .withParent(project)
                .build());
    doReturn(Set.of(appService)).when(project).getAllprojects();
  }

  @Test
  public void shouldInjectTests() {
    // Arrange
    when(styledTextOutput.style(any())).thenReturn(styledTextOutput);
    when(styledTextOutput.append(any())).thenReturn(styledTextOutput);
    doReturn(configurations).when(appService).getConfigurations();
    doReturn(Stream.of(configuration)).when(configurations).stream();
    doReturn(true).when(configuration).isCanBeResolved();
    doReturn(resolvedConfiguration).when(configuration).getResolvedConfiguration();
    doReturn(Set.of(resolvedDependency))
        .when(resolvedConfiguration)
        .getFirstLevelModuleDependencies();
    doReturn("name").when(resolvedDependency).getName();
    doReturn("group").when(resolvedDependency).getModuleGroup();
    ModuleBuilder builder = new ModuleBuilder(project);
    builder.setStyledLogger(styledTextOutput);
    // Act
    ArchitectureValidation.inject(project, builder);
    // Assert
    assertTrue(
        Files.exists(
            Path.of(BASE_PATH, "/src/test/java/co/com/bancolombia/ArchitectureTest.java")));
  }
}
