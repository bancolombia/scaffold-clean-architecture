package co.com.bancolombia.task;

import static co.com.bancolombia.TestUtils.assertFileContains;
import static co.com.bancolombia.TestUtils.createTask;
import static co.com.bancolombia.TestUtils.deleteStructure;
import static co.com.bancolombia.TestUtils.getTask;
import static co.com.bancolombia.TestUtils.getTestDir;
import static co.com.bancolombia.TestUtils.setupProject;
import static co.com.bancolombia.task.AbstractCleanArchitectureDefaultTask.BooleanOption.FALSE;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.validations.architecture.ArchitectureValidation;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.gradle.api.Project;
import org.gradle.internal.logging.text.StyledTextOutput;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class GenerateReactiveModulesNoLombokTest {
  private static final String TEST_DIR = getTestDir(GenerateReactiveModulesNoLombokTest.class);
  private static final String APP_SERVICE_DIR = TEST_DIR + "/applications/app-service";

  @BeforeAll
  static void setup() throws IOException, CleanException {
    deleteStructure(Path.of(TEST_DIR));
    Project project =
        setupProject(GenerateReactiveModulesNoLombokTest.class, GenerateStructureTask.class);

    GenerateStructureTask taskStructure = getTask(project, GenerateStructureTask.class);
    taskStructure.setType(GenerateStructureTask.ProjectType.REACTIVE);
    taskStructure.setStatusLombok(FALSE);
    taskStructure.execute();

    Project appService =
        ProjectBuilder.builder()
            .withName("app-service")
            .withProjectDir(new File(APP_SERVICE_DIR))
            .withParent(project)
            .build();

    GenerateDrivenAdapterTask drivenAdapter = createTask(project, GenerateDrivenAdapterTask.class);
    drivenAdapter.setType("SQS");
    drivenAdapter.execute();

    GenerateEntryPointTask entryPoint = createTask(project, GenerateEntryPointTask.class);
    entryPoint.setType("SQS");
    entryPoint.execute();

    StyledTextOutput styledTextOutput = mock(StyledTextOutput.class);
    when(styledTextOutput.style(any())).thenReturn(styledTextOutput);
    when(styledTextOutput.append(any())).thenReturn(styledTextOutput);
    ModuleBuilder builder = new ModuleBuilder(project);
    builder.setStyledLogger(styledTextOutput);
    ArchitectureValidation.inject(
        builder, project.getLogger(), Set.of(project.getProjectDir(), appService.getProjectDir()));
  }

  @AfterAll
  static void tearDown() {
    deleteStructure(Path.of(TEST_DIR));
  }

  @Test
  void shouldNotReferenceLombokInGeneratedSources() throws IOException {
    // Arrange
    List<Path> sources;
    try (Stream<Path> files = Files.walk(Path.of(TEST_DIR))) {
      sources = files.filter(file -> file.toString().endsWith(".java")).toList();
    }
    // Act
    List<Path> referencingLombok = sources.stream().filter(this::referencesLombok).toList();
    // Assert
    assertTrue(
        sources.stream().anyMatch(file -> file.endsWith("ArchitectureTest.java")),
        "ArchitectureTest.java was not injected");
    assertTrue(
        referencingLombok.isEmpty(), () -> "Sources that reference Lombok: " + referencingLombok);
  }

  @Test
  void shouldCloseTheAssignmentsOfTheSQSSenderConstructor() {
    assertFileContains(
        TEST_DIR
            + "/infrastructure/driven-adapters/sqs-sender/src/main/java/co/com/bancolombia/sqs/sender/SQSSender.java",
        "this.client = client;",
        "this.properties = properties;");
  }

  @Test
  void shouldGenerateTheOperationOfTheSQSListenerBuilder() {
    // With metrics, the generated test builds the listener with an operation.
    String listener = TEST_DIR + "/infrastructure/entry-points/sqs-listener/src/";
    assertFileContains(
        listener + "main/java/co/com/bancolombia/sqs/listener/helper/SQSListener.java",
        "SQSListenerBuilder operation(final String operation)",
        "new SQSListener(this.client, this.properties, this.processor, this.operation)");
    assertFileContains(
        listener + "test/java/co/com/bancolombia/sqs/listener/helper/SQSListenerTest.java",
        ".operation(\"operation\")");
  }

  private boolean referencesLombok(Path file) {
    try {
      return Files.readString(file).contains("lombok");
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }
}
