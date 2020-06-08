package co.com.bancolombia.task;

import co.com.bancolombia.Utils;
import co.com.bancolombia.exceptions.ParamNotFoundException;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import java.io.IOException;

public class GenerateModelTask extends GenerateBaseTask {
    private String modelName = "";
    private Logger logger = getProject().getLogger();

    @Option(option = "name", description = "Set the model name")
    public void setNameProject(String modelName) {
        this.modelName = modelName;
    }

    @TaskAction
    public void generateModelTask() throws IOException, ParamNotFoundException {
        if (modelName.isEmpty()) {
            throw new IllegalArgumentException("No model name, usage: gradle generateModel --name modelName");
        }
        String packageName = Utils.readProperties("package");
        modelName = Utils.capitalize(modelName);
        logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
        logger.lifecycle("Project  Package: {}", packageName);
        logger.lifecycle("Model Name: {}", modelName);
        addParamPackage(packageName);
        addParam("modelName", modelName.toLowerCase());
        addParam("modelClassName", modelName);
        setupFromTemplate("model");
        executeTask();
    }
}
