package co.com.bancolombia.task;

import co.com.bancolombia.Constants;
import co.com.bancolombia.Utils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;



public class GenerateModelTask extends DefaultTask {
    public String modelName = "";
    public String _package = "co.com.bancolombia";

    @Option(option = "name", description = "Set the model name")
    public void setNameProject(String modelName) {
        if (!modelName.isEmpty()) {
            this.modelName = modelName;
        }
    }

    @TaskAction
    public void GenerateModel() throws Exception {
        if (modelName.isEmpty()) {
            throw new IllegalArgumentException("No model name, usege: gradle generateModel --name modelName");
        }
        _package = Utils.readProperties("package");
        System.out.println("Clean Architecture plugin version: " + Utils.getVersionPlugin());
        System.out.println("Project  Package: " + _package);
        _package = _package.replaceAll("\\.", "\\/");
        System.out.println("Model Name: " + modelName);
        System.out.println("Generating Childs Dirs");
        getProject().mkdir(Constants.domain.concat("/").concat(Constants.model).concat("/").concat(Constants.mainJava).concat("/").concat(_package).concat("/").concat(Constants.model).concat("/").concat(Utils.decapitalize(modelName)).concat("/").concat(Constants.gateway));
        System.out.println("Generated Childs Dirs");

        System.out.println("Generating Base Files");
        getProject().file(Constants.domain.concat("/").concat(Constants.model).concat("/").concat(Constants.mainJava).concat("/").concat(_package).concat("/").concat(Constants.model).concat("/").concat(Utils.decapitalize(modelName).concat("/").concat(Constants.gateway).concat("/").concat(Utils.capitalize(modelName) + Constants.repository + Constants.javaExtension))).createNewFile();
        getProject().file(Constants.domain.concat("/").concat(Constants.model).concat("/").concat(Constants.mainJava).concat("/").concat(_package).concat("/").concat(Constants.model).concat("/").concat(Utils.decapitalize(modelName).concat("/").concat(Utils.capitalize(modelName) + Constants.javaExtension))).createNewFile();

        System.out.println("Generated Base Files");
        System.out.println("Writing in Files");
        Utils.writeString(getProject(), Constants.domain.concat("/").concat(Constants.model).concat("/").concat(Constants.mainJava).concat("/").concat(_package).concat("/").concat(Constants.model).concat("/").concat(Utils.decapitalize(modelName)).concat("/").concat(Constants.gateway).concat("/").concat(Utils.capitalize(modelName) + Constants.repository + Constants.javaExtension), Constants.getInterfaceModel(modelName, _package));
        Utils.writeString(getProject(), Constants.domain.concat("/").concat(Constants.model).concat("/").concat(Constants.mainJava).concat("/").concat(_package).concat("/").concat(Constants.model).concat("/").concat(Utils.decapitalize(modelName)).concat("/").concat(Utils.capitalize(modelName) + Constants.javaExtension), Constants.getModel(modelName, _package));
        System.out.println("Writed in Files");

    }


}
