package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.ParamNotFoundException;
import co.com.bancolombia.factory.validations.architecture.ArchitectureValidation;
import co.com.bancolombia.task.annotations.CATask;
import co.com.bancolombia.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import co.com.bancolombia.utils.swagger.Swagger;
import org.gradle.api.tasks.options.Option;

@CATask(
        name = "generateModel",
        shortcut = "gm",
        description = "Generate model in domain layer")

public class GenerateModelTask extends AbstractCleanArchitectureDefaultTask {

  private String name = "";
  private String fromOpenApi;
  private String models;

  @Option(option = "name", description = "Set the model name")
  public void setName(String modelName) {
    this.name = modelName;
  }

  @Option(option = "from-openapi", description = "Path or URL to the OpenAPI v3 specification (YAML or JSON)")
  public void setFromOpenApi(String fromOpenApi) {
    this.fromOpenApi = fromOpenApi;
  }

  @Option(option = "models", description = "(Optional) Schema names you want to generate (default: all schemas)")
  public void setModels(String models) {
    this.models = models;
  }


  @Override
  protected void doExecute() throws IOException, ParamNotFoundException {

    //Debe llevar nombre de modelo o tener ruta de contrato
    boolean hasName = name != null && !name.isBlank();
    boolean hasSpec = fromOpenApi != null && !fromOpenApi.isBlank();

    //Si no hay carpeta domain/model, saca error
    File modelModule = builder.resolveFile("domain/model");
    if (!modelModule.isDirectory()) {
      throw new IllegalStateException(
              "domain/model module not found. Run cleanArchitecture (ca) before importing OpenAPI models.");
    }

    if (!hasName && !hasSpec) {
      printHelp();
      throw new IllegalArgumentException(
              "No model name and no OpenAPI spec, \r\n " +
                      "usage: gradle generateModel --from-openapi [path-or-url]" +
                      "\r\n or \r\n" +
                      "usage: gradle generateModel --name [name]");
    }
    logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
    if (hasSpec) {
      if (models != null && !models.isBlank()) {
        logger.lifecycle("Schemas filter: {}", models);
      }
      List<String> classNames = Swagger.generateDomainModels(builder, fromOpenApi, models);
      logger.lifecycle("Generated {} domain model(s) from OpenAPI", classNames.size());
    }
    if (hasName) {
      name = Utils.capitalize(name);
      ArchitectureValidation.validateModelName(name);
      logger.lifecycle("Model Name: {}", name);
      builder.addParam("modelName", name.toLowerCase());
      builder.addParam("modelClassName", name);
      builder.setupFromTemplate("model");
      builder.persist();
    }

  }

  @Override
  protected Optional<String> resolveAnalyticsType() {
    return Optional.of(name);
  }
}
