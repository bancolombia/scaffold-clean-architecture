package co.com.bancolombia.task;

import co.com.bancolombia.utils.FileUtils;
import co.com.bancolombia.utils.Utils;
import co.com.bancolombia.exceptions.ParamNotFoundException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.templates.Constants;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import java.io.IOException;

public class GenerateUseCaseTask extends DefaultTask {
    private final ModuleBuilder builder = new ModuleBuilder(getProject());
    private final Logger logger = getProject().getLogger();
    private String useCaseName = "";

    @Option(option = "name", description = "Set the UseCase name")
    public void setNameProject(String useCaseName) {
        this.useCaseName = useCaseName;
    }

    @TaskAction
    public void generateUseCaseTask() throws IOException, ParamNotFoundException {
        if (useCaseName.isEmpty()) {
            throw new IllegalArgumentException(
                    "No use case name, usege: gradle generateUseCase --name useCaseName");
        }
        String packageName = FileUtils.readProperties("package");
        useCaseName = Utils.capitalize(useCaseName);
        logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
        logger.lifecycle("Project  Package: {}", packageName);
        logger.lifecycle("Use Case Name: {}", useCaseName);
        builder.addParamPackage(packageName);
        builder.addParam("useCaseName", useCaseName.toLowerCase());
        builder.addParam("useCaseClassName", refactorName(useCaseName));
        builder.setupFromTemplate("usecase");
        builder.persist();
    }

    private String refactorName(String useCaseName) {
        if (useCaseName.endsWith(Constants.USECASE_CLASS_NAME)) {
            return useCaseName;
        }
        return useCaseName + Constants.USECASE_CLASS_NAME;
    }
}

