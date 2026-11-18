package co.com.bancolombia.utils.swagger;

import co.com.bancolombia.exceptions.CleanDomainException;
import co.com.bancolombia.factory.ModuleBuilder;
import io.swagger.codegen.v3.DefaultGenerator;
import io.swagger.codegen.v3.config.CodegenConfigurator;
import io.swagger.codegen.v3.generators.DomainModelCodegen;
import io.swagger.codegen.v3.generators.OpenApiDomainModelPlanValidator;
import io.swagger.codegen.v3.generators.OpenApiModelPackageResolver;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.gradle.api.logging.Logger;

@UtilityClass
public class Swagger {

  private static final String DOMAIN_MODEL_MODULE = "domain/model";
  private static final String JAVA_SOURCE_FOLDER = "src/main/java";
  private static final String PACKAGE = "package";
  private static final String DOMAIN_MODEL_LANG =
      "io.swagger.codegen.v3.generators.DomainModelCodegen";
  private static final String MODELS_SYSTEM_PROPERTY = "models";

  public static void fromBuilder(ModuleBuilder builder, String outputDir, boolean entryPoint) {
    if (builder.getStringParam("swagger-file") != null) {
      generate(
          SwaggerOptions.builder()
              .reactive(builder.isReactive())
              .lombok(builder.isEnableLombok())
              .entryPoint(entryPoint)
              .router(builder.getBooleanParam("task-param-router"))
              .swagger(builder.getStringParam("swagger-file"))
              .logger(builder.getLogger())
              .outputDir(builder.getProjectDir() + "/" + outputDir)
              .packageName(
                  entryPoint
                      ? builder.getStringParam(PACKAGE)
                      : builder.getStringParam(PACKAGE) + ".consumer")
              .build());
    }
  }

  public static void generate(SwaggerOptions options) {
    CodegenConfigurator configurator =
        baseConfigurator(options.getSwagger(), options.getOutputDir(), options.isLombok());
    configurator.addAdditionalProperty("async", options.isReactive());
    configurator.addAdditionalProperty("invokerPackage", options.getPackageName() + ".api");
    configurator.addAdditionalProperty("fullController", true);
    configurator.setApiPackage(options.getPackageName() + ".api");
    configurator.setModelPackage(options.getPackageName() + ".api.model");
    configurator.setLang(resolveLang(options));

    List<File> files = run(configurator, null, options.getOutputDir(), options.getLogger());
    if (options.getLogger() != null) {
      files.forEach(file -> options.getLogger().lifecycle("File generated from swagger: {}", file));
    }
  }

  public static List<String> generateDomainModels(
      ModuleBuilder builder, String specPath, String modelsFilter) {
    return generateDomainModels(builder, specPath, modelsFilter, null, null);
  }

  public static List<String> generateDomainModels(
      ModuleBuilder builder,
      String specPath,
      String modelsFilter,
      String requestSchemaKeys,
      String responseSchemaKeys) {
    String modelPackage = domainModelPackage(builder);
    String outputDir = domainModelOutputDir(builder);
    Logger logger = builder.getLogger();
    String resolvedSpec = resolveSpec(builder, specPath);
    Set<String> requestKeys = OpenApiModelPackageResolver.parseSchemaKeys(requestSchemaKeys);
    Set<String> responseKeys = OpenApiModelPackageResolver.parseSchemaKeys(responseSchemaKeys);

    SwaggerParseResult parsed = new OpenAPIV3Parser().readLocation(resolvedSpec, null, null);
    if (parsed.getOpenAPI() == null) {
      throw new CleanDomainException("Invalid OpenAPI specification: " + parsed.getMessages());
    }
    List<DomainModelCodegen.ModelTarget> planned =
        planDomainModels(parsed, modelsFilter, modelPackage, requestKeys, responseKeys);
    OpenApiDomainModelPlanValidator.validate(
        planned, parsed.getOpenAPI(), requestKeys, responseKeys);

    CodegenConfigurator configurator =
        baseConfigurator(resolvedSpec, outputDir, builder.isEnableLombok());
    configurator.setLang(DOMAIN_MODEL_LANG);
    configurator.setModelPackage(modelPackage);
    configurator.setSkipOverwrite(true);
    configurator.addAdditionalProperty(
        OpenApiModelPackageResolver.BASE_MODEL_PACKAGE, modelPackage);
    configurator.addAdditionalProperty(
        OpenApiModelPackageResolver.REQUEST_SCHEMA_KEYS, joinKeys(requestKeys));
    configurator.addAdditionalProperty(
        OpenApiModelPackageResolver.RESPONSE_SCHEMA_KEYS, joinKeys(responseKeys));
    configurator.addAdditionalProperty("hideGenerationTimestamp", true);

    List<File> generated =
        run(configurator, modelsFilter, outputDir, logger).stream()
            .filter(file -> file.getName().endsWith(".java"))
            .toList();

    List<String> classNames = new ArrayList<>();
    for (File file : generated) {
      logger.lifecycle("OpenAPI domain model generated: {}", file.getAbsolutePath());
      classNames.add(file.getName().replace(".java", ""));
    }
    return classNames;
  }

  private static CodegenConfigurator baseConfigurator(
      String inputSpec, String outputDir, boolean lombok) {
    CodegenConfigurator configurator = new CodegenConfigurator();
    configurator.setInputSpecURL(inputSpec);
    configurator.setOutputDir(outputDir);
    configurator.addAdditionalProperty("lombok", lombok);
    configurator.addAdditionalProperty("jakarta", true);
    configurator.addAdditionalProperty("useBeanValidation", false);
    return configurator;
  }

  private static List<File> run(
      CodegenConfigurator configurator, String modelsFilter, String outputDir, Logger logger) {
    String previousModels = System.getProperty(MODELS_SYSTEM_PROPERTY);
    try {
      if (modelsFilter != null && !modelsFilter.isBlank()) {
        configurator.addSystemProperty(MODELS_SYSTEM_PROPERTY, modelsFilter.replace(" ", ""));
      }
      return new DefaultGenerator().opts(configurator.toClientOptInput()).generate();
    } finally {
      restoreSystemProperty(previousModels);
      cleanupCodegenMetadata(outputDir, logger);
    }
  }

  private static String resolveSpec(ModuleBuilder builder, String specPath) {
    File specFile = builder.resolveFile(specPath);
    if (specFile.isFile()) {
      return specFile.toURI().toString();
    }
    if (specPath.startsWith("http://") || specPath.startsWith("https://")) {
      return specPath;
    }
    throw new CleanDomainException(
        "OpenAPI spec not found: "
            + specPath
            + ". Use a path relative to the project root or an http(s) URL.");
  }

  private static void restoreSystemProperty(String previousValue) {
    if (previousValue == null) {
      System.clearProperty(MODELS_SYSTEM_PROPERTY);
    } else {
      System.setProperty(MODELS_SYSTEM_PROPERTY, previousValue);
    }
  }

  private static void cleanupCodegenMetadata(String outputDir, Logger logger) {
    try {
      Files.deleteIfExists(Path.of(outputDir, ".swagger-codegen-ignore"));
      Files.deleteIfExists(Path.of(outputDir, ".swagger-codegen", "VERSION"));
      Files.deleteIfExists(Path.of(outputDir, ".swagger-codegen"));
    } catch (IOException e) {
      if (logger != null) {
        logger.info("Swagger codegen metadata cleanup skipped", e);
      }
    }
  }

  private String resolveLang(SwaggerOptions options) {
    if (options.isEntryPoint() && options.isReactive() && options.isRouter()) {
      return "io.swagger.codegen.v3.generators.WebFluxRouterCodegen";
    }
    if (options.isEntryPoint()) {
      return "io.swagger.codegen.v3.generators.RestControllerCodegen";
    }
    if (options.isReactive()) {
      return "io.swagger.codegen.v3.generators.WebClientCodegen";
    }
    return "io.swagger.codegen.v3.generators.RestConsumerCodegen";
  }

  public static List<DomainModelCodegen.ModelTarget> planDomainModels(
      ModuleBuilder builder, String specPath, String modelsFilter) {
    return planDomainModels(builder, specPath, modelsFilter, Set.of(), Set.of());
  }

  public static List<DomainModelCodegen.ModelTarget> planDomainModels(
      ModuleBuilder builder,
      String specPath,
      String modelsFilter,
      Set<String> requestSchemaKeys,
      Set<String> responseSchemaKeys) {
    SwaggerParseResult parsed =
        new OpenAPIV3Parser().readLocation(resolveSpec(builder, specPath), null, null);
    if (parsed.getOpenAPI() == null) {
      throw new CleanDomainException("Invalid OpenAPI specification: " + parsed.getMessages());
    }
    return planDomainModels(
        parsed, modelsFilter, domainModelPackage(builder), requestSchemaKeys, responseSchemaKeys);
  }

  public static Path domainModelSourceRoot(ModuleBuilder builder) {
    return Path.of(domainModelOutputDir(builder), JAVA_SOURCE_FOLDER);
  }

  private static String domainModelPackage(ModuleBuilder builder) {
    return builder.getStringParam(PACKAGE) + ".model.entities";
  }

  private static String domainModelOutputDir(ModuleBuilder builder) {
    return builder.getProjectDir().getPath() + "/" + DOMAIN_MODEL_MODULE;
  }

  private static List<DomainModelCodegen.ModelTarget> planDomainModels(
      SwaggerParseResult parsed,
      String modelsFilter,
      String basePackage,
      Set<String> requestSchemaKeys,
      Set<String> responseSchemaKeys) {
    Set<String> selected =
        modelsFilter == null || modelsFilter.isBlank()
            ? Set.of()
            : Arrays.stream(modelsFilter.split(",")).map(String::trim).collect(Collectors.toSet());
    return DomainModelCodegen.resolveTargets(
        parsed.getOpenAPI(), selected, basePackage, requestSchemaKeys, responseSchemaKeys);
  }

  private static String joinKeys(Set<String> keys) {
    return keys.stream().sorted().collect(Collectors.joining(","));
  }

  @Builder
  @Getter
  public static class SwaggerOptions {
    private final boolean entryPoint;
    private final boolean reactive;
    private final boolean lombok;
    private final boolean router;
    private final String swagger;
    private final String packageName;
    private final String outputDir;
    private final Logger logger;
  }
}
