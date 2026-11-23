package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.ParamNotFoundException;
import co.com.bancolombia.factory.validations.architecture.ArchitectureValidation;
import co.com.bancolombia.task.annotations.CATask;
import co.com.bancolombia.utils.Utils;
import co.com.bancolombia.utils.swagger.OpenApiSpecContext;
import co.com.bancolombia.utils.swagger.Swagger;
import io.swagger.codegen.v3.generators.DomainModelCodegen.ModelTarget;
import io.swagger.codegen.v3.generators.OpenApiModelPackageResolver;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.gradle.api.tasks.options.Option;
import org.gradle.work.DisableCachingByDefault;

@CATask(name = "generateModel", shortcut = "gm", description = "Generate model in domain layer")
@DisableCachingByDefault(because = "Generates and modifies project files, it has side effects")
public class GenerateModelTask extends AbstractCleanArchitectureDefaultTask {

  private String name = "";
  private String fromOpenApi;
  private String models;
  private String requestModels;
  private String responseModels;

  @Option(option = "name", description = "Set the model name")
  public void setName(String modelName) {
    this.name = modelName;
  }

  @Option(
      option = "from-swagger",
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

  @Option(
      option = "request-models",
      description =
          "(Optional) OpenAPI schema names to place under entities.request "
              + "(comma-separated, exact schema keys)")
  public void setRequestModels(String requestModels) {
    this.requestModels = requestModels;
  }

  @Option(
      option = "response-models",
      description =
          "(Optional) OpenAPI schema names to place under entities.response "
              + "(comma-separated, exact schema keys)")
  public void setResponseModels(String responseModels) {
    this.responseModels = responseModels;
  }

  @Override
  protected void doExecute() throws IOException, ParamNotFoundException {
    boolean hasName = name != null && !name.isBlank();
    boolean hasSpec = fromOpenApi != null && !fromOpenApi.isBlank();

    if (!hasName && !hasSpec) {
      printHelp();
      throw new IllegalArgumentException(
          "No model name or no OpenAPI specification, \r\n "
              + "usage: gradle generateModel --from-swagger [path-or-url]"
              + "\r\n or \r\n"
              + "usage: gradle generateModel --name [name]");
    }
    logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
    if (hasSpec) {
      generateFromOpenApi();
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

  private void generateFromOpenApi() throws IOException {
    if (models != null && !models.isBlank()) {
      logger.lifecycle("Schemas filter: {}", models);
    }
    var requestKeys = OpenApiModelPackageResolver.parseSchemaKeys(requestModels);
    var responseKeys = OpenApiModelPackageResolver.parseSchemaKeys(responseModels);
    OpenApiSpecContext spec = Swagger.loadOpenApiSpec(builder, fromOpenApi);
    List<ModelTarget> targets =
        Swagger.planDomainModels(builder, spec, models, requestKeys, responseKeys);
    warnExistingTargets(targets);

    List<String> classNames =
        Swagger.generateDomainModels(builder, spec, models, requestModels, responseModels);
    logger.lifecycle("Generated {} domain model(s) from OpenAPI", classNames.size());
  }

  private void warnExistingTargets(List<ModelTarget> targets) {
    Path sourceRoot = Swagger.domainModelSourceRoot(builder);
    List<String> existing =
        targets.stream()
            .map(ModelTarget::relativePath)
            .filter(path -> Files.exists(sourceRoot.resolve(path)))
            .toList();
    if (!existing.isEmpty()) {
      logger.warn("Skipping models that already exist: {}", existing);
    }
  }

  @Override
  protected Optional<String> resolveAnalyticsType() {
    return Optional.ofNullable(name).filter(value -> !value.isBlank());
  }
}
