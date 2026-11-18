package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.ParamNotFoundException;
import co.com.bancolombia.factory.validations.architecture.ArchitectureValidation;
import co.com.bancolombia.task.annotations.CATask;
import co.com.bancolombia.utils.Utils;
import co.com.bancolombia.utils.swagger.Swagger;
import io.swagger.codegen.v3.generators.DomainModelCodegen.ModelTarget;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.gradle.api.tasks.options.Option;
import org.gradle.work.DisableCachingByDefault;

@CATask(name = "generateModel", shortcut = "gm", description = "Generate model in domain layer")
@DisableCachingByDefault(because = "Generates and modifies project files, it has side effects")
public class GenerateModelTask extends AbstractCleanArchitectureDefaultTask {

  private static final String MODEL_SOURCE_ROOT = "domain/model/src/main/java";

  private String name = "";
  private String fromOpenApi;
  private String models;

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
    List<ModelTarget> targets = Swagger.planDomainModels(builder, fromOpenApi, models);
    for (ModelTarget target : targets) {
      ArchitectureValidation.validateModelName(target.className());
    }
    failOnDuplicateTargets(targets);
    warnExistingTargets(targets);

    List<String> classNames = Swagger.generateDomainModels(builder, fromOpenApi, models);
    logger.lifecycle("Generated {} domain model(s) from OpenAPI", classNames.size());
  }

  private static void failOnDuplicateTargets(List<ModelTarget> targets) {
    Map<String, List<String>> schemasByPath =
        targets.stream()
            .collect(
                Collectors.groupingBy(
                    ModelTarget::relativePath,
                    Collectors.mapping(ModelTarget::schemaKey, Collectors.toList())));
    schemasByPath.forEach(
        (path, schemaKeys) -> {
          if (schemaKeys.size() > 1) {
            throw new IllegalArgumentException(
                "Schemas " + schemaKeys + " would all generate " + path);
          }
        });
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
