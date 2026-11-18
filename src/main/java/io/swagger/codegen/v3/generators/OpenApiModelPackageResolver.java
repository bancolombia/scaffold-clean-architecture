package io.swagger.codegen.v3.generators;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public final class OpenApiModelPackageResolver {

  public static final String BASE_MODEL_PACKAGE = "baseModelPackage";
  public static final String REQUEST_SCHEMA_KEYS = "openApiRequestSchemaKeys";
  public static final String RESPONSE_SCHEMA_KEYS = "openApiResponseSchemaKeys";

  private OpenApiModelPackageResolver() {}

  public static Set<String> parseSchemaKeys(Object raw) {
    if (raw == null) {
      return Set.of();
    }
    String value = raw.toString().trim();
    if (value.isEmpty()) {
      return Set.of();
    }
    return Arrays.stream(value.split(","))
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  static String resolvePackage(
      String baseModelPackage,
      String schemaKey,
      Set<String> requestSchemaKeys,
      Set<String> responseSchemaKeys) {
    String segment = resolveFolderSegment(schemaKey, requestSchemaKeys, responseSchemaKeys);
    if (segment.isEmpty()) {
      return baseModelPackage;
    }
    return baseModelPackage + "." + segment;
  }

  static String resolveFolderSegment(
      String schemaKey, Set<String> requestSchemaKeys, Set<String> responseSchemaKeys) {
    if (requestSchemaKeys.contains(schemaKey)) {
      return "request";
    }
    if (responseSchemaKeys.contains(schemaKey)) {
      return "response";
    }
    return "";
  }

  static String modelFilePath(
      String schemaKey,
      String className,
      Set<String> requestSchemaKeys,
      Set<String> responseSchemaKeys) {
    String segment = resolveFolderSegment(schemaKey, requestSchemaKeys, responseSchemaKeys);
    if (segment.isEmpty()) {
      return className;
    }
    return segment + File.separator + className;
  }

  static String folderKind(
      String schemaKey, Set<String> requestSchemaKeys, Set<String> responseSchemaKeys) {
    return resolveFolderSegment(schemaKey, requestSchemaKeys, responseSchemaKeys)
        .toUpperCase(Locale.ROOT);
  }
}
