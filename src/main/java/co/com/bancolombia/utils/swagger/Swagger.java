package co.com.bancolombia.utils.swagger;

import co.com.bancolombia.factory.ModuleBuilder;
import io.swagger.codegen.v3.ClientOptInput;
import io.swagger.codegen.v3.DefaultGenerator;
import io.swagger.codegen.v3.config.CodegenConfigurator;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.gradle.api.logging.Logger;

@UtilityClass
public class Swagger {

  public static void fromBuilder(ModuleBuilder builder, String outputDir, boolean entryPoint) {
    if (builder.getStringParam("swagger-file") != null) {
      generate(
          SwaggerOptions.builder()
              .reactive(builder.isReactive())
              .lombok(builder.isEnableLombok())
              .entryPoint(entryPoint)
              .router(builder.getBooleanParam("task-param-router"))
              .swagger(builder.getStringParam("swagger-file"))
              .logger(builder.getProject().getLogger())
              .outputDir(builder.getProject().getRootDir() + "/" + outputDir)
              .packageName(
                  entryPoint
                      ? builder.getStringParam("package")
                      : builder.getStringParam("package") + ".consumer")
              .build());
    }
  }

  public static void generate(SwaggerOptions options) {
    CodegenConfigurator configurator = new CodegenConfigurator();

    configurator.addAdditionalProperty("async", options.isReactive());
    configurator.addAdditionalProperty("lombok", options.isLombok());
    configurator.addAdditionalProperty("invokerPackage", options.getPackageName() + ".api");
    configurator.addAdditionalProperty("fullController", true);
    configurator.addAdditionalProperty("jakarta", true);
    configurator.addAdditionalProperty("useBeanValidation", false);

    configurator.setInputSpecURL(options.getSwagger());
    configurator.setOutputDir(options.getOutputDir());
    configurator.setApiPackage(options.getPackageName() + ".api");
    configurator.setModelPackage(options.getPackageName() + ".api.model");

    configurator.setLang(resolveLang(options));

    final ClientOptInput clientOptInput = configurator.toClientOptInput();
    List<File> files = new DefaultGenerator().opts(clientOptInput).generate();
    if (options.getLogger() != null) {
      files.forEach(file -> options.getLogger().lifecycle("File generated from swagger: {}", file));
    }
    try {
      Files.deleteIfExists(Path.of(options.getOutputDir(), ".swagger-codegen-ignore"));
      Files.deleteIfExists(Path.of(options.getOutputDir(), ".swagger-codegen", "VERSION"));
      Files.deleteIfExists(Path.of(options.getOutputDir(), ".swagger-codegen"));
    } catch (IOException e) {
      options.getLogger().info("file not found", e);
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
