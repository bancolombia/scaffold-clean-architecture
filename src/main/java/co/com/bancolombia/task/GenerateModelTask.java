package co.com.bancolombia.task;

import co.com.bancolombia.templates.Constants;
import co.com.bancolombia.Utils;
import co.com.bancolombia.templates.ModelTemplate;
import co.com.bancolombia.templates.PluginTemplate;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import java.io.IOException;

public class GenerateModelTask extends DefaultTask {
    private String modelName = "";
    private Logger logger = getProject().getLogger();

    @Option(option = "name", description = "Set the model name")
    public void setNameProject(String modelName) {
        this.modelName = modelName;
    }

    @TaskAction
    public void generateModelTask() throws IOException {
        if (modelName.isEmpty()) {
            throw new IllegalArgumentException("No model name, usege: gradle generateModel --name modelName");
        }

        String packageName;
        packageName = Utils.readProperties("package");
        logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
        logger.lifecycle("Project  Package: {}", packageName);
        packageName = packageName.replaceAll("\\.", "\\/");
        logger.lifecycle("Model Name: {}", modelName);
        logger.lifecycle(PluginTemplate.GENERATING_CHILDS_DIRS);
        getProject().mkdir(Constants.DOMAIN.concat("/").concat(Constants.MODEL).concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(Constants.MODEL).concat("/").concat(Utils.decapitalize(modelName)).concat("/").concat(Constants.GATEWAYS));
        getProject().mkdir(Constants.DOMAIN.concat("/").concat(Constants.MODEL).concat("/").concat(Constants.TEST_JAVA).concat("/").concat(packageName).concat("/").concat(Constants.MODEL).concat("/").concat(Utils.decapitalize(modelName)));

        logger.lifecycle(PluginTemplate.GENERATED_CHILDS_DIRS);

        logger.lifecycle(PluginTemplate.GENERATING_FILES);
        Utils.writeString(getProject(), Constants.DOMAIN.concat("/").concat(Constants.MODEL).concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(Constants.MODEL).concat("/").concat(Utils.decapitalize(modelName)).concat("/").concat(Constants.GATEWAYS).concat("/").concat(Utils.capitalize(modelName) + Constants.REPOSITORY + Constants.JAVA_EXTENSION), ModelTemplate.getInterfaceModel(modelName, packageName));
        Utils.writeString(getProject(), Constants.DOMAIN.concat("/").concat(Constants.MODEL).concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(Constants.MODEL).concat("/").concat(Utils.decapitalize(modelName)).concat("/").concat(Utils.capitalize(modelName) + Constants.JAVA_EXTENSION), ModelTemplate.getModel(modelName, packageName));
        logger.lifecycle(PluginTemplate.WRITED_IN_FILES);

    }
}
