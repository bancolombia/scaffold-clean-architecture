package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.ParamNotFoundException;
import co.com.bancolombia.utils.Utils;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import java.io.IOException;

public class GenerateUseCaseTask extends CleanArchitectureDefaultTask {
    private static final String USECASE_CLASS_NAME = "UseCase";
    private String name = "";

    @Option(option = "name", description = "Set UseCase name")
    public void setName(String useCaseName) {
        this.name = useCaseName;
    }

    @TaskAction
    public void generateUseCaseTask() throws IOException, ParamNotFoundException {
        if (name.isEmpty()) {
            printHelp();
            throw new IllegalArgumentException(
                    "No use case name, usage: gradle generateUseCase --name [name]");
        }
        name = Utils.capitalize(name);
        String className = refactorName(name);
        String useCaseName = className.replace(USECASE_CLASS_NAME, "").toLowerCase();
        logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
        logger.lifecycle("Use Case Name: {}", name);
        builder.loadPackage();
        builder.addParam("useCaseName", useCaseName);
        builder.addParam("useCaseClassName", className);
        builder.addParam("lombok", builder.isEnableLombok());

        builder.setupFromTemplate("usecase");
        builder.persist();
    }

    private String refactorName(String useCaseName) {
        if (useCaseName.endsWith(USECASE_CLASS_NAME)) {
            return useCaseName;
        }
        return useCaseName + USECASE_CLASS_NAME;
    }
}

