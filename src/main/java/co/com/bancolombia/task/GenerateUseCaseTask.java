package co.com.bancolombia.task;

import co.com.bancolombia.Constants;
import co.com.bancolombia.Utils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerateUseCaseTask extends DefaultTask {
    private Logger logger = LoggerFactory.getLogger(GenerateUseCaseTask.class);
    private String useCaseName = "";
    private String packageName = "co.com.bancolombia";

    @Option(option = "name", description = "Set the UseCase name")
    public void setNameProject(String useCaseName) {
        if (!useCaseName.isEmpty()) {
            this.useCaseName = useCaseName;
        }
    }

    @TaskAction
    public void generateUseCase() throws Exception {
        if (useCaseName.isEmpty()) {
            throw new IllegalArgumentException("No use case name, usege: gradle generateUseCase --name useCaseName");
        }
        String useCaseDir = Constants.domain.concat("/").concat(Constants.usecase).concat("/").concat(Constants.mainJava).concat("/").concat(packageName).concat("/").concat(Constants.usecase).concat("/").concat(Utils.decapitalize(useCaseName));
        packageName = Utils.readProperties("package");
        logger.info("Clean Architecture plugin version: " + Utils.getVersionPlugin());
        logger.info("Project  Package: " + packageName);
        packageName = packageName.replaceAll("\\.", "\\/");
        logger.info("Use Case Name: " + useCaseName);
        logger.info("Generating Childs Dirs");
        getProject().mkdir(useCaseDir);
        logger.info("Generated Childs Dirs");
        logger.info("Generating Base Files");
        getProject().file(useCaseDir.concat("/").concat(Utils.capitalize(useCaseName) + Constants.javaExtension)).createNewFile();
        logger.info("Generated Base Files");
        logger.info("Writing in Files");
        Utils.writeString(getProject(), useCaseDir.concat("/").concat(Utils.capitalize(useCaseName) + Constants.javaExtension), Constants.getUseCase(useCaseName, packageName));
        logger.info("Writed in Files");


    }
}

