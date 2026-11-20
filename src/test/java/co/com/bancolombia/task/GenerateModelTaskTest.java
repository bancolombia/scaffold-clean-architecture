package co.com.bancolombia.task;

import static co.com.bancolombia.TestUtils.assertFilesExistsInDir;
import static co.com.bancolombia.TestUtils.createTask;
import static co.com.bancolombia.TestUtils.deleteStructure;
import static co.com.bancolombia.TestUtils.getTask;
import static co.com.bancolombia.TestUtils.getTestDir;
import static co.com.bancolombia.TestUtils.setupProject;
import static org.junit.jupiter.api.Assertions.*;

import co.com.bancolombia.exceptions.CleanDomainException;
import co.com.bancolombia.exceptions.CleanException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.*;

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
    task.setRequestModels(null);
    task.setResponseModels(null);
  }

  @AfterAll
  static void tearDown() {
    deleteStructure(Path.of(TEST_DIR));
  }

  @Test
  void generatedModelsWithImports() throws IOException, CleanException {

    task.setFromOpenApi(SPEC);
    task.setResponseModels("bookDetail");
    task.execute();

    Path generatedRoot =
        Path.of(TEST_DIR, "domain/model/src/main/java/co/com/bancolombia/model/entities");

    assertFilesExistsInDir(
        MODEL_DIR, "src/main/java/co/com/bancolombia/model/entities/response/BookDetail.java");

    String book = Files.readString(generatedRoot.resolve("response/BookDetail.java"));
    assertTrue(book.contains("import java.util.List;"));
    assertTrue(book.contains("import java.time.OffsetDateTime;"));
    assertTrue(book.contains("import java.math.BigDecimal;"));
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
    assertFalse(Files.exists(Path.of(ENTITIES, "response", "BookDetail.java")));
  }

  @Test
  void shouldKeepSchemasInEntitiesPackageWhenNoFolderOptions() throws IOException, CleanException {
    task.setFromOpenApi(SPEC);
    task.execute();

    assertFilesExistsInDir(ENTITIES + "/", "Author.java", "BookSearch.java", "BookDetail.java");
    assertFalse(Files.exists(Path.of(ENTITIES, "request")));
    assertFalse(Files.exists(Path.of(ENTITIES, "response")));
  }

  @Test
  void shouldGenerateModelsFromOpenApiByRequestOrResponse() throws IOException, CleanException {
    task.setFromOpenApi(SPEC);
    task.setRequestModels("bookSearch");
    task.setResponseModels("bookDetail");
    task.execute();

    assertFilesExistsInDir(
        ENTITIES + "/", "Author.java", "request/BookSearch.java", "response/BookDetail.java");
    assertContains("Author.java", "package co.com.bancolombia.model.entities;");
    assertContains("request/BookSearch.java", "package co.com.bancolombia.model.entities.request;");
    assertContains(
        "response/BookDetail.java", "package co.com.bancolombia.model.entities.response;");
    assertContains("request/BookSearch.java", "public class BookSearch");
    assertContains("response/BookDetail.java", "public class BookDetail");
  }

  @Test
  void shouldImportAndRemapReferencedOpenApiModels() throws IOException, CleanException {
    task.setFromOpenApi(SPEC);
    task.setResponseModels("bookDetail");
    task.execute();

    assertContains("response/BookDetail.java", "import co.com.bancolombia.model.entities.Author;");
    assertContains("response/BookDetail.java", "private Author author");
  }

  @Test
  void shouldGenerateAllModelsWithBlankModels() throws IOException, CleanException {
    task.setFromOpenApi(SPEC);
    task.setModels(" ");
    task.execute();

    assertFilesExistsInDir(ENTITIES + "/", "Author.java", "BookSearch.java", "BookDetail.java");
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

  @Test
  void shouldRestorePreviousModelsProperty() throws IOException, CleanException {
    System.setProperty("models", "previous");
    try {
      task.setFromOpenApi(SPEC);
      task.setModels("author");
      task.execute();
      Assertions.assertEquals("previous", System.getProperty("models"));
    } finally {
      System.clearProperty("models");
    }
  }

  @Test
  void shouldGenerateTopLevelEnum() throws IOException, CleanException {
    task.setFromOpenApi(SPEC);
    task.execute();
    assertContains("LoanStatus.java", "public enum LoanStatus");
    assertContains("LoanStatus.java", "ACTIVE(\"ACTIVE\")");
    assertContains("LoanStatus.java", "private final String value;");
    assertContains("LoanStatus.java", "LoanStatus(String value)");
    assertContains("LoanRecord.java", "private LoanStatus status;");
  }

  @Test
  void shouldGenerateOneOfComposedModelsAsInterface() throws IOException, CleanException {
    task.setFromOpenApi(SPEC);
    task.setModels("identifierOneOf,isbnIdentifier,libraryCardId");
    task.execute();
    assertContains("IdentifierOneOf.java", "public interface IdentifierOneOf");
    assertNotContains("IdentifierOneOf.java", "@Getter");
    assertNotContains("IdentifierOneOf.java", "public class");
    assertContains("IsbnIdentifier.java", "implements IdentifierOneOf");
    assertContains("LibraryCardId.java", "implements IdentifierOneOf");
  }

  @Test
  void shouldGenerateAnyOfComposedModelsAsInterface() throws IOException, CleanException {
    task.setFromOpenApi(SPEC);
    task.setModels("fulfillmentAnyOf,storePickup,shipmentAddress,digitalDownload");
    task.execute();
    assertContains("FulfillmentAnyOf.java", "public interface FulfillmentAnyOf");
    assertContains("StorePickup.java", "implements FulfillmentAnyOf");
  }

  @Test
  void shouldUseSuperBuilderForAllOfInheritance() throws IOException, CleanException {
    task.setFromOpenApi(SPEC);
    task.execute();

    assertContains("Child.java", "public class Child extends Base");
    assertContains("Child.java", "@SuperBuilder(toBuilder = true)");
    assertContains("Child.java", "import lombok.experimental.SuperBuilder;");
    assertNotContains("Child.java", "@Builder(");
    assertNotContains("Child.java", "private String id;");
    assertContains("Base.java", "@SuperBuilder(toBuilder = true)");
  }

  private void assertNotContains(String relativePath, String unexpected) throws IOException {
    String content = Files.readString(Path.of(ENTITIES, relativePath));
    assertFalse(
        content.contains(unexpected),
        () -> relativePath + " should not contain: " + unexpected + "\n---\n" + content);
  }

  @Test
  void shouldRemapAndImportRenamedParentFromAnotherPackage() throws IOException, CleanException {
    task.setFromOpenApi(SPEC);
    task.setResponseModels("auditTrail");
    task.execute();

    assertContains("LoanRecord.java", "public class LoanRecord extends AuditTrail");
    assertContains(
        "LoanRecord.java", "import co.com.bancolombia.model.entities.response.AuditTrail;");
    assertContains("Base.java", "@SuperBuilder(toBuilder = true)");
  }

  @Test
  void shouldKeepBuilderForModelsWithoutInheritance() throws IOException, CleanException {
    task.setFromOpenApi(SPEC);
    task.execute();

    assertContains("Author.java", "@Builder(toBuilder = true)");
    assertContains("Author.java", "@AllArgsConstructor");
    assertNotContains("Author.java", "SuperBuilder");
  }

  private static String spec(String fileName) {
    return Path.of("src/test/resources/swagger", fileName).toAbsolutePath().toString();
  }

  @Test
  void shouldNotWriteAnyFileWhenAModelNameIsInvalid() {
    task.setFromOpenApi(spec("invalid-name.yaml"));

    assertThrows(CleanDomainException.class, () -> task.execute());
    assertFalse(Files.exists(Path.of(ENTITIES)), "No model should be generated");
  }

  @Test
  void shouldFailWhenTwoSchemasGenerateTheSameFile() {
    task.setFromOpenApi(spec("duplicate-target.yaml"));

    assertThrows(CleanDomainException.class, () -> task.execute());
    assertFalse(Files.exists(Path.of(ENTITIES)));
  }

  @Test
  void shouldNotOverwriteExistingModels() throws IOException, CleanException {
    Path existing = Path.of(ENTITIES, "Author.java");
    Files.createDirectories(existing.getParent());
    Files.writeString(existing, "// custom content");

    task.setFromOpenApi(SPEC);
    task.execute();

    assertEquals("// custom content", Files.readString(existing));
    assertFilesExistsInDir(ENTITIES + "/", "BookSearch.java", "BookDetail.java");
  }

  @Test
  void shouldReportAnalyticsTypeOnlyWhenNameIsSet() {
    task.setName(null);
    assertTrue(task.resolveAnalyticsType().isEmpty());

    task.setName("customer");
    assertEquals(Optional.of("customer"), task.resolveAnalyticsType());
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
