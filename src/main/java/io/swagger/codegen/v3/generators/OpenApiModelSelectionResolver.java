package io.swagger.codegen.v3.generators;

import co.com.bancolombia.exceptions.CleanDomainException;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/** Expands {@code --models} with schema dependencies and validates keys against the contract. */
public final class OpenApiModelSelectionResolver {
  private static final Pattern COMPONENTS_SCHEMA_REF = Pattern.compile("#/components/schemas/(.+)");

  private OpenApiModelSelectionResolver() {}

  public static Set<String> parseModelsFilter(String modelsFilter) {
    if (modelsFilter == null || modelsFilter.isBlank()) {
      return Set.of();
    }
    return Arrays.stream(modelsFilter.split(","))
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  public static void validateSelectedSchemaKeys(
      OpenAPI openAPI, Set<String> selectedKeys, String optionName) {
    if (selectedKeys.isEmpty()) {
      return;
    }
    Set<String> known = knownSchemaKeys(openAPI);
    List<String> unknown =
        selectedKeys.stream().filter(key -> !known.contains(key)).sorted().toList();
    if (!unknown.isEmpty()) {
      throw new CleanDomainException(unknownSchemaMessage(optionName, unknown, known));
    }
  }

  public static Set<String> expandSelectedSchemas(OpenAPI openAPI, Set<String> selectedKeys) {
    if (selectedKeys.isEmpty()) {
      return Set.of();
    }
    Map<String, Schema> schemas = componentsSchemas(openAPI);
    Set<String> expanded = new LinkedHashSet<>(selectedKeys);
    Deque<String> queue = new ArrayDeque<>(selectedKeys);
    while (!queue.isEmpty()) {
      String schemaKey = queue.removeFirst();
      Schema schema = schemas.get(schemaKey);
      if (schema == null) {
        continue;
      }
      for (String dependency : collectReferencedSchemaKeys(schema)) {
        if (schemas.containsKey(dependency) && expanded.add(dependency)) {
          queue.addLast(dependency);
        }
      }
    }
    return expanded;
  }

  public static Set<String> allOfParentClassNames(
      OpenAPI openAPI, Function<String, String> schemaKeyToClassName) {
    Set<String> parentKeys = new LinkedHashSet<>();
    for (Schema schema : componentsSchemas(openAPI).values()) {
      if (schema.getAllOf() == null) {
        continue;
      }
      for (Object part : schema.getAllOf()) {
        if (!(part instanceof Schema allOfPart)) {
          continue;
        }
        String parentKey = schemaKeyFromRef(allOfPart.get$ref());
        if (parentKey != null) {
          parentKeys.add(parentKey);
        }
      }
    }
    return parentKeys.stream().map(schemaKeyToClassName).collect(Collectors.toSet());
  }

  static String unknownSchemaMessage(
      String optionName, List<String> unknown, Set<String> knownSchemaKeys) {
    List<String> available = knownSchemaKeys.stream().sorted().toList();
    return "Unknown schema name(s) in "
        + optionName
        + ": "
        + unknown
        + ". Available schema names: "
        + available;
  }

  private static Set<String> collectReferencedSchemaKeys(Schema schema) {
    Set<String> refs = new LinkedHashSet<>();
    collectReferencedSchemaKeys(schema, refs);
    return refs;
  }

  private static void collectReferencedSchemaKeys(Schema schema, Set<String> refs) {
    if (schema == null) {
      return;
    }
    String direct = schemaKeyFromRef(schema.get$ref());
    if (direct != null) {
      refs.add(direct);
      return;
    }
    visitComposed(schema.getAllOf(), refs);
    visitComposed(schema.getOneOf(), refs);
    visitComposed(schema.getAnyOf(), refs);
    if (schema.getProperties() != null) {
      for (Object property : schema.getProperties().values()) {
        if (property instanceof Schema propertySchema) {
          collectReferencedSchemaKeys(propertySchema, refs);
        }
      }
    }
    collectReferencedSchemaKeys(schema.getItems(), refs);
    Object additionalProperties = schema.getAdditionalProperties();
    if (additionalProperties instanceof Schema additionalSchema) {
      collectReferencedSchemaKeys(additionalSchema, refs);
    }
  }

  private static void visitComposed(List<?> parts, Set<String> refs) {
    if (parts == null) {
      return;
    }
    for (Object part : parts) {
      if (part instanceof Schema schema) {
        collectReferencedSchemaKeys(schema, refs);
      }
    }
  }

  private static String schemaKeyFromRef(String ref) {
    if (ref == null || ref.isBlank()) {
      return null;
    }
    Matcher matcher = COMPONENTS_SCHEMA_REF.matcher(ref);
    return matcher.matches() ? matcher.group(1) : null;
  }

  private static Set<String> knownSchemaKeys(OpenAPI openAPI) {
    return componentsSchemas(openAPI).keySet();
  }

  private static Map<String, Schema> componentsSchemas(OpenAPI openAPI) {
    if (openAPI == null) {
      return Map.of();
    }
    Components components = openAPI.getComponents();
    if (components == null || components.getSchemas() == null) {
      return Map.of();
    }
    return components.getSchemas();
  }
}
