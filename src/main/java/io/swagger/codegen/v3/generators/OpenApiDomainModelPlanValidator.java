package io.swagger.codegen.v3.generators;

import co.com.bancolombia.exceptions.CleanDomainException;
import co.com.bancolombia.factory.validations.architecture.ArchitectureValidation;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public final class OpenApiDomainModelPlanValidator {

  private static final String RENAME_HINT =
      "Use --rename-models=schemaKey:ClassName to choose another class name.";

  /** java.lang types, plus types codegen maps without an import mapping entry. */
  private static final Set<String> JAVA_TYPES =
      Set.of(
          "Object",
          "String",
          "Integer",
          "Long",
          "Short",
          "Byte",
          "Double",
          "Float",
          "Boolean",
          "Character",
          "Number",
          "Void",
          "Class",
          "Enum",
          "Record",
          "Override",
          "Deprecated",
          "OffsetDateTime",
          "BigInteger");

  private static final Set<String> LOMBOK_TYPES =
      Set.of(
          "Getter", "Setter", "Builder", "SuperBuilder", "AllArgsConstructor", "NoArgsConstructor");

  private OpenApiDomainModelPlanValidator() {}

  public static void validate(
      List<DomainModelCodegen.ModelTarget> targets,
      OpenAPI openAPI,
      Set<String> selectedSchemaKeys,
      Set<String> requestSchemaKeys,
      Set<String> responseSchemaKeys,
      Map<String, String> renames,
      boolean lombok) {
    validateFolderAssignments(openAPI, selectedSchemaKeys, requestSchemaKeys, responseSchemaKeys);
    validateKnownKeys(renames.keySet(), "rename-models", knownSchemaKeys(openAPI));
    assertUniqueClassNames(targets);
    assertValidClassNames(targets, renames, reservedClassNames(lombok));
  }

  /**
   * Types the generated models use by simple name. Codegen also imports every type in its import
   * mapping (File, Timestamp, LocalTime...) and skips schemas named like one of them.
   */
  static Set<String> reservedClassNames(boolean lombok) {
    Set<String> reserved = new HashSet<>(JAVA_TYPES);
    reserved.addAll(new DomainModelCodegen().importMapping().keySet());
    if (lombok) {
      reserved.addAll(LOMBOK_TYPES);
    }
    return reserved;
  }

  private static void validateFolderAssignments(
      OpenAPI openAPI,
      Set<String> selectedSchemaKeys,
      Set<String> requestSchemaKeys,
      Set<String> responseSchemaKeys) {
    Set<String> overlap =
        requestSchemaKeys.stream().filter(responseSchemaKeys::contains).collect(Collectors.toSet());
    if (!overlap.isEmpty()) {
      throw new CleanDomainException(
          "A schema cannot be listed in both request-models and response-models: " + overlap);
    }
    Set<String> known = knownSchemaKeys(openAPI);
    OpenApiModelSelectionResolver.validateSelectedSchemaKeys(openAPI, selectedSchemaKeys, "models");
    validateKnownKeys(requestSchemaKeys, "request-models", known);
    validateKnownKeys(responseSchemaKeys, "response-models", known);
  }

  private static void validateKnownKeys(Set<String> keys, String optionName, Set<String> known) {
    if (keys.isEmpty()) {
      return;
    }
    List<String> unknown = keys.stream().filter(key -> !known.contains(key)).sorted().toList();
    if (!unknown.isEmpty()) {
      throw new CleanDomainException(
          OpenApiModelSelectionResolver.unknownSchemaMessage(optionName, unknown, known));
    }
  }

  private static Set<String> knownSchemaKeys(OpenAPI openAPI) {
    Components components = openAPI.getComponents();
    if (components == null || components.getSchemas() == null) {
      return Set.of();
    }
    return components.getSchemas().keySet();
  }

  /**
   * Class names must be unique across packages too: types are referenced by simple name, so two
   * classes with the same name could not be told apart.
   */
  private static void assertUniqueClassNames(List<DomainModelCodegen.ModelTarget> targets) {
    // Case-insensitive: Book.java and BOOK.java are the same file on macOS and Windows
    var byClassName =
        targets.stream()
            .collect(
                Collectors.groupingBy(
                    target -> target.className().toLowerCase(Locale.ROOT),
                    TreeMap::new,
                    Collectors.toList()));
    List<String> conflicts = new ArrayList<>();
    for (var entry : byClassName.entrySet()) {
      if (entry.getValue().size() < 2) {
        continue;
      }
      String schemas =
          entry.getValue().stream()
              .map(DomainModelCodegen.ModelTarget::schemaKey)
              .sorted()
              .collect(Collectors.joining("', '", "'", "'"));
      String classNames =
          entry.getValue().stream()
              .map(DomainModelCodegen.ModelTarget::className)
              .distinct()
              .sorted()
              .collect(Collectors.joining("' / '", "'", "'"));
      conflicts.add("schemas " + schemas + " map to the same class name " + classNames);
    }
    if (!conflicts.isEmpty()) {
      conflicts.add(RENAME_HINT);
      throw new CleanDomainException(String.join(System.lineSeparator(), conflicts));
    }
  }

  private static void assertValidClassNames(
      List<DomainModelCodegen.ModelTarget> targets,
      Map<String, String> renames,
      Set<String> reservedClassNames) {
    // Renamed schemas are checked even when they are not generated in this run
    Map<String, String> classNames = new TreeMap<>(renames);
    targets.forEach(target -> classNames.put(target.schemaKey(), target.className()));
    List<String> errors = new ArrayList<>();
    for (Map.Entry<String, String> entry : classNames.entrySet()) {
      String className = entry.getValue();
      if (reservedClassNames.contains(className)) {
        errors.add(
            "schema '"
                + entry.getKey()
                + "': class name '"
                + className
                + "' clashes with a Java or Lombok type used by the generated models");
        continue;
      }
      try {
        ArchitectureValidation.validateModelName(className);
      } catch (CleanDomainException e) {
        errors.add("schema '" + entry.getKey() + "': " + e.getMessage());
      }
    }
    if (!errors.isEmpty()) {
      errors.add(RENAME_HINT);
      throw new CleanDomainException(String.join(System.lineSeparator(), errors));
    }
  }
}
