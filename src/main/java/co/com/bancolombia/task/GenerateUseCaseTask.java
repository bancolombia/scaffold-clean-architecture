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

    @Option(option = "name", description = "Set the UseCase name")
    public void setNameProject(String useCaseName) {
        if (!useCaseName.isEmpty()) {
            this.useCaseName = useCaseName;
        }
    }

    @TaskAction
    public void generateUseCase() throws Exception {
        String packageName;
        if (useCaseName.isEmpty()) {
            throw new IllegalArgumentException("No use case name, usege: gradle generateUseCase --name useCaseName");
        }
        packageName = Utils.readProperties("package");
        logger.info("Clean Architecture plugin version: {}" , Utils.getVersionPlugin());
        logger.info("Project  Package: {}", packageName);
        packageName = packageName.replaceAll("\\.", "\\/");
        String useCaseDir = Constants.DOMAIN.concat("/").concat(Constants.USECASE).concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(Constants.USECASE).concat("/").concat(Utils.decapitalize(useCaseName));
        logger.info("Use Case Name: {}", useCaseName);
        logger.info("Generating Childs Dirs");
        getProject().mkdir(useCaseDir);
        logger.info("Generated Childs Dirs");
        logger.info("Generating Base Files");
        getProject().file(useCaseDir.concat("/").concat(Utils.capitalize(useCaseName) + Constants.JAVA_EXTENSION)).createNewFile();
        logger.info("Generated Base Files");
        logger.info("Writing in Files");
        Utils.writeString(getProject(), useCaseDir.concat("/").concat(Utils.capitalize(useCaseName) + Constants.JAVA_EXTENSION), Constants.getUseCase(useCaseName, packageName));
        logger.info("Writed in Files");


    }
}

