package co.com.bancolombia.utils.swagger;

import co.com.bancolombia.factory.ModuleBuilder;
import io.swagger.codegen.v3.ClientOptInput;
import io.swagger.codegen.v3.DefaultGenerator;
import io.swagger.codegen.v3.config.CodegenConfigurator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.experimental.UtilityClass;
import org.gradle.api.logging.Logger;

@UtilityClass
public class Swagger {

  public static void fromBuilder(ModuleBuilder builder, String outputDir, boolean entryPoint) {
    String swagger = builder.getStringParam("swagger-file");
    if (swagger != null) {
      Logger logger = builder.getProject().getLogger();
      String realOutputDir = builder.getProject().getRootDir() + "/" + outputDir;

      String packageName = builder.getStringParam("package");
      if (!entryPoint) {
        packageName = packageName + ".consumer";
      }

      // Generator setup
      CodegenConfigurator configurator = new CodegenConfigurator();

      configurator.addAdditionalProperty("async", builder.isReactive());
      configurator.addAdditionalProperty("lombok", builder.isEnableLombok());
      configurator.addAdditionalProperty("invokerPackage", packageName + ".api");
      configurator.addAdditionalProperty("fullController", true);
      configurator.addAdditionalProperty("jakarta", true);
      configurator.addAdditionalProperty("useBeanValidation", false);

      configurator.setInputSpecURL(swagger);
      configurator.setOutputDir(realOutputDir);
      configurator.setApiPackage(packageName + ".api");
      configurator.setModelPackage(packageName + ".api.model");

      configurator.setLang(resolveLang(builder, entryPoint));

      final ClientOptInput clientOptInput = configurator.toClientOptInput();
      new DefaultGenerator()
          .opts(clientOptInput)
          .generate()
          .forEach(file -> logger.lifecycle("File generated from swagger: {}", file));
      try {
        Files.deleteIfExists(Path.of(realOutputDir, ".swagger-codegen-ignore"));
        Files.deleteIfExists(Path.of(realOutputDir, ".swagger-codegen", "VERSION"));
        Files.deleteIfExists(Path.of(realOutputDir, ".swagger-codegen"));
      } catch (IOException e) {
        logger.info("file not found", e);
      }
    }
  }

  private String resolveLang(ModuleBuilder builder, boolean entryPoint) {
    if (entryPoint) {
      if (builder.isReactive() && builder.getBooleanParam("task-param-router")) {
        return "io.swagger.codegen.v3.generators.WebFluxRouterCodegen";
      } else {
        return "io.swagger.codegen.v3.generators.RestControllerCodegen";
      }
    } else if (builder.isReactive()) {
      return "io.swagger.codegen.v3.generators.WebClientCodegen";
    } else {
      return "io.swagger.codegen.v3.generators.RestConsumerCodegen";
    }
  }
}
