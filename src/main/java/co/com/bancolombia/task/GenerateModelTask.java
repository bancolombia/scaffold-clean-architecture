package co.com.bancolombia.task;

import co.com.bancolombia.Constants;
import co.com.bancolombia.Utils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GenerateModelTask extends DefaultTask {
    private String modelName = "";
    Logger logger = LoggerFactory.getLogger(GenerateModelTask.class);

    @Option(option = "name", description = "Set the model name")
    public void setNameProject(String modelName) {
        if (!modelName.isEmpty()) {
            this.modelName = modelName;
        }
    }

    @TaskAction
    public void generateModel() throws Exception {
        String packageName;
        if (modelName.isEmpty()) {
            throw new IllegalArgumentException("No model name, usege: gradle generateModel --name modelName");
        }
        packageName = Utils.readProperties("package");
        logger.info("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
        logger.info("Project  Package: {}", packageName);
        packageName = packageName.replaceAll("\\.", "\\/");
        logger.info("Model Name: {}", modelName);
        logger.info("Generating Childs Dirs");
        getProject().mkdir(Constants.DOMAIN.concat("/").concat(Constants.MODEL).concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(Constants.MODEL).concat("/").concat(Utils.decapitalize(modelName)).concat("/").concat(Constants.GATEWAYS));
        logger.info("Generated Childs Dirs");

        logger.info("Generating Base Files");
        getProject().file(Constants.DOMAIN.concat("/").concat(Constants.MODEL).concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(Constants.MODEL).concat("/").concat(Utils.decapitalize(modelName).concat("/").concat(Constants.GATEWAYS).concat("/").concat(Utils.capitalize(modelName) + Constants.REPOSITORY + Constants.JAVA_EXTENSION))).createNewFile();
        getProject().file(Constants.DOMAIN.concat("/").concat(Constants.MODEL).concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(Constants.MODEL).concat("/").concat(Utils.decapitalize(modelName).concat("/").concat(Utils.capitalize(modelName) + Constants.JAVA_EXTENSION))).createNewFile();

        logger.info("Generated Base Files");
        logger.info("Writing in Files");
        Utils.writeString(getProject(), Constants.DOMAIN.concat("/").concat(Constants.MODEL).concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(Constants.MODEL).concat("/").concat(Utils.decapitalize(modelName)).concat("/").concat(Constants.GATEWAYS).concat("/").concat(Utils.capitalize(modelName) + Constants.REPOSITORY + Constants.JAVA_EXTENSION), Constants.getInterfaceModel(modelName, packageName));
        Utils.writeString(getProject(), Constants.DOMAIN.concat("/").concat(Constants.MODEL).concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(Constants.MODEL).concat("/").concat(Utils.decapitalize(modelName)).concat("/").concat(Utils.capitalize(modelName) + Constants.JAVA_EXTENSION), Constants.getModel(modelName, packageName));
        logger.info("Writed in Files");

    }


}
