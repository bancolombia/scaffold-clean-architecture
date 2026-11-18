package io.swagger.codegen.v3.generators;

import co.com.bancolombia.exceptions.CleanDomainException;
import co.com.bancolombia.factory.validations.architecture.ArchitectureValidation;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class OpenApiDomainModelPlanValidator {

  private OpenApiDomainModelPlanValidator() {}

  public static void validate(
      List<DomainModelCodegen.ModelTarget> targets,
      OpenAPI openAPI,
      Set<String> requestSchemaKeys,
      Set<String> responseSchemaKeys) {
    validateFolderAssignments(openAPI, requestSchemaKeys, responseSchemaKeys);
    assertNoDuplicateOutputFiles(targets);
    for (DomainModelCodegen.ModelTarget target : targets) {
      ArchitectureValidation.validateModelName(target.className());
    }
  }

  private static void validateFolderAssignments(
      OpenAPI openAPI, Set<String> requestSchemaKeys, Set<String> responseSchemaKeys) {
    Set<String> overlap =
        requestSchemaKeys.stream().filter(responseSchemaKeys::contains).collect(Collectors.toSet());
    if (!overlap.isEmpty()) {
      throw new CleanDomainException(
          "A schema cannot be listed in both request-models and response-models: " + overlap);
    }
    Set<String> known = knownSchemaKeys(openAPI);
    List<String> unknown = new ArrayList<>();
    requestSchemaKeys.stream().filter(key -> !known.contains(key)).forEach(unknown::add);
    responseSchemaKeys.stream().filter(key -> !known.contains(key)).forEach(unknown::add);
    if (!unknown.isEmpty()) {
      throw new CleanDomainException(
          "Unknown schema name(s) in request-models/response-models: " + unknown);
    }
  }

  private static Set<String> knownSchemaKeys(OpenAPI openAPI) {
    Components components = openAPI.getComponents();
    if (components == null || components.getSchemas() == null) {
      return Set.of();
    }
    return components.getSchemas().keySet();
  }

  private static void assertNoDuplicateOutputFiles(List<DomainModelCodegen.ModelTarget> targets) {
    var byPath =
        targets.stream()
            .collect(Collectors.groupingBy(DomainModelCodegen.ModelTarget::relativePath));
    List<String> conflicts = new ArrayList<>();
    for (var entry : byPath.entrySet()) {
      if (entry.getValue().size() < 2) {
        continue;
      }
      String schemas =
          entry.getValue().stream()
              .map(DomainModelCodegen.ModelTarget::schemaKey)
              .collect(Collectors.joining("', '", "'", "'"));
      conflicts.add("schemas " + schemas + " map to the same output file '" + entry.getKey() + "'");
    }
    if (!conflicts.isEmpty()) {
      throw new CleanDomainException(String.join(System.lineSeparator(), conflicts));
    }
  }
}
