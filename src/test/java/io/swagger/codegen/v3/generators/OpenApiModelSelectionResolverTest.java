package io.swagger.codegen.v3.generators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.com.bancolombia.exceptions.CleanDomainException;
import io.swagger.v3.parser.OpenAPIV3Parser;
import java.nio.file.Path;
import java.util.Set;
import org.junit.jupiter.api.Test;

class OpenApiModelSelectionResolverTest {

  @Test
  void shouldExpandAllOfParentAndPropertyReferences() {
    var parsed =
        new OpenAPIV3Parser()
            .readLocation(
                Path.of("src/test/resources/swagger/sample-catalog.yaml")
                    .toAbsolutePath()
                    .toString(),
                null,
                null);
    Set<String> expanded =
        OpenApiModelSelectionResolver.expandSelectedSchemas(
            parsed.getOpenAPI(), Set.of("child", "bookDetail"));
    assertTrue(expanded.contains("child"));
    assertTrue(expanded.contains("base"));
    assertTrue(expanded.contains("bookDetail"));
    assertTrue(expanded.contains("author"));
  }

  @Test
  void shouldFailForUnknownModelsFilterKey() {
    var parsed =
        new OpenAPIV3Parser()
            .readLocation(
                Path.of("src/test/resources/swagger/sample-catalog.yaml")
                    .toAbsolutePath()
                    .toString(),
                null,
                null);
    var openAPI = parsed.getOpenAPI();
    Set<String> selectedKeys = Set.of("Author");
    CleanDomainException error =
        assertThrows(
            CleanDomainException.class,
            () ->
                OpenApiModelSelectionResolver.validateSelectedSchemaKeys(
                    openAPI, selectedKeys, "models"));
    assertTrue(error.getMessage().contains("Unknown schema name(s) in models"));
    assertTrue(error.getMessage().contains("Author"));
    assertTrue(error.getMessage().contains("author"));
  }

  @Test
  void shouldDetectAllOfParentClassNamesAcrossSpec() {
    var parsed =
        new OpenAPIV3Parser()
            .readLocation(
                Path.of("src/test/resources/swagger/sample-catalog.yaml")
                    .toAbsolutePath()
                    .toString(),
                null,
                null);
    DomainModelCodegen codegen = new DomainModelCodegen();
    Set<String> parents =
        OpenApiModelSelectionResolver.allOfParentClassNames(
            parsed.getOpenAPI(), codegen::toModelName);
    assertEquals(Set.of("Base", "AuditTrail"), parents);
  }
}
