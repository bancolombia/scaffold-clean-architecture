package io.swagger.codegen.v3.generators;

import co.com.bancolombia.exceptions.CleanDomainException;
import co.com.bancolombia.factory.validations.architecture.ArchitectureValidation;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * Resolves the domain class name of every OpenAPI schema. An explicit {@code --rename-models} entry
 * wins; otherwise the forbidden domain suffixes (Dto, Request, Response) are removed from the
 * default class name, so {@code bookResponse} becomes {@code Book}.
 */
public final class OpenApiDomainModelNaming {

  public static final String DOMAIN_CLASS_NAMES = "openApiDomainClassNames";
  private static final Pattern CLASS_NAME = Pattern.compile("[A-Z]\\w*");

  private OpenApiDomainModelNaming() {}

  /** Parses {@code schemaKey:ClassName} pairs separated by commas. */
  public static Map<String, String> parseRenames(String raw) {
    Map<String, String> renames = new LinkedHashMap<>();
    for (String entry : OpenApiModelPackageResolver.parseSchemaKeys(raw)) {
      String[] parts = entry.split(":", -1);
      if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
        throw new CleanDomainException(
            "Invalid rename-models entry '"
                + entry
                + "'. Use schemaKey:ClassName pairs, for example bookResponse:Book");
      }
      String schemaKey = parts[0].trim();
      String className = parts[1].trim();
      if (!CLASS_NAME.matcher(className).matches()) {
        throw new CleanDomainException(
            "Invalid class name '"
                + className
                + "' in rename-models: it must start with an uppercase letter and contain only"
                + " letters, digits or '_'");
      }
      if (renames.put(schemaKey, className) != null) {
        throw new CleanDomainException("Schema '" + schemaKey + "' is renamed more than once");
      }
    }
    return renames;
  }

  /**
   * Maps each schema key to its domain class name.
   *
   * @param defaultClassName the codegen class name of a schema key, before any domain renaming
   */
  public static Map<String, String> resolveClassNames(
      Collection<String> schemaKeys,
      Map<String, String> renames,
      UnaryOperator<String> defaultClassName) {
    Map<String, String> classNames = new LinkedHashMap<>();
    for (String schemaKey : schemaKeys) {
      String className = renames.get(schemaKey);
      if (className == null) {
        className =
            ArchitectureValidation.stripForbiddenSuffixes(defaultClassName.apply(schemaKey));
      }
      classNames.put(schemaKey, className);
    }
    return classNames;
  }
}
