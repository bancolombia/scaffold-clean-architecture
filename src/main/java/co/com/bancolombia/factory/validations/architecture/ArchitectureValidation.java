package co.com.bancolombia.factory.validations.architecture;

import co.com.bancolombia.Constants;
import co.com.bancolombia.exceptions.CleanDomainException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.utils.FileUtils;
import java.io.File;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.gradle.api.Project;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ArchitectureValidation {
  private static final String SKIP_PROP = "arch.unit.skip";
  private static final String FORBIDDEN_DOMAIN_SUFFIXES_PROP = "arch.unit.forbiddenDomainSuffixes";
  private static final String FORBIDDEN_DOMAIN_SUFFIXES = "dto,request,response";
  private static final String FORBIDDEN_DOMAIN_CLASS_NAMES_PROP =
      "arch.unit.forbiddenDomainClassNames";
  private static final String FORBIDDEN_DOMAIN_NAMES =
      "rabbit,sqs,sns,ibm,dynamo,aws,mysql,postgres,redis,mongo,rsocket,r2dbc,http,kms,s3,graphql,kafka";

  public static void inject(Project project, ModuleBuilder builder) {
    if (!FileUtils.readBooleanProperty(SKIP_PROP)) {
      String os = System.getProperty("os.name");
      String paths =
          project.getAllprojects().stream()
              .map(p -> "\"" + toOSPath(os, p.getProjectDir()) + "/\"")
              .collect(Collectors.joining(","));
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
      project.getAllprojects().stream()
          .filter(p -> p.getName().equals(Constants.APP_SERVICE))
          .findFirst()
          .ifPresent(appService -> generateArchUnitFiles(project, appService, builder));
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
      Project project, Project appService, ModuleBuilder builder) {
    project
        .getLogger()
        .lifecycle("Injecting ArchitectureTest in module {}", appService.getProjectDir().getName());
    builder.setupFromTemplate("structure/applications/appservice/arch-validations");
    builder.appendDependencyToModule(
        Constants.APP_SERVICE,
        "testImplementation 'com.tngtech.archunit:archunit:" + Constants.ARCH_UNIT_VERSION + "'");
    builder.appendDependencyToModule(
        Constants.APP_SERVICE, "testImplementation 'com.fasterxml.jackson.core:jackson-databind'");
    builder.persist();
  }

  public enum Type {
    CLASS_NAME,
    CLASS_SUFFIX
  }
}
