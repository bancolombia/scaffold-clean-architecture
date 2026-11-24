package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.ParamNotFoundException;
import co.com.bancolombia.factory.validations.architecture.ArchitectureValidation;
import co.com.bancolombia.task.annotations.CATask;
import co.com.bancolombia.utils.Utils;
import co.com.bancolombia.utils.swagger.Swagger;
import co.com.bancolombia.utils.swagger.Swagger.DomainModelPlan;
import io.swagger.codegen.v3.generators.DomainModelCodegen.ModelTarget;
import io.swagger.v3.oas.models.OpenAPI;
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
  private String renameModels;

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

  @Option(
      option = "rename-models",
      description =
          "(Optional) Domain class names for OpenAPI schemas, as schemaKey:ClassName pairs "
              + "(comma-separated). By default the Dto/Request/Response suffixes are removed")
  public void setRenameModels(String renameModels) {
    this.renameModels = renameModels;
  }

  @Override
  protected void doExecute() throws IOException, ParamNotFoundException {
    boolean hasName = isSet(name);
    boolean hasSpec = isSet(fromOpenApi);

    if (!hasName && !hasSpec) {
      printHelp();
      throw new IllegalArgumentException(
          "No model name or no OpenAPI specification. Usage:"
              + System.lineSeparator()
              + "  gradle generateModel --from-swagger [path-or-url]"
              + System.lineSeparator()
              + "  gradle generateModel --name [name]");
    }
    if (!hasSpec
        && (isSet(models)
            || isSet(requestModels)
            || isSet(responseModels)
            || isSet(renameModels))) {
      throw new IllegalArgumentException(
          "--models, --request-models, --response-models and --rename-models "
              + "require --from-swagger");
    }
    logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
    // Validate every input before writing any file
    if (hasName) {
      name = Utils.capitalize(name);
      ArchitectureValidation.validateModelName(name);
    }
    DomainModelPlan plan = hasSpec ? planFromOpenApi() : null;

    if (plan != null) {
      warnExistingTargets(plan.targets());
      List<String> classNames = Swagger.generateDomainModels(builder, plan);
      logger.lifecycle("Generated {} domain model(s) from OpenAPI", classNames.size());
    }
    if (hasName) {
      logger.lifecycle("Model Name: {}", name);
      builder.addParam("modelName", name.toLowerCase());
      builder.addParam("modelClassName", name);
      builder.setupFromTemplate("model");
      builder.persist();
    }
  }

  private DomainModelPlan planFromOpenApi() {
    if (isSet(models)) {
      logger.lifecycle("Schemas filter: {}", models);
    }
    OpenAPI openAPI = Swagger.loadOpenApiSpec(builder, fromOpenApi);
    return Swagger.planDomainModels(
        builder, openAPI, models, requestModels, responseModels, renameModels);
  }

  private static boolean isSet(String value) {
    return value != null && !value.isBlank();
  }

  private void warnExistingTargets(List<ModelTarget> targets) {
    Path sourceRoot = Swagger.domainModelSourceRoot(builder);
    // Name the schema of each skipped file: with suffixes removed, a file written by another
    // schema in an earlier run (book vs bookResponse) is kept instead of this one
    List<String> existing =
        targets.stream()
            .filter(target -> Files.exists(sourceRoot.resolve(target.relativePath())))
            .map(target -> target.relativePath() + " (schema '" + target.schemaKey() + "')")
            .toList();
    if (!existing.isEmpty()) {
      logger.warn(
          "Skipping models that already exist, check they match these schemas: {}", existing);
    }
  }

  @Override
  protected Optional<String> resolveAnalyticsType() {
    return Optional.ofNullable(name).filter(value -> !value.isBlank());
  }
}
