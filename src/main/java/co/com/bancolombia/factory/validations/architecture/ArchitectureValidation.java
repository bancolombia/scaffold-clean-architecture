package co.com.bancolombia.factory.validations.architecture;

import co.com.bancolombia.Constants;
import co.com.bancolombia.exceptions.CleanDomainException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.utils.FileUtils;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.gradle.api.logging.Logger;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ArchitectureValidation {
  private static final String SKIP_PROP = "arch.unit.skip";
  private static final String FORBIDDEN_DOMAIN_SUFFIXES_PROP = "arch.unit.forbiddenDomainSuffixes";
  private static final String FORBIDDEN_DOMAIN_SUFFIXES = "dto,request,response";
  private static final String FORBIDDEN_DOMAIN_CLASS_NAMES_PROP =
      "arch.unit.forbiddenDomainClassNames";
  private static final String FORBIDDEN_DOMAIN_NAMES =
      "rabbit,sqs,sns,ibm,dynamo,aws,mysql,postgres,redis,mongo,rsocket,r2dbc,http,kms,s3,graphql,kafka,kafkastrimzi";

  public static void inject(ModuleBuilder builder, Logger logger, Set<File> files) {
    if (!FileUtils.readBooleanProperty(SKIP_PROP)) {
      String os = System.getProperty("os.name");
      String paths =
          files.stream().map(f -> "\"" + toOSPath(os, f) + "/\"").collect(Collectors.joining(","));

      builder.addParam("reactive", builder.isReactive());
      builder.addParam("modulePaths", paths);
      builder.addParam(
          "forbiddenDomainSuffixes",
          loadForbiddenValuesForAsString(
              FORBIDDEN_DOMAIN_SUFFIXES_PROP, FORBIDDEN_DOMAIN_SUFFIXES));
      builder.addParam(
          "forbiddenDomainClassNames",
          loadForbiddenValuesForAsString(
              FORBIDDEN_DOMAIN_CLASS_NAMES_PROP, FORBIDDEN_DOMAIN_NAMES));

      files.stream()
          .filter(file -> file.getName().equals(Constants.APP_SERVICE))
          .findFirst()
          .ifPresent(appService -> generateArchUnitFiles(logger, appService.getName(), builder));
    }
  }

  public static void validateModelName(String name) {
    loadForbiddenValuesFor(FORBIDDEN_DOMAIN_SUFFIXES_PROP, FORBIDDEN_DOMAIN_SUFFIXES)
        .filter(name::endsWith)
        .findAny()
        .ifPresent(
            forbidden -> {
              throw new CleanDomainException(
                  "Model suffix '"
                      + forbidden
                      + "' is forbidden, please don't use "
                      + "tech names in domain model name at "
                      + name);
            });
    validateUseCaseName(name);
  }

  /**
   * Removes the forbidden domain suffixes (dto, request, response by default) from the end of a
   * class name, repeatedly, so BookRequestDto becomes Book. A name made only of a forbidden suffix
   * is returned unchanged, so {@link #validateModelName(String)} still rejects it.
   */
  public static String stripForbiddenSuffixes(String name) {
    List<String> suffixes =
        loadForbiddenValuesFor(FORBIDDEN_DOMAIN_SUFFIXES_PROP, FORBIDDEN_DOMAIN_SUFFIXES).toList();
    String current = name;
    boolean stripped = true;
    while (stripped) {
      stripped = false;
      for (String suffix : suffixes) {
        if (current.length() > suffix.length() && current.endsWith(suffix)) {
          current = current.substring(0, current.length() - suffix.length());
          stripped = true;
          break;
        }
      }
    }
    return current;
  }

  public static void validateUseCaseName(String name) {
    loadForbiddenValuesFor(FORBIDDEN_DOMAIN_CLASS_NAMES_PROP, FORBIDDEN_DOMAIN_NAMES)
        .filter(name::contains)
        .findAny()
        .ifPresent(
            forbidden -> {
              throw new CleanDomainException(
                  "Model or UseCase word '"
                      + forbidden
                      + "' "
                      + "is forbidden, please don't use tech names in domain at "
                      + name);
            });
  }

  private static String loadForbiddenValuesForAsString(String property, String defaults) {
    return loadForbiddenValuesFor(property, defaults)
        .collect(Collectors.joining("\",\"", "\"", "\""));
  }

  private static Stream<String> loadForbiddenValuesFor(String property, String defaults) {
    String values = defaults;
    try {
      String content = FileUtils.readProperties(property);
      if (StringUtils.isNoneEmpty(content)) {
        values = content;
      }
    } catch (Exception ignored) { // NOSONAR
    }
    return Stream.of(values.split(","))
        .flatMap(tool -> Stream.of(tool, tool.toUpperCase(), classCase(tool)));
  }

  private static String classCase(String tool) {
    return tool.substring(0, 1).toUpperCase() + tool.substring(1);
  }

  private static String toOSPath(String os, File projectDir) {
    if (os != null && os.contains("Windows")) {
      return projectDir.toString().replace("\\", "\\\\");
    }
    return projectDir.toString();
  }

  @SneakyThrows
  private static void generateArchUnitFiles(
      Logger logger, String appService, ModuleBuilder builder) {
    logger.lifecycle("Injecting ArchitectureTest in module {}", appService);
    builder.setupFromTemplate("structure/applications/appservice/arch-validations");
    builder.persist();
  }

  public enum Type {
    CLASS_NAME,
    CLASS_SUFFIX
  }
}
