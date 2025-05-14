package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.ParamNotFoundException;
import co.com.bancolombia.factory.validations.architecture.ArchitectureValidation;
import co.com.bancolombia.task.annotations.CATask;
import co.com.bancolombia.utils.Utils;
import java.io.IOException;
import java.util.Optional;
import org.gradle.api.tasks.options.Option;

@CATask(
    name = "generateUseCase",
    shortcut = "guc",
    description = "Generate use case in domain layer")
public class GenerateUseCaseTask extends AbstractCleanArchitectureDefaultTask {
  private static final String USECASE_CLASS_NAME = "UseCase";
  private String name = "";

  public GenerateUseCaseTask() {
    notCompatibleWithConfigurationCache("This task performs validations that should always run");
  }

  @Option(option = "name", description = "Set UseCase name")
  public void setName(String useCaseName) {
    this.name = useCaseName;
  }

  @Override
  public void execute() throws IOException, ParamNotFoundException {
    if (name == null || name.isEmpty()) {
      printHelp();
      throw new IllegalArgumentException(
          "No use case name, usage: gradle generateUseCase --name [name]");
    }
    name = Utils.capitalize(name);
    ArchitectureValidation.validateUseCaseName(name);
    String className = refactorName(name);
    String useCaseName = className.replace(USECASE_CLASS_NAME, "").toLowerCase();
    logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
    logger.lifecycle("Use Case Name: {}", name);
    builder.addParam("useCaseName", useCaseName);
    builder.addParam("useCaseClassName", className);
    builder.setupFromTemplate("usecase");
    builder.persist();
  }

  @Override
  protected Optional<String> resolveAnalyticsType() {
    return Optional.of(name);
  }

  private String refactorName(String useCaseName) {
    if (useCaseName.endsWith(USECASE_CLASS_NAME)) {
      return useCaseName;
    }
    return useCaseName + USECASE_CLASS_NAME;
  }
}
