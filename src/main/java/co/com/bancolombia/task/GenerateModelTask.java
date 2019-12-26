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
        logger.info("Clean Architecture plugin version: " + Utils.getVersionPlugin());
        logger.info("Project  Package: " + packageName);
        packageName = packageName.replaceAll("\\.", "\\/");
        logger.info("Model Name: " + modelName);
        logger.info("Generating Childs Dirs");
        getProject().mkdir(Constants.domain.concat("/").concat(Constants.model).concat("/").concat(Constants.mainJava).concat("/").concat(packageName).concat("/").concat(Constants.model).concat("/").concat(Utils.decapitalize(modelName)).concat("/").concat(Constants.gateway));
        logger.info("Generated Childs Dirs");

        logger.info("Generating Base Files");
        getProject().file(Constants.domain.concat("/").concat(Constants.model).concat("/").concat(Constants.mainJava).concat("/").concat(packageName).concat("/").concat(Constants.model).concat("/").concat(Utils.decapitalize(modelName).concat("/").concat(Constants.gateway).concat("/").concat(Utils.capitalize(modelName) + Constants.repository + Constants.javaExtension))).createNewFile();
        getProject().file(Constants.domain.concat("/").concat(Constants.model).concat("/").concat(Constants.mainJava).concat("/").concat(packageName).concat("/").concat(Constants.model).concat("/").concat(Utils.decapitalize(modelName).concat("/").concat(Utils.capitalize(modelName) + Constants.javaExtension))).createNewFile();

        logger.info("Generated Base Files");
        logger.info("Writing in Files");
        Utils.writeString(getProject(), Constants.domain.concat("/").concat(Constants.model).concat("/").concat(Constants.mainJava).concat("/").concat(packageName).concat("/").concat(Constants.model).concat("/").concat(Utils.decapitalize(modelName)).concat("/").concat(Constants.gateway).concat("/").concat(Utils.capitalize(modelName) + Constants.repository + Constants.javaExtension), Constants.getInterfaceModel(modelName, packageName));
        Utils.writeString(getProject(), Constants.domain.concat("/").concat(Constants.model).concat("/").concat(Constants.mainJava).concat("/").concat(packageName).concat("/").concat(Constants.model).concat("/").concat(Utils.decapitalize(modelName)).concat("/").concat(Utils.capitalize(modelName) + Constants.javaExtension), Constants.getModel(modelName, packageName));
        logger.info("Writed in Files");

    }


}
