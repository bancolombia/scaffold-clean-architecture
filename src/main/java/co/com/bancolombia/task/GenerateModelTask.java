package co.com.bancolombia.task;

import co.com.bancolombia.Constants;
import co.com.bancolombia.Utils;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import java.io.IOException;


public class GenerateModelTask extends DefaultTask {
    private String modelName = "";
    private Logger logger = getProject().getLogger();

    @Option(option = "name", description = "Set the model name")
    public void setNameProject(String modelName) { this.modelName = modelName; }

    @TaskAction
    public void generateModelTask() throws IOException {
        String packageName;
        if (modelName.isEmpty()) {
            throw new IllegalArgumentException("No model name, usege: gradle generateModel --name modelName");
        }
        packageName = Utils.readProperties("package");
        logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
        logger.lifecycle("Project  Package: {}", packageName);
        packageName = packageName.replaceAll("\\.", "\\/");
        logger.lifecycle("Model Name: {}", modelName);
        logger.lifecycle("Generating Childs Dirs");
        getProject().mkdir(Constants.DOMAIN.concat("/").concat(Constants.MODEL).concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(Constants.MODEL).concat("/").concat(Utils.decapitalize(modelName)).concat("/").concat(Constants.GATEWAYS));
        getProject().mkdir(Constants.DOMAIN.concat("/").concat(Constants.MODEL).concat("/").concat(Constants.TEST_JAVA).concat("/").concat(packageName).concat("/").concat(Constants.MODEL).concat("/").concat(Utils.decapitalize(modelName)));

        logger.lifecycle("Generated Childs Dirs");

        logger.lifecycle("Writing in Files");
        Utils.writeString(getProject(), Constants.DOMAIN.concat("/").concat(Constants.MODEL).concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(Constants.MODEL).concat("/").concat(Utils.decapitalize(modelName)).concat("/").concat(Constants.GATEWAYS).concat("/").concat(Utils.capitalize(modelName) + Constants.REPOSITORY + Constants.JAVA_EXTENSION), Constants.getInterfaceModel(modelName, packageName));
        Utils.writeString(getProject(), Constants.DOMAIN.concat("/").concat(Constants.MODEL).concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(Constants.MODEL).concat("/").concat(Utils.decapitalize(modelName)).concat("/").concat(Utils.capitalize(modelName) + Constants.JAVA_EXTENSION), Constants.getModel(modelName, packageName));
        logger.lifecycle("Writed in Files");

    }
}
