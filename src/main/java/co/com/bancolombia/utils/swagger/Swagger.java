package co.com.bancolombia.utils.swagger;

import co.com.bancolombia.factory.ModuleBuilder;
import io.swagger.codegen.v3.ClientOptInput;
import io.swagger.codegen.v3.DefaultGenerator;
import io.swagger.codegen.v3.config.CodegenConfigurator;
import java.io.File;
import java.util.List;
import java.util.Map;
import org.gradle.api.logging.Logger;

public class Swagger {

  public static void main(String[] args) { // TODO: Remove this method
    Map<String, Object> params = Map.of("async", true, "lombok", true, "router", true);
    generateEntrypoint(
        "co.com.bancolombia",
        "swagger3.yaml",
        "build/clean/infrastructure/entry-points/reactive-web",
        params);
  }

  public static void fromBuilder(ModuleBuilder builder, String outputDir) {
    String swaggerFile = builder.getStringParam("swagger-file");
    if (swaggerFile != null) {
      String packageName = builder.getStringParam("package");
      Map<String, Object> params =
          Map.of(
              "async",
              builder.isReactive(),
              "lombok",
              builder.isEnableLombok(),
              "router",
              builder.getBooleanParam("task-param-router"));
      Logger logger = builder.getProject().getLogger();
      generateEntrypoint(
              packageName, swaggerFile, builder.getProject().getRootDir() + "/" + outputDir, params)
          .forEach(file -> logger.lifecycle("File generated from swagger: {}", file));
    }
  }

  public static List<File> generateEntrypoint(
      String packageName, String swagger, String outputDir, Map<String, Object> additionalProps) {
    CodegenConfigurator configurator = new CodegenConfigurator();

    additionalProps.forEach(configurator::addAdditionalProperty);

    configurator.addAdditionalProperty("invokerPackage", packageName + ".api");
    configurator.addAdditionalProperty("fullController", true);
    configurator.addAdditionalProperty("jakarta", true);
    configurator.addAdditionalProperty("useBeanValidation", false); // TODO: make it to works
    configurator.setInputSpecURL(swagger);
    configurator.setOutputDir(outputDir);
    configurator.setApiPackage(packageName + ".api");
    configurator.setModelPackage(packageName + ".api.model");
    configurator.setLang("io.swagger.codegen.v3.generators.WebFluxCodegen");
    final ClientOptInput clientOptInput = configurator.toClientOptInput();
    return new DefaultGenerator().opts(clientOptInput).generate();
  }
}
