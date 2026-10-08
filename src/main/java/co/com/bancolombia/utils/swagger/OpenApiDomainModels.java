package co.com.bancolombia.utils.swagger;

import co.com.bancolombia.exceptions.CleanDomainException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.validations.architecture.ArchitectureValidation;
import io.swagger.codegen.v3.ClientOptInput;
import io.swagger.codegen.v3.DefaultGenerator;
import io.swagger.codegen.v3.config.CodegenConfigurator;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import io.swagger.codegen.v3.generators.OpenApiModelPackageResolver;
import lombok.experimental.UtilityClass;
import org.gradle.api.logging.Logger;

@UtilityClass
public class OpenApiDomainModels {

    private static final String DOMAIN_MODEL_MODULE = "domain/model";
    private static final String CODEGEN_LANG = "io.swagger.codegen.v3.generators.DomainModelCodegen";

    public static List<String> generate(ModuleBuilder builder, String specPath, String modelsFilter)
            throws IOException {
        File specFile = builder.resolveFile(specPath);
        String inputSpec =
                specFile.isFile() ? specFile.toURI().toString() : normalizeRemoteSpec(specPath);

        String basePackage = builder.getStringParam("package");
        String modelPackage = basePackage + ".model.entities";
        String outputDir = builder.getProjectDir().getPath() + "/" + DOMAIN_MODEL_MODULE;

        CodegenConfigurator configurator = new CodegenConfigurator();
        configurator.setInputSpecURL(inputSpec);
        configurator.setOutputDir(outputDir);
        configurator.setLang(CODEGEN_LANG);
        configurator.setModelPackage(modelPackage);
        configurator.addAdditionalProperty(
                OpenApiModelPackageResolver.BASE_MODEL_PACKAGE, modelPackage);

        configurator.addAdditionalProperty("lombok", builder.isEnableLombok());
        configurator.addAdditionalProperty("jakarta", true);
        configurator.addAdditionalProperty("useBeanValidation", false);
        configurator.addAdditionalProperty("hideGenerationTimestamp", true);

        if (modelsFilter != null && !modelsFilter.isBlank()) {
            configurator.addSystemProperty("models", modelsFilter.replace(" ", ""));
        }

        Logger logger = builder.getLogger();
        ClientOptInput clientOptInput = configurator.toClientOptInput();
        List<File> generated =
                new DefaultGenerator().opts(clientOptInput).generate().stream()
                        .filter(file -> file.getName().endsWith(".java"))
                        .toList();

        generated.forEach(
                file -> logger.lifecycle("OpenAPI domain model generated: {}", file.getAbsolutePath()));
        cleanupCodegenMetadata(Path.of(outputDir), logger);

        List<String> classNames = new ArrayList<>();
        for (File file : generated) {
            String className = file.getName().replace(".java", "");
            ArchitectureValidation.validateModelName(className);
            classNames.add(className);
        }
        return classNames;
    }


    private static String normalizeRemoteSpec(String specPath) {
        if (specPath.startsWith("http://") || specPath.startsWith("https://")) {
            return specPath;
        }
        throw new CleanDomainException(
                "OpenAPI spec not found: "
                        + specPath
                        + ". Use a path relative to the project root or an http(s) URL.");
    }

    private static void cleanupCodegenMetadata(Path outputDir, Logger logger) {
        try {
            Files.deleteIfExists(outputDir.resolve(".swagger-codegen-ignore"));
            Files.deleteIfExists(outputDir.resolve(".swagger-codegen/VERSION"));
            Files.deleteIfExists(outputDir.resolve(".swagger-codegen"));
        } catch (IOException e) {
            logger.info("Swagger codegen metadata cleanup skipped", e);
        }
    }
}