package co.com.bancolombia.task;

import co.com.bancolombia.utils.FileUtils;
import co.com.bancolombia.utils.Utils;
import co.com.bancolombia.exceptions.ParamNotFoundException;
import co.com.bancolombia.factory.ModuleBuilder;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import java.io.IOException;

public class GenerateModelTask extends DefaultTask {
    private final ModuleBuilder builder = new ModuleBuilder(getProject());
    private final Logger logger = getProject().getLogger();

    private String modelName = "";

    @Option(option = "name", description = "Set the model name")
    public void setNameProject(String modelName) {
        this.modelName = modelName;
    }

    @TaskAction
    public void generateModelTask() throws IOException, ParamNotFoundException {
        if (modelName.isEmpty()) {
            throw new IllegalArgumentException("No model name, usage: gradle generateModel --name modelName");
        }
        String packageName = FileUtils.readProperties("package");
        modelName = Utils.capitalize(modelName);
        logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
        logger.lifecycle("Project  Package: {}", packageName);
        logger.lifecycle("Model Name: {}", modelName);
        builder.addParamPackage(packageName);
        builder.addParam("modelName", modelName.toLowerCase());
        builder.addParam("modelClassName", modelName);
        builder.setupFromTemplate("model");
        builder.persist();
    }
}
