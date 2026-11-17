package io.swagger.codegen.v3.generators;

import java.io.File;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class OpenApiModelPackageResolver {

  public static final String BASE_MODEL_PACKAGE = "baseModelPackage";

  private static final Pattern REQUEST_OR_RESPONSE_SUFFIX =
      Pattern.compile("(request|response)$", Pattern.CASE_INSENSITIVE);

  private OpenApiModelPackageResolver() {}

  /**
   * Add the corresponding package to the class
   *
   * @param baseModelPackage Name of the base package
   * @param className Contract´s model name
   * @return base package name plus it´s corresponding folder
   */
  static String resolvePackage(String baseModelPackage, String className) {
    String segment = resolveFolderSegment(className);
    if (segment.isEmpty()) {
      return baseModelPackage;
    }
    return baseModelPackage + "." + segment;
  }

  /**
   * Separate the models into 'request' and 'response' folders, respectively.
   *
   * @param className Model name
   * @return Determine whether the item should be filed under "request," "response," or neither,
   *     based on its name.
   */
  static String resolveFolderSegment(String className) {
    Matcher suffix = REQUEST_OR_RESPONSE_SUFFIX.matcher(className);
    if (suffix.find()) {
      return suffix.group(1).toLowerCase(Locale.ROOT);
    }
    return "";
  }

  /**
   * Assign the file to its corresponding folder
   *
   * @param originalClassName Contract´s model name
   * @param domainClassName File name
   * @return model file path
   */
  static String modelFilePath(String originalClassName, String domainClassName) {
    String segment = resolveFolderSegment(originalClassName);
    if (segment.isEmpty()) {
      return domainClassName;
    }
    return segment + File.separator + domainClassName;
  }

  /**
   * Clean up model names to comply with clean architecture.
   *
   * @param className Model name
   * @return model name without the word Response or Request
   */
  static String cleanModelName(String className) {
    String cleaned = REQUEST_OR_RESPONSE_SUFFIX.matcher(className).replaceFirst("");
    return cleaned.isEmpty() ? className : cleaned;
  }
}
