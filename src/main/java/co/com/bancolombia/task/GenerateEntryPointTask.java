package co.com.bancolombia.task;

import co.com.bancolombia.Constants;
import co.com.bancolombia.Utils;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import java.io.IOException;
import java.util.stream.Collectors;

public class GenerateEntryPointTask extends DefaultTask {

    private String entryPoints =  "(1 -> API REST, 2 -> API REACTIVE)";
    private int numberEntryPoint = -1;
    private Logger logger = getProject().getLogger();
    private String packageName;
    private String nameEntryPoint;
    private String entryPoint = null;
    private String entryPointPackage = null;

    @Option(option = "value", description = "Set the number of the entry point (1 -> API REST, 2 -> API REACTIVE)")
    public void setEntryPoint(String number) { this.numberEntryPoint = Utils.tryParse(number); }

    @TaskAction
    public void generateEntryPointTask() throws IOException {

        throwEntryPointTask();

        logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
        logger.lifecycle("Project  Package: {}", packageName);
        packageName = packageName.replaceAll("\\.", "\\/");
        logger.lifecycle("Entry Point: {} - {}", numberEntryPoint, nameEntryPoint);

        setEntryPointPackage();
        generateEntryPoint();
    }

    private void throwEntryPointTask() throws IOException {
        if (numberEntryPoint < 0) {
            throw new IllegalArgumentException("No Entry Point is set, usege: gradle generateEntryPoint --value numberEntryPoint");
        }

        nameEntryPoint = Constants.getNameEntryPoint(numberEntryPoint);
        if (nameEntryPoint == null) {
            throw new IllegalArgumentException("Entry Point not is available ".concat(entryPoints));
        }
        packageName = Utils.readProperties("package");
    }

    private void setEntryPointPackage(){
        if (numberEntryPoint == 1){
            entryPoint = "api-rest";
            entryPointPackage = "api";
        }else if (numberEntryPoint == 2){
            entryPoint = "reactive-web";
            entryPointPackage = "api";
        }
    }

    private void generateEntryPoint() throws IOException {
        logger.lifecycle("Generating Childs Dirs");
        String entryPointDir = Constants.INFRASTRUCTURE.concat("/").concat(Constants.ENTRY_POINTS).concat("/").concat(entryPoint);
        getProject().mkdir(entryPointDir.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(entryPointPackage));
        getProject().mkdir(entryPointDir.concat("/").concat(Constants.TEST_JAVA).concat("/").concat(packageName).concat("/").concat(entryPointPackage));

        logger.lifecycle("Generated Childs Dirs");

        logger.lifecycle("Writing in Files");
        Utils.writeString(getProject(),entryPointDir.concat("/").concat(Constants.BUILD_GRADLE), getBuildGradleEntryPointContent());
        Utils.writeString(getProject(),entryPointDir.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(entryPointPackage).concat("/").concat(Constants.API_REST_CLASS).concat(Constants.JAVA_EXTENSION), getContentClassEntryPoint());

        rewriteSettingsGradle();
        logger.lifecycle("Writed in Files");
    }

    private String getBuildGradleEntryPointContent(){
        String value = null;
        if (numberEntryPoint == 1) value = Constants.getBuildGradleApiRest();
        else if (numberEntryPoint == 2) value = Constants.getBuildGradleReactiveWeb();
        return value;
    }

    private String getContentClassEntryPoint(){
        String value = null;
        if (numberEntryPoint == 1) value = Constants.getApiRestClassContent(packageName.concat(".").concat(entryPointPackage));
        else if (numberEntryPoint == 2) value = Constants.getReactiveWebClassContent(packageName.concat(".").concat(entryPointPackage));
        return value;
    }

    private String getSettingsGradleEntryPoint(){
        String value = null;
        if (numberEntryPoint == 1) value = Constants.getSettingsApiRestContent();
        else if (numberEntryPoint == 2) value = Constants.getSettingsReactiveWebContent();
        return value;
    }

    private void rewriteSettingsGradle() throws IOException {
        String settings = Utils.readFile(getProject(),Constants.SETTINGS_GRADLE).collect(Collectors.joining("\n"));
        settings += getSettingsGradleEntryPoint();
        Utils.writeString(getProject(),Constants.SETTINGS_GRADLE, settings);
    }
}
