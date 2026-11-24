package io.swagger.codegen.v3.generators;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.com.bancolombia.exceptions.CleanDomainException;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class OpenApiDomainModelPlanValidatorTest {
  private static final String BASE_PACKAGE = "co.com.bancolombia.model.entities";
  private static final Set<String> NO_KEYS = Set.of();

  @Test
  void shouldFailWhenTwoSchemasTargetTheSameClassName() {
    List<DomainModelCodegen.ModelTarget> targets =
        List.of(
            new DomainModelCodegen.ModelTarget("author", "Author", BASE_PACKAGE),
            new DomainModelCodegen.ModelTarget("Author", "Author", BASE_PACKAGE + ".response"));

    CleanDomainException error = assertInvalid(targets, parse("duplicate-target.yaml"), Map.of());
    assertTrue(error.getMessage().contains("map to the same class name 'Author'"));
    assertTrue(error.getMessage().contains("--rename-models"));
  }

  @Test
  void shouldFailWhenSchemaIsListedInBothFolders() {
    OpenAPI openAPI = parse("sample-catalog.yaml");
    List<DomainModelCodegen.ModelTarget> targets = targets(openAPI, Map.of());

    Set<String> bothFolders = Set.of("bookSearch");

    assertThrows(
        CleanDomainException.class,
        () ->
            OpenApiDomainModelPlanValidator.validate(
                targets, openAPI, NO_KEYS, bothFolders, bothFolders, Map.of(), true));
  }

  @Test
  void shouldFailWhenRenamedSchemaIsUnknown() {
    OpenAPI openAPI = parse("sample-catalog.yaml");
    Map<String, String> renames = Map.of("unknownSchema", "Something");
    List<DomainModelCodegen.ModelTarget> targets = targets(openAPI, renames);

    CleanDomainException error = assertInvalid(targets, openAPI, renames);
    assertTrue(error.getMessage().contains("Unknown schema name(s) in rename-models"));
  }

  @Test
  void shouldReportEveryInvalidClassName() {
    List<DomainModelCodegen.ModelTarget> targets =
        List.of(
            new DomainModelCodegen.ModelTarget("a", "AwsAccount", BASE_PACKAGE),
            new DomainModelCodegen.ModelTarget("b", "KafkaEvent", BASE_PACKAGE));

    CleanDomainException error = assertInvalid(targets, parse("sample-catalog.yaml"), Map.of());
    assertTrue(error.getMessage().contains("schema 'a'"));
    assertTrue(error.getMessage().contains("schema 'b'"));
  }

  @Test
  void shouldFailWhenClassNamesDifferOnlyByCase() {
    List<DomainModelCodegen.ModelTarget> targets =
        List.of(
            new DomainModelCodegen.ModelTarget("bookDto", "Book", BASE_PACKAGE),
            new DomainModelCodegen.ModelTarget("BOOK", "BOOK", BASE_PACKAGE));

    CleanDomainException error = assertInvalid(targets, parse("sample-catalog.yaml"), Map.of());
    assertTrue(error.getMessage().contains("'BOOK' / 'Book'"), error.getMessage());
  }

  @Test
  void shouldRejectSchemaKeysThatCodegenMapsToJavaTypes() {
    List<DomainModelCodegen.ModelTarget> targets =
        List.of(
            new DomainModelCodegen.ModelTarget("Date", "java.util.Date", BASE_PACKAGE),
            new DomainModelCodegen.ModelTarget("author", "Author", BASE_PACKAGE));

    CleanDomainException error = assertInvalid(targets, parse("sample-catalog.yaml"), Map.of());
    assertTrue(error.getMessage().contains("schema 'Date'"), error.getMessage());
    assertTrue(error.getMessage().contains("java.util.Date"), error.getMessage());
    assertTrue(error.getMessage().contains("Rename these schemas"), error.getMessage());
  }

  @Test
  void shouldRejectClassNamesThatClashWithJavaOrLombokTypes() {
    OpenAPI openAPI = parse("sample-catalog.yaml");
    Map<String, String> renames = Map.of("author", "List");
    List<DomainModelCodegen.ModelTarget> targets = targets(openAPI, renames);

    CleanDomainException error = assertInvalid(targets, openAPI, renames);
    assertTrue(error.getMessage().contains("clashes with a Java or Lombok type"));
  }

  @Test
  void shouldReserveCodegenImportMappingsAndLombokTypesOnlyWithLombok() {
    Set<String> withLombok = OpenApiDomainModelPlanValidator.reservedClassNames(true);
    Set<String> withoutLombok = OpenApiDomainModelPlanValidator.reservedClassNames(false);

    assertTrue(withLombok.containsAll(Set.of("File", "Timestamp", "LocalTime", "List", "Builder")));
    assertTrue(withoutLombok.containsAll(Set.of("File", "Timestamp", "List")));
    assertFalse(withoutLombok.contains("Builder"));
    // Added to the import mapping by processOpts, which codegen runs before generating
    assertTrue(withoutLombok.contains("Objects"));
  }

  @Test
  void shouldAcceptLombokNamesWhenLombokIsDisabled() {
    OpenAPI openAPI = parse("sample-catalog.yaml");
    Map<String, String> renames = Map.of("author", "Builder");
    List<DomainModelCodegen.ModelTarget> targets = targets(openAPI, renames);

    assertDoesNotThrow(
        () ->
            OpenApiDomainModelPlanValidator.validate(
                targets, openAPI, NO_KEYS, NO_KEYS, NO_KEYS, renames, false));
  }

  @Test
  void shouldValidateRenamedSchemasThatAreNotGenerated() {
    Map<String, String> renames = Map.of("author", "AwsAuthor");

    assertInvalid(List.of(), parse("sample-catalog.yaml"), renames);
  }

  /** Validates with every schema selected and without request or response folders. */
  private static CleanDomainException assertInvalid(
      List<DomainModelCodegen.ModelTarget> targets, OpenAPI openAPI, Map<String, String> renames) {
    return assertThrows(
        CleanDomainException.class,
        () ->
            OpenApiDomainModelPlanValidator.validate(
                targets, openAPI, NO_KEYS, NO_KEYS, NO_KEYS, renames, true));
  }

  private static List<DomainModelCodegen.ModelTarget> targets(
      OpenAPI openAPI, Map<String, String> renames) {
    return DomainModelCodegen.resolveTargets(
        openAPI,
        Set.of(),
        BASE_PACKAGE,
        Set.of(),
        Set.of(),
        DomainModelCodegen.resolveClassNames(openAPI, renames));
  }

  private static OpenAPI parse(String fileName) {
    return new OpenAPIV3Parser()
        .readLocation(
            Path.of("src/test/resources/swagger", fileName).toAbsolutePath().toString(), null, null)
        .getOpenAPI();
  }
}
