package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.ParamNotFoundException;
import co.com.bancolombia.task.annotations.CATask;
import co.com.bancolombia.utils.Utils;
import co.com.bancolombia.utils.swagger.OpenApiDomainModels;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.gradle.api.tasks.options.Option;

@CATask(
        name = "generateModelsFromOpenApi",
        shortcut = "gmfoa",
        description = "Generate models in domain layer from an OpenAPI v3 contract")
public class GenerateModelsFromOpenApiTask extends AbstractCleanArchitectureDefaultTask {

    private String fromOpenApi;
    private String models;

    @Option(
            option = "from-openapi",
            description = "Path or URL to the OpenAPI v3 specification (YAML or JSON)")
    public void setFromOpenApi(String fromOpenApi) {
        this.fromOpenApi = fromOpenApi;
    }

    @Option(
            option = "models",
            description = "(Optional) Schema names you want to generate (default: all schemas)")
    public void setModels(String models) {
        this.models = models;
    }

    @Override
    protected void doExecute() throws IOException, ParamNotFoundException {
        if (fromOpenApi == null || fromOpenApi.isBlank()) {
            printHelp();
            throw new IllegalArgumentException(
                    "No OpenAPI spec, usage: gradle generateModelsFromOpenApi --from-openapi [path-or-url]");
        }

        File modelModule = builder.resolveFile("domain/model");
        if (!modelModule.isDirectory()) {
            throw new IllegalStateException(
                    "domain/model module not found. Run cleanArchitecture (ca) before importing OpenAPI models.");
        }

        logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
        logger.lifecycle("OpenAPI spec: {}", fromOpenApi);
        if (models != null && !models.isBlank()) {
            logger.lifecycle("Schemas filter: {}", models);
        }

        List<String> classNames =
                OpenApiDomainModels.generate(builder, fromOpenApi, models);

        logger.lifecycle("Generated {} domain model(s) from OpenAPI", classNames.size());
    }

    @Override
    protected Optional<String> resolveAnalyticsType() {
        return Optional.of("openapi-domain-models");
    }
}