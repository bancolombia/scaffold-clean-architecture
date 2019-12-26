package co.com.bancolombia.task;

import co.com.bancolombia.Constants;
import co.com.bancolombia.Utils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GenerateStructureTask extends DefaultTask {
    private Logger logger = LoggerFactory.getLogger(GenerateStructureTask.class);
    private String packageName = "co.com.bancolombia";
    private String type = "imperative";
    private String projectName = "cleanArchitecture";

    @Option(option = "package", description = "Set the principal package to use in the project")
    public void setPackage(String packageName) {
        if (!packageName.isEmpty()) {
            this.packageName = packageName;
        }
    }

    @Option(option = "type", description = "Set project type, the options are  (reactive | imperative) ")
    public void setType(String type) {
        if (!type.isEmpty()) {
            this.type = type;
        }
    }

    @Option(option = "name", description = "Set the project name, by default is cleanArchitecture ")
    public void setProjectName(String projectName) {
        if (!projectName.isEmpty()) {
            this.projectName = projectName;
        }
    }


    @TaskAction
    public void generateStructure() throws Exception {

        logger.info("Clean Architecture plugin version: " + Utils.getVersionPlugin());
        logger.info("Package: " + packageName);
        packageName = packageName.replaceAll("\\.", "\\/");
        logger.info("Project Type: " + type);
        logger.info("Project Name: " + projectName);
        logger.info("Generating base directories");
        getProject().mkdir(Constants.infraestucture);
        logger.info(Constants.infraestucture + "directory has been created.");
        getProject().mkdir(Constants.domain);
        logger.info(Constants.domain + "directory has been created.");
        getProject().mkdir(Constants.application);
        logger.info(Constants.application + "directory has been created.");
        logger.info("Directories base have been created.");

        logger.info("Generating sub directories");
        getProject().mkdir(Constants.application.concat("/").concat(Constants.mainJava).concat("/").concat(packageName).concat("/").concat(Constants.config));
        getProject().mkdir(Constants.application.concat("/").concat(Constants.testJava).concat("/").concat(packageName));
        getProject().mkdir(Constants.application.concat("/").concat(Constants.mainResource));

        getProject().mkdir(Constants.infraestucture.concat("/").concat(Constants.drivenAdapters));
        getProject().mkdir(Constants.infraestucture.concat("/").concat(Constants.entryPoints));
        getProject().mkdir(Constants.infraestucture.concat("/").concat(Constants.helpers));

        getProject().mkdir(Constants.domain.concat("/").concat(Constants.model).concat("/").concat(Constants.mainJava).concat("/").concat(packageName).concat("/").concat(Constants.model));
        getProject().mkdir(Constants.domain.concat("/").concat(Constants.model).concat("/").concat(Constants.testJava).concat("/").concat(packageName).concat("/").concat(Constants.model));
        getProject().mkdir(Constants.domain.concat("/").concat(Constants.usecase).concat("/").concat(Constants.mainJava).concat("/").concat(packageName).concat("/").concat(Constants.usecase));
        getProject().mkdir(Constants.domain.concat("/").concat(Constants.usecase).concat("/").concat(Constants.testJava).concat("/").concat(packageName).concat("/").concat(Constants.usecase));

        logger.info("Generated Childs Dirs");

        logger.info("Generating Base Files");
        getProject().file(Constants.domain.concat("/").concat(Constants.model).concat("/").concat(Constants.buildGradle)).createNewFile();
        getProject().file(Constants.domain.concat("/").concat(Constants.usecase).concat("/").concat(Constants.buildGradle)).createNewFile();

        getProject().file(Constants.application.concat("/").concat(Constants.buildGradle)).createNewFile();
        getProject().file(Constants.application.concat("/").concat(Constants.mainResource).concat("/").concat(Constants.applicationProperties)).createNewFile();
        getProject().file(Constants.application.concat("/").concat(Constants.mainResource).concat("/").concat(Constants.log4j)).createNewFile();
        getProject().file(Constants.application.concat("/").concat(Constants.mainJava).concat("/").concat(packageName).concat("/").concat(Constants.mainApplication)).createNewFile();

        getProject().file(Constants.mainGradle).createNewFile();
        getProject().file(Constants.lombokConfig).createNewFile();
        getProject().file(Constants.settingsGradle).createNewFile();
        getProject().file(Constants.gitignore).createNewFile();
        getProject().file(Constants.readMe).createNewFile();
        getProject().file(Constants.gradleProperties).createNewFile();


        logger.info("Generated Base Files");
        logger.info("Writing in Files");

        Utils.writeString(getProject(), Constants.domain.concat("/").concat(Constants.usecase).concat("/").concat(Constants.buildGradle), Constants.buildGradleUseCaseContent);
        Utils.writeString(getProject(), Constants.lombokConfig, Constants.lombokConfigContent);
        Utils.writeString(getProject(), Constants.gitignore, Constants.gitIgnoreContent);
        Utils.writeString(getProject(), Constants.readMe, Constants.readmeContent);
        Utils.writeString(getProject(), Constants.gradleProperties, Constants.getGradlePropertiesContent(packageName));
        Utils.writeString(getProject(), Constants.settingsGradle, Constants.getSettingsGradleContent(this.projectName));
        Utils.writeString(getProject(), Constants.mainGradle, Constants.mainGradleContent);
        Utils.writeString(getProject(), Constants.buildGradle, Constants.getBuildGradleContent());
        Utils.writeString(getProject(), Constants.application.concat("/").concat(Constants.buildGradle), Constants.buildGradleApplicationContent);
        Utils.writeString(getProject(), Constants.application.concat("/").concat(Constants.mainResource).concat("/").concat(Constants.applicationProperties), Constants.getApplicationPropertiesContent(this.projectName));
        Utils.writeString(getProject(), Constants.application.concat("/").concat(Constants.mainResource).concat("/").concat(Constants.log4j), Constants.log4jContent);
        Utils.writeString(getProject(), Constants.application.concat("/").concat(Constants.mainJava).concat("/").concat(packageName).concat("/").concat(Constants.mainApplication), Constants.getMainApplicationContent(this.projectName));

        logger.info("Writed in Files");

    }


}
