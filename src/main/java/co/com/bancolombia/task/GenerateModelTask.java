package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.ParamNotFoundException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.utils.Utils;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import java.io.IOException;

public class GenerateModelTask extends DefaultTask {
    private final ModuleBuilder builder = new ModuleBuilder(getProject());
    private final Logger logger = getProject().getLogger();

    private String name = "";

    @Option(option = "name", description = "Set the model name")
    public void setName(String modelName) {
        this.name = modelName;
    }

    @TaskAction
    public void generateModelTask() throws IOException, ParamNotFoundException {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("No model name, usage: gradle generateModel --name [name]");
        }
        name = Utils.capitalize(name);
        logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
        logger.lifecycle("Model Name: {}", name);
        builder.loadPackage();
        builder.addParam("modelName", name.toLowerCase());
        builder.addParam("modelClassName", name);
        builder.setupFromTemplate("model");
        builder.persist();
    }
}
