package co.com.bancolombia.task;

import static co.com.bancolombia.TestUtils.assertFilesExistsInDir;
import static co.com.bancolombia.TestUtils.createTask;
import static co.com.bancolombia.TestUtils.deleteStructure;
import static co.com.bancolombia.TestUtils.getTask;
import static co.com.bancolombia.TestUtils.getTestDir;
import static co.com.bancolombia.TestUtils.setupProject;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.com.bancolombia.exceptions.CleanDomainException;
import co.com.bancolombia.exceptions.CleanException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GenerateModelTaskTest {
  private static final String TEST_DIR = getTestDir(GenerateModelTaskTest.class);
  private static final String MODEL_DIR = TEST_DIR + "/domain/model/";
  private static final String ENTITIES =
          MODEL_DIR + "src/main/java/co/com/bancolombia/model/entities";
  private static final String SPEC =
          Path.of("src/test/resources/swagger/sample-catalog.yaml").toAbsolutePath().toString();

  private static GenerateModelTask task;

  @BeforeAll
  static void setup() throws IOException, CleanException {
    deleteStructure(Path.of(TEST_DIR));
    Project project = setupProject(GenerateModelTaskTest.class, GenerateStructureTask.class);

    GenerateStructureTask taskStructure = getTask(project, GenerateStructureTask.class);
    taskStructure.setType(GenerateStructureTask.ProjectType.REACTIVE);
    taskStructure.execute();

    ProjectBuilder.builder()
            .withName("app-service")
            .withProjectDir(new File(TEST_DIR + "/applications/app-service"))
            .withParent(project)
            .build();

    task = createTask(project, GenerateModelTask.class);
  }

  @BeforeEach
  void createModelTask() throws IOException {
    deleteDirectory(Path.of(ENTITIES));
    task.setName("");
    task.setFromOpenApi(null);
    task.setModels(null);
  }

  @AfterAll
  static void tearDown() {
    deleteStructure(Path.of(TEST_DIR));
  }

  @Test
  void shouldFailWithoutArgumentsForModel() {
    task.setName(null);
    assertThrows(IllegalArgumentException.class, () -> task.execute());
  }

  @Test
  void shouldFailWithBlankNameAndBlankSpec() {
    task.setName("  ");
    task.setFromOpenApi("  ");
    assertThrows(IllegalArgumentException.class, () -> task.execute());
  }

  @Test
  void shouldGenerateModel() throws IOException, CleanException {
    task.setName("testModel");
    task.execute();
    assertFilesExistsInDir(
            MODEL_DIR,
            "src/main/java/co/com/bancolombia/model/testmodel/gateways/TestModelRepository.java",
            "src/main/java/co/com/bancolombia/model/testmodel/TestModel.java");
  }

  @Test
  void shouldGenerateOnlyFilteredOpenApiModels() throws IOException, CleanException {
    task.setFromOpenApi(SPEC);
    task.setModels("author");
    task.execute();

    assertFilesExistsInDir(ENTITIES + "/", "Author.java");
    assertFalse(Files.exists(Path.of(ENTITIES, "response", "Book.java")));
  }

  @Test
  void shouldGenerateModelsFromOpenApiByRequestOrResponse() throws IOException, CleanException {
    task.setFromOpenApi(SPEC);
    task.execute();

    assertFilesExistsInDir(
            ENTITIES + "/", "Author.java", "request/Book.java", "response/Book.java");
    assertContains("Author.java", "package co.com.bancolombia.model.entities;");
    assertContains("request/Book.java", "package co.com.bancolombia.model.entities.request;");
    assertContains("response/Book.java", "package co.com.bancolombia.model.entities.response;");
    assertContains("request/Book.java", "public class Book");
    assertContains("response/Book.java", "public class Book");
  }

  @Test
  void shouldImportAndRemapReferencedOpenApiModels() throws IOException, CleanException {
    task.setFromOpenApi(SPEC);
    task.execute();

    assertContains("response/Book.java", "import co.com.bancolombia.model.entities.Author;");
    assertContains("response/Book.java", "private Author author");
  }


  @Test
  void shouldGenerateNamedModelAndOpenApiModelsTogether() throws IOException, CleanException {
    task.setName("customer");
    task.setFromOpenApi(SPEC);
    task.execute();

    assertFilesExistsInDir(
            MODEL_DIR, "src/main/java/co/com/bancolombia/model/customer/Customer.java");
    assertFilesExistsInDir(ENTITIES + "/", "Author.java");
  }

  @Test
  void shouldFailWhenOpenApiSpecDoesNotExist() {
    task.setFromOpenApi("does-not-exist.yaml");
    assertThrows(CleanDomainException.class, () -> task.execute());
  }

  private static void assertContains(String relativePath, String expected) throws IOException {
    String content = Files.readString(Path.of(ENTITIES, relativePath));
    assertTrue(
            content.contains(expected),
            () -> relativePath + " should contain: " + expected + "\n---\n" + content);
  }

  private static void deleteDirectory(Path dir) throws IOException {
    if (!Files.exists(dir)) {
      return;
    }
    try (var paths = Files.walk(dir)) {
      paths.sorted(java.util.Comparator.reverseOrder()).forEach(path -> path.toFile().delete());
    }
  }
}