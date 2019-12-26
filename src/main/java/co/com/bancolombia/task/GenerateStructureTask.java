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

        logger.info("Clean Architecture plugin version: {0}", Utils.getVersionPlugin());
        logger.info("Package: {0}", packageName);
        packageName = packageName.replaceAll("\\.", "\\/");
        logger.info("Project Type: {0}", type);
        logger.info("Project Name: {0}", projectName);
        logger.info("Generating base directories");
        getProject().mkdir(Constants.INFRAESTUCTURE);
        logger.info("{0} directory has been created.", Constants.INFRAESTUCTURE);
        getProject().mkdir(Constants.DOMAIN);
        logger.info("{0} directory has been created.",Constants.DOMAIN);
        getProject().mkdir(Constants.APPLICATION);
        logger.info("{0} directory has been created.", Constants.APPLICATION);
        logger.info("Directories base have been created.");

        logger.info("Generating sub directories");
        getProject().mkdir(Constants.APPLICATION.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(Constants.CONFIG));
        getProject().mkdir(Constants.APPLICATION.concat("/").concat(Constants.TEST_JAVA).concat("/").concat(packageName));
        getProject().mkdir(Constants.APPLICATION.concat("/").concat(Constants.MAIN_RESOURCES));

        getProject().mkdir(Constants.INFRAESTUCTURE.concat("/").concat(Constants.DRIVEN_ADAPTERS));
        getProject().mkdir(Constants.INFRAESTUCTURE.concat("/").concat(Constants.ENTRY_POINTS));
        getProject().mkdir(Constants.INFRAESTUCTURE.concat("/").concat(Constants.HELPERS));

        getProject().mkdir(Constants.DOMAIN.concat("/").concat(Constants.MODEL).concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(Constants.MODEL));
        getProject().mkdir(Constants.DOMAIN.concat("/").concat(Constants.MODEL).concat("/").concat(Constants.TEST_JAVA).concat("/").concat(packageName).concat("/").concat(Constants.MODEL));
        getProject().mkdir(Constants.DOMAIN.concat("/").concat(Constants.USECASE).concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(Constants.USECASE));
        getProject().mkdir(Constants.DOMAIN.concat("/").concat(Constants.USECASE).concat("/").concat(Constants.TEST_JAVA).concat("/").concat(packageName).concat("/").concat(Constants.USECASE));

        logger.info("Generated Childs Dirs");

        logger.info("Generating Base Files");
        getProject().file(Constants.DOMAIN.concat("/").concat(Constants.MODEL).concat("/").concat(Constants.BUILD_GRADLE)).createNewFile();
        getProject().file(Constants.DOMAIN.concat("/").concat(Constants.USECASE).concat("/").concat(Constants.BUILD_GRADLE)).createNewFile();

        getProject().file(Constants.APPLICATION.concat("/").concat(Constants.BUILD_GRADLE)).createNewFile();
        getProject().file(Constants.APPLICATION.concat("/").concat(Constants.MAIN_RESOURCES).concat("/").concat(Constants.APPLICATION_PROPERTIES)).createNewFile();
        getProject().file(Constants.APPLICATION.concat("/").concat(Constants.MAIN_RESOURCES).concat("/").concat(Constants.LOG_4_J)).createNewFile();
        getProject().file(Constants.APPLICATION.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(Constants.MAIN_APPLICATION)).createNewFile();

        getProject().file(Constants.MAIN_GRADLE).createNewFile();
        getProject().file(Constants.LOMBOK_CONFIG).createNewFile();
        getProject().file(Constants.SETTINGS_GRADLE).createNewFile();
        getProject().file(Constants.GITIGNORE).createNewFile();
        getProject().file(Constants.READ_ME).createNewFile();
        getProject().file(Constants.GRADLE_PROPERTIES).createNewFile();


        logger.info("Generated Base Files");
        logger.info("Writing in Files");

        Utils.writeString(getProject(), Constants.DOMAIN.concat("/").concat(Constants.USECASE).concat("/").concat(Constants.BUILD_GRADLE), Constants.BUILD_GRADLE_USE_CASE_CONTENT);
        Utils.writeString(getProject(), Constants.LOMBOK_CONFIG, Constants.LOMBOK_CONFIG_CONTENT);
        Utils.writeString(getProject(), Constants.GITIGNORE, Constants.GIT_IGNORE_CONTENT);
        Utils.writeString(getProject(), Constants.READ_ME, Constants.README_CONTENT);
        Utils.writeString(getProject(), Constants.GRADLE_PROPERTIES, Constants.getGradlePropertiesContent(packageName));
        Utils.writeString(getProject(), Constants.SETTINGS_GRADLE, Constants.getSettingsGradleContent(this.projectName));
        Utils.writeString(getProject(), Constants.MAIN_GRADLE, Constants.MAIN_GRADLE_CONTENT);
        Utils.writeString(getProject(), Constants.BUILD_GRADLE, Constants.getBuildGradleContent());
        Utils.writeString(getProject(), Constants.APPLICATION.concat("/").concat(Constants.BUILD_GRADLE), Constants.BUILD_GRADLE_APPLICATION_CONTENT);
        Utils.writeString(getProject(), Constants.APPLICATION.concat("/").concat(Constants.MAIN_RESOURCES).concat("/").concat(Constants.APPLICATION_PROPERTIES), Constants.getApplicationPropertiesContent(this.projectName));
        Utils.writeString(getProject(), Constants.APPLICATION.concat("/").concat(Constants.MAIN_RESOURCES).concat("/").concat(Constants.LOG_4_J), Constants.LOG_4_J_CONTENT);
        Utils.writeString(getProject(), Constants.APPLICATION.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(Constants.MAIN_APPLICATION), Constants.getMainApplicationContent(this.projectName));

        logger.info("Writed in Files");

    }


}
