package co.com.bancolombia.task;

import co.com.bancolombia.Constants;
import co.com.bancolombia.Utils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class GenerateModelTask extends DefaultTask {
    public String nameModel = "";
    public String _package = "";

    @Option(option = "name", description = "Set the name of the model default is cleanArchitecture ")
    public void setNameProject(String nameModel) {
        if (!nameModel.isEmpty()) {
            this.nameModel = nameModel;
        }
    }


    @TaskAction
    public void Generate() throws IOException {
        if (nameModel.isEmpty() || nameModel.isBlank()) {
            System.out.println("Set the name of the model with --name");
            System.exit(1);
        }
        System.out.println("Name Model: " + nameModel);

        System.out.println("Generating Childs Dirs");
        getProject().mkdir(Constants.domain.concat("/").concat(Constants.model).concat("/").concat(Constants.mainJava).concat("/").concat(_package).concat("/").concat(Constants.model).concat("/").concat(Utils.decapitalize(nameModel)).concat("/").concat(Constants.gateway));
        System.out.println("Generated Childs Dirs");

        System.out.println("Generating Base Files");
        getProject().file(Constants.domain.concat("/").concat(Constants.model).concat("/").concat(Constants.mainJava).concat("/").concat(_package).concat("/").concat(Constants.model).concat("/").concat(Utils.decapitalize(nameModel).concat("/").concat(Constants.gateway).concat("/").concat(Utils.capitalize(nameModel) + Constants.repository + Constants.javaExtension))).createNewFile();
        getProject().file(Constants.domain.concat("/").concat(Constants.model).concat("/").concat(Constants.mainJava).concat("/").concat(_package).concat("/").concat(Constants.model).concat("/").concat(Utils.decapitalize(nameModel).concat("/").concat(Utils.capitalize(nameModel) + Constants.javaExtension))).createNewFile();


        System.out.println("Generated Base Files");
        System.out.println("Writing in Files");
        Utils.writeString(getProject(), Constants.domain.concat("/").concat(Constants.model).concat("/").concat(Constants.mainJava).concat("/").concat(_package).concat("/").concat(Constants.model).concat("/").concat(Utils.decapitalize(nameModel)).concat("/").concat(Constants.gateway).concat("/").concat(Utils.capitalize(nameModel) + Constants.repository + Constants.javaExtension), Constants.getInterfaceModel(nameModel, _package));
        Utils.writeString(getProject(), Constants.domain.concat("/").concat(Constants.model).concat("/").concat(Constants.mainJava).concat("/").concat(_package).concat("/").concat(Constants.model).concat("/").concat(Utils.decapitalize(nameModel)).concat("/").concat(Utils.capitalize(nameModel) + Constants.javaExtension), Constants.getModel(nameModel, _package));
        System.out.println("Writed in Files");

    }


}
