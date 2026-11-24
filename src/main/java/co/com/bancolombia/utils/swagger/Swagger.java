package co.com.bancolombia.utils.swagger;

import co.com.bancolombia.exceptions.CleanDomainException;
import co.com.bancolombia.factory.ModuleBuilder;
import io.swagger.codegen.v3.DefaultGenerator;
import io.swagger.codegen.v3.config.CodegenConfigurator;
import io.swagger.codegen.v3.config.PreparsedCodegenConfigurator;
import io.swagger.codegen.v3.generators.DomainModelCodegen;
import io.swagger.codegen.v3.generators.OpenApiDomainModelNaming;
import io.swagger.codegen.v3.generators.OpenApiDomainModelPlanValidator;
import io.swagger.codegen.v3.generators.OpenApiModelPackageResolver;
import io.swagger.codegen.v3.generators.OpenApiModelSelectionResolver;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
  private static final Object CODEGEN_LOCK = new Object();

  public static void fromBuilder(ModuleBuilder builder, String outputDir, boolean entryPoint) {
    String swaggerFile = builder.getStringParam("swagger-file");
    if (swaggerFile != null) {
      generate(
          SwaggerOptions.builder()
              .reactive(builder.isReactive())
              .lombok(builder.isEnableLombok())
              .entryPoint(entryPoint)
              .router(builder.getBooleanParam("task-param-router"))
              .swagger(resolveSpec(builder, swaggerFile))
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

  /**
   * Parses the spec from its location (file path or URL), so relative {@code $ref}s to other files
   * resolve against the spec's own folder.
   */
  public static OpenAPI loadOpenApiSpec(ModuleBuilder builder, String specPath) {
    String location = resolveSpec(builder, specPath);
    SwaggerParseResult parsed;
    try {
      parsed = new OpenAPIParser().readLocation(location, null, domainParseOptions());
    } catch (Exception e) {
      throw new CleanDomainException(
          "Unable to read OpenAPI specification: " + location + ": " + e.getMessage());
    }
    if (parsed == null || parsed.getOpenAPI() == null) {
      throw new CleanDomainException(
          "Invalid OpenAPI specification: " + (parsed == null ? location : parsed.getMessages()));
    }
    List<String> messages = parsed.getMessages() == null ? List.of() : parsed.getMessages();
    List<String> unresolvedRefs = OpenApiModelSelectionResolver.unresolvedRefs(parsed.getOpenAPI());
    if (!unresolvedRefs.isEmpty()) {
      throw new CleanDomainException(
          "Unresolved references in OpenAPI spec: "
              + unresolvedRefs
              + (messages.isEmpty() ? "" : ". Parser messages: " + messages));
    }
    messages.forEach(message -> builder.getLogger().warn("OpenAPI spec: {}", message));
    return parsed.getOpenAPI();
  }

  /**
   * Resolves and validates everything that will be generated, without writing any file.
   *
   * @param renameModels {@code schemaKey:ClassName} pairs that override the domain class names
   */
  public static DomainModelPlan planDomainModels(
      ModuleBuilder builder,
      OpenAPI openAPI,
      String modelsFilter,
      String requestModels,
      String responseModels,
      String renameModels) {
    Set<String> requestKeys = OpenApiModelPackageResolver.parseSchemaKeys(requestModels);
    Set<String> responseKeys = OpenApiModelPackageResolver.parseSchemaKeys(responseModels);
    Map<String, String> renames = OpenApiDomainModelNaming.parseRenames(renameModels);
    Set<String> selectedKeys = OpenApiModelSelectionResolver.parseModelsFilter(modelsFilter);
    Set<String> generationKeys = resolveGenerationSchemaKeys(openAPI, selectedKeys);
    Map<String, String> classNames = DomainModelCodegen.resolveClassNames(openAPI, renames);
    List<DomainModelCodegen.ModelTarget> targets =
        DomainModelCodegen.resolveTargets(
            openAPI,
            generationKeys,
            domainModelPackage(builder),
            requestKeys,
            responseKeys,
            classNames);
    OpenApiDomainModelPlanValidator.validate(
        targets,
        openAPI,
        selectedKeys,
        requestKeys,
        responseKeys,
        renames,
        builder.isEnableLombok());
    return new DomainModelPlan(
        openAPI, targets, generationKeys, requestKeys, responseKeys, classNames);
  }

  public static List<String> generateDomainModels(ModuleBuilder builder, DomainModelPlan plan) {
    String outputDir = domainModelOutputDir(builder);
    Logger logger = builder.getLogger();
    PreparsedCodegenConfigurator configurator =
        domainConfigurator(outputDir, domainModelPackage(builder), builder.isEnableLombok(), plan);
    List<File> generated =
        run(configurator, plan.openAPI(), joinKeys(plan.generationKeys()), outputDir, logger)
            .stream()
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
      String outputDir, String modelPackage, boolean lombok, DomainModelPlan plan) {
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
        OpenApiModelPackageResolver.REQUEST_SCHEMA_KEYS, joinKeys(plan.requestKeys()));
    configurator.addAdditionalProperty(
        OpenApiModelPackageResolver.RESPONSE_SCHEMA_KEYS, joinKeys(plan.responseKeys()));
    configurator.addAdditionalProperty(
        OpenApiDomainModelNaming.DOMAIN_CLASS_NAMES, plan.classNames());
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

  // Codegen reads the models filter from a JVM-wide system property, so generations that share a
  // Gradle daemon must not overlap between setting and restoring it.
  private static List<File> run(
      CodegenConfigurator configurator, String modelsFilter, String outputDir, Logger logger) {
    synchronized (CODEGEN_LOCK) {
      String previousModels = System.getProperty(MODELS_SYSTEM_PROPERTY);
      try {
        applyModelsFilter(configurator, modelsFilter);
        return new DefaultGenerator().opts(configurator.toClientOptInput()).generate();
      } finally {
        restoreSystemProperty(previousModels);
        cleanupCodegenMetadata(outputDir, logger);
      }
    }
  }

  private static List<File> run(
      PreparsedCodegenConfigurator configurator,
      OpenAPI openAPI,
      String modelsFilter,
      String outputDir,
      Logger logger) {
    synchronized (CODEGEN_LOCK) {
      String previousModels = System.getProperty(MODELS_SYSTEM_PROPERTY);
      try {
        applyModelsFilter(configurator, modelsFilter);
        return new DefaultGenerator().opts(configurator.toClientOptInput(openAPI)).generate();
      } finally {
        restoreSystemProperty(previousModels);
        cleanupCodegenMetadata(outputDir, logger);
      }
    }
  }

  private static void applyModelsFilter(CodegenConfigurator configurator, String modelsFilter) {
    if (modelsFilter != null && !modelsFilter.isBlank()) {
      configurator.addSystemProperty(MODELS_SYSTEM_PROPERTY, modelsFilter.replace(" ", ""));
    }
  }

  private static ParseOptions domainParseOptions() {
    ParseOptions options = new ParseOptions();
    options.setResolve(true);
    options.setResolveFully(false);
    options.setFlatten(true);
    options.setResolveResponses(true);
    return options;
  }

  /**
   * Returns the absolute path of a spec given as a file path (relative to the project root or
   * absolute) or a {@code file:} URI, or the URL itself when it is an http(s) URL.
   */
  private static String resolveSpec(ModuleBuilder builder, String specPath) {
    if (specPath.startsWith("http://") || specPath.startsWith("https://")) {
      return specPath;
    }
    File specFile =
        specPath.startsWith("file:") ? fromFileUri(specPath) : builder.resolveFile(specPath);
    if (specFile.isFile()) {
      return specFile.getAbsolutePath();
    }
    throw new CleanDomainException(
        "OpenAPI spec not found: "
            + specPath
            + ". Use a file path (relative to the project root or absolute), a file: URI or an"
            + " http(s) URL.");
  }

  private static File fromFileUri(String uri) {
    try {
      return Path.of(URI.create(uri)).toFile();
    } catch (IllegalArgumentException | FileSystemNotFoundException e) {
      throw new CleanDomainException(
          "Invalid file: URI for the OpenAPI spec: "
              + uri
              + " ("
              + e.getMessage()
              + "). Use an absolute URI such as file:///path/to/swagger.yaml and encode spaces as"
              + " %20, or pass the file path instead.");
    }
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

  public record DomainModelPlan(
      OpenAPI openAPI,
      List<DomainModelCodegen.ModelTarget> targets,
      Set<String> generationKeys,
      Set<String> requestKeys,
      Set<String> responseKeys,
      Map<String, String> classNames) {}

  public static Path domainModelSourceRoot(ModuleBuilder builder) {
    return Path.of(domainModelOutputDir(builder), JAVA_SOURCE_FOLDER);
  }

  private static String domainModelPackage(ModuleBuilder builder) {
    return builder.getStringParam(PACKAGE) + ".model.entities";
  }

  private static String domainModelOutputDir(ModuleBuilder builder) {
    return builder.getProjectDir().getPath() + "/" + DOMAIN_MODEL_MODULE;
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
