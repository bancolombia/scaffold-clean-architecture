package io.swagger.codegen.v3.generators;

import static org.junit.jupiter.api.Assertions.assertThrows;

import co.com.bancolombia.exceptions.CleanDomainException;
import io.swagger.v3.parser.OpenAPIV3Parser;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class OpenApiDomainModelPlanValidatorTest {

  @Test
  void shouldFailWhenTwoSchemasTargetTheSameFile() {
    List<DomainModelCodegen.ModelTarget> targets =
        List.of(
            new DomainModelCodegen.ModelTarget(
                "author", "Author", "co.com.bancolombia.model.entities"),
            new DomainModelCodegen.ModelTarget(
                "Author", "Author", "co.com.bancolombia.model.entities"));

    var parsed =
        new OpenAPIV3Parser()
            .readLocation(
                Path.of("src/test/resources/swagger/duplicate-target.yaml")
                    .toAbsolutePath()
                    .toString(),
                null,
                null);

    assertThrows(
        CleanDomainException.class,
        () ->
            OpenApiDomainModelPlanValidator.validate(
                targets, parsed.getOpenAPI(), Set.of(), Set.of(), Set.of()));
  }

  @Test
  void shouldFailWhenSchemaIsListedInBothFolders() {
    var parsed =
        new OpenAPIV3Parser()
            .readLocation(
                Path.of("src/test/resources/swagger/sample-catalog.yaml")
                    .toAbsolutePath()
                    .toString(),
                null,
                null);
    List<DomainModelCodegen.ModelTarget> targets =
        DomainModelCodegen.resolveTargets(
            parsed.getOpenAPI(),
            Set.of(),
            "co.com.bancolombia.model.entities",
            Set.of("bookSearch"),
            Set.of("bookSearch"));

    assertThrows(
        CleanDomainException.class,
        () ->
            OpenApiDomainModelPlanValidator.validate(
                targets,
                parsed.getOpenAPI(),
                Set.of(),
                Set.of("bookSearch"),
                Set.of("bookSearch")));
  }
}
