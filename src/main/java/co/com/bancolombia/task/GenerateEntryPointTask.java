package co.com.bancolombia.task;

import co.com.bancolombia.Constants;
import co.com.bancolombia.Utils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerateEntryPointTask extends DefaultTask {
    private int numberEntryPoint = -1;
    Logger logger = LoggerFactory.getLogger(GenerateEntryPointTask.class);

    @Option(option = "value", description = "Set the number of the entry point")
    public void setEntryPoint(String number) {
        if (!number.isEmpty()) {
                this.numberEntryPoint = Integer.parseInt(number);
            }
        }

    @TaskAction
    public void generateEntryPoint() throws Exception {
        String packageName;
        String nameEntryPoint;
        if (numberEntryPoint < 0) {
            throw new IllegalArgumentException("No Entry Point is set, usege: gradle generateEntryPoint --value numberEntryPoint");
        }

        nameEntryPoint = Constants.getNameEntryPoint(numberEntryPoint);
        if (nameEntryPoint.isEmpty()) {
            throw new IllegalArgumentException("Entry Point not is available");
        }
        packageName = Utils.readProperties("package");

        logger.info("Clean Architecture plugin version: {0}", Utils.getVersionPlugin());
        logger.info("Project  Package: {0}", packageName);
        packageName = packageName.replaceAll("\\.", "\\/");
        logger.info("Entry Point: {0} - {1}", numberEntryPoint, nameEntryPoint);


        switch (numberEntryPoint){
            case 1:
                generateApiRest(packageName);
                break;
            default:
                break;
        }
    }

    private void generateApiRest(String packageName) throws Exception  {
        logger.info("Generating Childs Dirs");
        String entryPoint = "apirest";
        String entryPointDir = Constants.INFRAESTUCTURE.concat("/").concat(Constants.ENTRY_POINTS).concat("/").concat(entryPoint);
        getProject().mkdir(entryPointDir.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(entryPoint).concat("/").concat(Constants.CONFIG));
        getProject().mkdir(entryPointDir.concat("/").concat(Constants.MAIN_RESOURCES));

        logger.info("Generated Childs Dirs");

        logger.info("Generating Base Files");
        getProject().file(entryPointDir.concat("/").concat(Constants.BUILD_GRADLE)).createNewFile();
        getProject().file(entryPointDir.concat("/").concat(Constants.MAIN_RESOURCES).concat("/").concat(Constants.APPLICATION_PROPERTIES)).createNewFile();
        getProject().file(entryPointDir.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(entryPoint).concat("/").concat(Constants.CONFIG).concat("/").concat(Constants.AUTHORIZATION_CONFIG).concat(Constants.JAVA_EXTENSION));
        logger.info("Generated Base Files");

        logger.info("Writing in Files");
        Utils.writeString(getProject(),entryPointDir.concat("/").concat(Constants.BUILD_GRADLE), Constants.getBuildGradleApiRest());
        Utils.writeString(getProject(), entryPointDir.concat("/").concat(Constants.MAIN_RESOURCES).concat("/").concat(Constants.APPLICATION_PROPERTIES),"");
        Utils.writeString(getProject(),entryPointDir.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(entryPoint).concat("/").concat(Constants.CONFIG).concat("/").concat(Constants.AUTHORIZATION_CONFIG).concat(Constants.JAVA_EXTENSION), Constants.getAuthorizationConfigApiRest(packageName));
        logger.info("Writed in Files");
    }
}
