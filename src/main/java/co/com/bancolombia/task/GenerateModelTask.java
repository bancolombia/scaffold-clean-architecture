package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.ParamNotFoundException;
import co.com.bancolombia.utils.Utils;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import java.io.IOException;

public class GenerateModelTask extends CleanArchitectureDefaultTask {
    private String name = "";

    @Option(option = "name", description = "Set the model name")
    public void setName(String modelName) {
        this.name = modelName;
    }

    @TaskAction
    public void generateModelTask() throws IOException, ParamNotFoundException {
        if (name.isEmpty()) {
            printHelp();
            throw new IllegalArgumentException("No model name, usage: gradle generateModel --name [name]");
        }
        name = Utils.capitalize(name);
        logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
        logger.lifecycle("Model Name: {}", name);
        builder.loadPackage();
        builder.addParam("modelName", name.toLowerCase());
        builder.addParam("modelClassName", name);
        builder.addParam("lombok", builder.isEnableLombok());

        builder.setupFromTemplate("model");
        builder.persist();
    }
}
