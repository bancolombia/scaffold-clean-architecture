package co.com.bancolombia.utils.swagger;

import co.com.bancolombia.exceptions.CleanDomainException;
import co.com.bancolombia.factory.ModuleBuilder;
import io.swagger.codegen.v3.DefaultGenerator;
import io.swagger.codegen.v3.config.CodegenConfigurator;
import io.swagger.codegen.v3.config.PreparsedCodegenConfigurator;
import io.swagger.codegen.v3.generators.DomainModelCodegen;
import io.swagger.codegen.v3.generators.OpenApiDomainModelPlanValidator;
import io.swagger.codegen.v3.generators.OpenApiModelPackageResolver;
import io.swagger.codegen.v3.generators.OpenApiModelSelectionResolver;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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

  public static OpenApiSpecContext loadOpenApiSpec(ModuleBuilder builder, String specPath) {
    String resolved = resolveSpec(builder, specPath);
    String content = readSpecContent(builder, specPath, resolved);
    SwaggerParseResult parsed =
        new OpenAPIParser().readContents(content, null, domainParseOptions());
    if (parsed.getOpenAPI() == null) {
      throw new CleanDomainException("Invalid OpenAPI specification: " + parsed.getMessages());
    }
    return new OpenApiSpecContext(parsed, content);
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
    return generateDomainModels(
        builder,
        loadOpenApiSpec(builder, specPath),
        modelsFilter,
        requestSchemaKeys,
        responseSchemaKeys);
  }

  public static List<String> generateDomainModels(
      ModuleBuilder builder,
      OpenApiSpecContext spec,
      String modelsFilter,
      String requestSchemaKeys,
      String responseSchemaKeys) {
    String modelPackage = domainModelPackage(builder);
    String outputDir = domainModelOutputDir(builder);
    Logger logger = builder.getLogger();
    Set<String> requestKeys = OpenApiModelPackageResolver.parseSchemaKeys(requestSchemaKeys);
    Set<String> responseKeys = OpenApiModelPackageResolver.parseSchemaKeys(responseSchemaKeys);
    OpenAPI openAPI = spec.openAPI();
    Set<String> selectedKeys = OpenApiModelSelectionResolver.parseModelsFilter(modelsFilter);
    Set<String> generationKeys = resolveGenerationSchemaKeys(openAPI, selectedKeys);
    List<DomainModelCodegen.ModelTarget> planned =
        DomainModelCodegen.resolveTargets(
            openAPI, generationKeys, modelPackage, requestKeys, responseKeys);
    OpenApiDomainModelPlanValidator.validate(
        planned, openAPI, selectedKeys, requestKeys, responseKeys);
    PreparsedCodegenConfigurator configurator =
        domainConfigurator(
            outputDir, modelPackage, builder.isEnableLombok(), requestKeys, responseKeys);
    String codegenModelsFilter = joinKeys(generationKeys);
    List<File> generated =
        run(configurator, openAPI, codegenModelsFilter, outputDir, logger).stream()
            .filter(file -> file.getName().endsWith(".java"))
            .toList();
    List<String> classNames = new ArrayList<>();
    for (File file : generated) {
      logger.lifecycle("OpenAPI domain model generated: {}", file.getAbsolutePath());
      classNames.add(file.getName().replace(".java", ""));
    }
    return classNames;
  }

  private static PreparsedCodegenConfigurator domainConfigurator(
      String outputDir,
      String modelPackage,
      boolean lombok,
      Set<String> requestKeys,
      Set<String> responseKeys) {
    PreparsedCodegenConfigurator configurator = new PreparsedCodegenConfigurator();
    configurator.setOutputDir(outputDir);
    configurator.addAdditionalProperty("lombok", lombok);
    configurator.addAdditionalProperty("jakarta", true);
    configurator.addAdditionalProperty("useBeanValidation", false);
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
    return configurator;
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
      applyModelsFilter(configurator, modelsFilter);
      return new DefaultGenerator().opts(configurator.toClientOptInput()).generate();
    } finally {
      restoreSystemProperty(previousModels);
      cleanupCodegenMetadata(outputDir, logger);
    }
  }

  private static List<File> run(
      PreparsedCodegenConfigurator configurator,
      OpenAPI openAPI,
      String modelsFilter,
      String outputDir,
      Logger logger) {
    String previousModels = System.getProperty(MODELS_SYSTEM_PROPERTY);
    try {
      applyModelsFilter(configurator, modelsFilter);
      return new DefaultGenerator().opts(configurator.toClientOptInput(openAPI)).generate();
    } finally {
      restoreSystemProperty(previousModels);
      cleanupCodegenMetadata(outputDir, logger);
    }
  }

  private static void applyModelsFilter(CodegenConfigurator configurator, String modelsFilter) {
    if (modelsFilter != null && !modelsFilter.isBlank()) {
      configurator.addSystemProperty(MODELS_SYSTEM_PROPERTY, modelsFilter.replace(" ", ""));
    }
  }

  private static String readSpecContent(ModuleBuilder builder, String specPath, String resolved) {
    if (resolved.startsWith("http://") || resolved.startsWith("https://")) {
      try {
        return new CodegenConfigurator().loadSpecContent(resolved, List.of());
      } catch (Exception e) {
        throw new CleanDomainException(
            "Unable to read OpenAPI URL: " + resolved + ": " + e.getMessage());
      }
    }
    File specFile = builder.resolveFile(specPath);
    if (specFile.isFile()) {
      try {
        return Files.readString(specFile.toPath());
      } catch (IOException e) {
        throw new CleanDomainException(
            "Unable to read OpenAPI file: " + specFile.getPath() + ": " + e.getMessage());
      }
    }
    throw new CleanDomainException(
        "OpenAPI spec not found: "
            + specPath
            + ". Use a path relative to the project root or an http(s) URL.");
  }

  private static ParseOptions domainParseOptions() {
    ParseOptions options = new ParseOptions();
    options.setResolve(true);
    options.setResolveFully(false);
    options.setFlatten(true);
    options.setResolveResponses(true);
    return options;
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
    return planDomainModels(
        builder,
        loadOpenApiSpec(builder, specPath),
        modelsFilter,
        requestSchemaKeys,
        responseSchemaKeys);
  }

  public static List<DomainModelCodegen.ModelTarget> planDomainModels(
      ModuleBuilder builder,
      OpenApiSpecContext spec,
      String modelsFilter,
      Set<String> requestSchemaKeys,
      Set<String> responseSchemaKeys) {
    return planDomainModels(
        spec, modelsFilter, domainModelPackage(builder), requestSchemaKeys, responseSchemaKeys);
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
      OpenApiSpecContext spec,
      String modelsFilter,
      String basePackage,
      Set<String> requestSchemaKeys,
      Set<String> responseSchemaKeys) {
    return planDomainModels(
        spec.openAPI(), modelsFilter, basePackage, requestSchemaKeys, responseSchemaKeys);
  }

  private static List<DomainModelCodegen.ModelTarget> planDomainModels(
      OpenAPI openAPI,
      String modelsFilter,
      String basePackage,
      Set<String> requestSchemaKeys,
      Set<String> responseSchemaKeys) {
    Set<String> selectedKeys = OpenApiModelSelectionResolver.parseModelsFilter(modelsFilter);
    Set<String> generationKeys = resolveGenerationSchemaKeys(openAPI, selectedKeys);
    return DomainModelCodegen.resolveTargets(
        openAPI, generationKeys, basePackage, requestSchemaKeys, responseSchemaKeys);
  }

  private static Set<String> resolveGenerationSchemaKeys(
      OpenAPI openAPI, Set<String> selectedKeys) {
    OpenApiModelSelectionResolver.validateSelectedSchemaKeys(openAPI, selectedKeys, "models");
    if (selectedKeys.isEmpty()) {
      return Set.of();
    }
    return OpenApiModelSelectionResolver.expandSelectedSchemas(openAPI, selectedKeys);
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
