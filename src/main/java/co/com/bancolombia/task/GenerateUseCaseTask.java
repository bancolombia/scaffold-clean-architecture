package co.com.bancolombia.task;

import co.com.bancolombia.Constants;
import co.com.bancolombia.Utils;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import java.io.IOException;

public class GenerateUseCaseTask extends DefaultTask {
    private Logger logger = getProject().getLogger();
    private String useCaseName = "";

    @Option(option = "name", description = "Set the UseCase name")
    public void setNameProject(String useCaseName) { this.useCaseName = useCaseName; }

    @TaskAction
    public void generateUseCase() throws IOException {
        String packageName;
        if (useCaseName.isEmpty()) {
            throw new IllegalArgumentException("No use case name, usege: gradle generateUseCase --name useCaseName");
        }
        packageName = Utils.readProperties("package");
        logger.lifecycle("Clean Architecture plugin version: {}" , Utils.getVersionPlugin());
        logger.lifecycle("Project  Package: {}", packageName);
        packageName = packageName.replaceAll("\\.", "\\/");
        String useCaseDir = Constants.DOMAIN.concat("/").concat(Constants.USECASE).concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(Constants.USECASE).concat("/").concat(Utils.decapitalize(useCaseName));
        logger.lifecycle("Use Case Name: {}", useCaseName);
        logger.lifecycle("Generating Childs Dirs");
        getProject().mkdir(useCaseDir);
        logger.lifecycle("Generated Childs Dirs");
        logger.lifecycle("Generating Base Files");
        getProject().file(useCaseDir.concat("/").concat(Utils.capitalize(useCaseName) + Constants.JAVA_EXTENSION)).createNewFile();
        logger.lifecycle("Generated Base Files");
        logger.lifecycle("Writing in Files");
        Utils.writeString(getProject(), useCaseDir.concat("/").concat(Utils.capitalize(useCaseName) + Constants.JAVA_EXTENSION), Constants.getUseCase(useCaseName, packageName));
        logger.lifecycle("Writed in Files");


    }
}

