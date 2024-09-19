package co.com.bancolombia.factory.validations.architecture;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.TestUtils.deleteStructure;
import static co.com.bancolombia.TestUtils.getTask;
import static co.com.bancolombia.TestUtils.getTestDir;
import static co.com.bancolombia.TestUtils.setupProject;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import co.com.bancolombia.exceptions.CleanDomainException;
import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.task.GenerateStructureTask;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
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
class ArchitectureValidationTest {
  private static final String TEST_DIR = getTestDir(ArchitectureValidationTest.class);
  public static final String BASE_PATH = TEST_DIR + "/applications/app-service";
  public static final String TEST_FILE = "/src/test/java/co/com/bancolombia/ArchitectureTest.java";
  @Mock private StyledTextOutput styledTextOutput;
  private Project project;

  @BeforeEach
  public void setup() {
    deleteStructure(Path.of(TEST_DIR));
  }

  public void mocks() throws CleanException, IOException {
    project = spy(setupProject(ArchitectureValidationTest.class, GenerateStructureTask.class));
    GenerateStructureTask generateStructureTask = getTask(project, GenerateStructureTask.class);
    generateStructureTask.executeBaseTask();

    Project appService =
        ProjectBuilder.builder()
            .withName(APP_SERVICE)
            .withProjectDir(new File(BASE_PATH))
            .withParent(project)
            .build();

    doReturn(Set.of(appService)).when(project).getAllprojects();
  }

  @AfterAll
  public static void tearDown() {
    deleteStructure(Path.of(TEST_DIR));
  }

  @Test
  void shouldInjectTests() throws IOException, CleanException {
    // Arrange
    mocks();
    Path testFile = Path.of(BASE_PATH, TEST_FILE);
    Files.deleteIfExists(testFile);
    when(styledTextOutput.style(any())).thenReturn(styledTextOutput);
    when(styledTextOutput.append(any())).thenReturn(styledTextOutput);
    ModuleBuilder builder = new ModuleBuilder(project);
    builder.setStyledLogger(styledTextOutput);
    // Act
    ArchitectureValidation.inject(project, builder);
    // Assert
    assertTrue(Files.exists(testFile));
  }

  @Test
  void shouldFailDomainNameHaveTechSuffix() {
    // Arrange
    // Assert
    assertThrows(
        CleanDomainException.class,
        () -> {
          // Act
          ArchitectureValidation.validateModelName("SampleRequest");
        });
  }

  @Test
  void shouldFailDomainNameHaveTechName() {
    // Arrange
    // Assert
    assertThrows(
        CleanDomainException.class,
        () -> {
          // Act
          ArchitectureValidation.validateModelName("KafkaModel");
        });
  }

  @Test
  void shouldFailUseCaseNameHaveTechName() {
    // Arrange
    // Assert
    assertThrows(
        CleanDomainException.class,
        () -> {
          // Act
          ArchitectureValidation.validateModelName("RabbitUseCase");
        });
  }
}
