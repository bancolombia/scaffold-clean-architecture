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

  @Test
  void shouldFailWhenTwoSchemasTargetTheSameClassName() {
    List<DomainModelCodegen.ModelTarget> targets =
        List.of(
            new DomainModelCodegen.ModelTarget("author", "Author", BASE_PACKAGE),
            new DomainModelCodegen.ModelTarget("Author", "Author", BASE_PACKAGE + ".response"));

    CleanDomainException error =
        assertThrows(
            CleanDomainException.class,
            () ->
                OpenApiDomainModelPlanValidator.validate(
                    targets,
                    parse("duplicate-target.yaml"),
                    Set.of(),
                    Set.of(),
                    Set.of(),
                    Map.of(),
                    true));
    assertTrue(error.getMessage().contains("map to the same class name 'Author'"));
    assertTrue(error.getMessage().contains("--rename-models"));
  }

  @Test
  void shouldFailWhenSchemaIsListedInBothFolders() {
    OpenAPI openAPI = parse("sample-catalog.yaml");
    List<DomainModelCodegen.ModelTarget> targets = targets(openAPI, Map.of());

    assertThrows(
        CleanDomainException.class,
        () ->
            OpenApiDomainModelPlanValidator.validate(
                targets,
                openAPI,
                Set.of(),
                Set.of("bookSearch"),
                Set.of("bookSearch"),
                Map.of(),
                true));
  }

  @Test
  void shouldFailWhenRenamedSchemaIsUnknown() {
    OpenAPI openAPI = parse("sample-catalog.yaml");
    Map<String, String> renames = Map.of("unknownSchema", "Something");
    List<DomainModelCodegen.ModelTarget> targets = targets(openAPI, renames);

    CleanDomainException error =
        assertThrows(
            CleanDomainException.class,
            () ->
                OpenApiDomainModelPlanValidator.validate(
                    targets, openAPI, Set.of(), Set.of(), Set.of(), renames, true));
    assertTrue(error.getMessage().contains("Unknown schema name(s) in rename-models"));
  }

  @Test
  void shouldReportEveryInvalidClassName() {
    List<DomainModelCodegen.ModelTarget> targets =
        List.of(
            new DomainModelCodegen.ModelTarget("a", "AwsAccount", BASE_PACKAGE),
            new DomainModelCodegen.ModelTarget("b", "KafkaEvent", BASE_PACKAGE));

    CleanDomainException error =
        assertThrows(
            CleanDomainException.class,
            () ->
                OpenApiDomainModelPlanValidator.validate(
                    targets,
                    parse("sample-catalog.yaml"),
                    Set.of(),
                    Set.of(),
                    Set.of(),
                    Map.of(),
                    true));
    assertTrue(error.getMessage().contains("schema 'a'"));
    assertTrue(error.getMessage().contains("schema 'b'"));
  }

  @Test
  void shouldFailWhenClassNamesDifferOnlyByCase() {
    List<DomainModelCodegen.ModelTarget> targets =
        List.of(
            new DomainModelCodegen.ModelTarget("bookDto", "Book", BASE_PACKAGE),
            new DomainModelCodegen.ModelTarget("BOOK", "BOOK", BASE_PACKAGE));

    CleanDomainException error =
        assertThrows(
            CleanDomainException.class,
            () ->
                OpenApiDomainModelPlanValidator.validate(
                    targets,
                    parse("sample-catalog.yaml"),
                    Set.of(),
                    Set.of(),
                    Set.of(),
                    Map.of(),
                    true));
    assertTrue(error.getMessage().contains("'BOOK' / 'Book'"), error.getMessage());
  }

  @Test
  void shouldRejectClassNamesThatClashWithJavaOrLombokTypes() {
    OpenAPI openAPI = parse("sample-catalog.yaml");
    Map<String, String> renames = Map.of("author", "List");
    List<DomainModelCodegen.ModelTarget> targets = targets(openAPI, renames);

    CleanDomainException error =
        assertThrows(
            CleanDomainException.class,
            () ->
                OpenApiDomainModelPlanValidator.validate(
                    targets, openAPI, Set.of(), Set.of(), Set.of(), renames, true));
    assertTrue(error.getMessage().contains("clashes with a Java or Lombok type"));
  }

  @Test
  void shouldReserveCodegenImportMappingsAndLombokTypesOnlyWithLombok() {
    Set<String> withLombok = OpenApiDomainModelPlanValidator.reservedClassNames(true);
    Set<String> withoutLombok = OpenApiDomainModelPlanValidator.reservedClassNames(false);

    assertTrue(withLombok.containsAll(Set.of("File", "Timestamp", "LocalTime", "List", "Builder")));
    assertTrue(withoutLombok.containsAll(Set.of("File", "Timestamp", "List")));
    assertFalse(withoutLombok.contains("Builder"));
    assertFalse(withLombok.contains("Objects"));
  }

  @Test
  void shouldAcceptLombokNamesWhenLombokIsDisabled() {
    OpenAPI openAPI = parse("sample-catalog.yaml");
    Map<String, String> renames = Map.of("author", "Builder");
    List<DomainModelCodegen.ModelTarget> targets = targets(openAPI, renames);

    assertDoesNotThrow(
        () ->
            OpenApiDomainModelPlanValidator.validate(
                targets, openAPI, Set.of(), Set.of(), Set.of(), renames, false));
  }

  @Test
  void shouldValidateRenamedSchemasThatAreNotGenerated() {
    Map<String, String> renames = Map.of("author", "AwsAuthor");

    assertThrows(
        CleanDomainException.class,
        () ->
            OpenApiDomainModelPlanValidator.validate(
                List.of(),
                parse("sample-catalog.yaml"),
                Set.of(),
                Set.of(),
                Set.of(),
                renames,
                true));
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
