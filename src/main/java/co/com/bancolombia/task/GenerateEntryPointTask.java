package co.com.bancolombia.task;

import co.com.bancolombia.Constants;
import co.com.bancolombia.Utils;
import co.com.bancolombia.exceptions.CleanException;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import java.io.IOException;
import java.util.stream.Collectors;

public class GenerateEntryPointTask extends DefaultTask {
    private int numberEntryPoint = -1;
    private Logger logger = getProject().getLogger();
    private static String entryPoints =  "(1 -> API REST)";

    @Option(option = "value", description = "Set the number of the entry point (1 -> API REST)")
    public void setEntryPoint(String number) { this.numberEntryPoint = Utils.tryParse(number); }

    @TaskAction
    public void generateEntryPoint() throws IOException, CleanException {
        String packageName;
        String nameEntryPoint;
        if (numberEntryPoint < 0) {
            throw new IllegalArgumentException("No Entry Point is set, usege: gradle generateEntryPoint --value numberEntryPoint");
        }

        nameEntryPoint = Constants.getNameEntryPoint(numberEntryPoint);
        if (nameEntryPoint == null) {
            throw new IllegalArgumentException("Entry Point not is available ".concat(entryPoints));
        }
        packageName = Utils.readProperties("package");

        logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
        logger.lifecycle("Project  Package: {}", packageName);
        packageName = packageName.replaceAll("\\.", "\\/");
        logger.lifecycle("Entry Point: {} - {}", numberEntryPoint, nameEntryPoint);
        String entryPoint;
        String entryPointPackage;
        switch (numberEntryPoint){
            case 1:
                entryPoint = "api-rest";
                entryPointPackage = "api";
                break;
            case 2:
                entryPoint = "reactive-web";
                entryPointPackage = "api";
                break;
            default:
                throw new CleanException("The Entry Point is disabled");
        }
        generateEntryPoint(packageName, numberEntryPoint, entryPoint, entryPointPackage);

    }

    private void generateEntryPoint(String packageName, int numberEntryPoint, String entryPoint, String entryPointPackage) throws IOException {
        logger.lifecycle("Generating Childs Dirs");
        String entryPointDir = Constants.INFRASTRUCTURE.concat("/").concat(Constants.ENTRY_POINTS).concat("/").concat(entryPoint);
        getProject().mkdir(entryPointDir.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(entryPointPackage));
        getProject().mkdir(entryPointDir.concat("/").concat(Constants.TEST_JAVA).concat("/").concat(packageName).concat("/").concat(entryPointPackage));

        logger.lifecycle("Generated Childs Dirs");

        logger.lifecycle("Writing in Files");
        Utils.writeString(getProject(),entryPointDir.concat("/").concat(Constants.BUILD_GRADLE), getBuildGradleEntryPointContent(numberEntryPoint));
        Utils.writeString(getProject(),entryPointDir.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(entryPointPackage).concat("/").concat(Constants.API_REST_CLASS).concat(Constants.JAVA_EXTENSION), getContentClassEntryPoint(numberEntryPoint, packageName.concat(".").concat(entryPointPackage)));

        String settings = Utils.readFile(getProject(),Constants.SETTINGS_GRADLE).collect(Collectors.joining("\n"));
        settings += getSettingsGradleEntryPoint(numberEntryPoint);
        Utils.writeString(getProject(),Constants.SETTINGS_GRADLE, settings);
        logger.lifecycle("Writed in Files");
    }

    private String getBuildGradleEntryPointContent(int numberEntryPoint){
        switch (numberEntryPoint){
            case 1:
                return Constants.getBuildGradleApiRest();
            case 2:
                return Constants.getBuildGradleReactiveWeb();
            default:
                return null;
        }
    }

    private String getContentClassEntryPoint(int numberEntryPoint, String packageName){
        switch (numberEntryPoint){
            case 1:
                return Constants.getApiRestClassContent(packageName);
            case 2:
                return Constants.getReactiveWebClassContent(packageName);
            default:
                return null;
        }
    }

    private String getSettingsGradleEntryPoint(int numberEntryPoint){
        switch (numberEntryPoint){
            case 1:
                return Constants.getSettingsApiRestContent();
            case 2:
                return Constants.getSettingsReactiveWebContent();
            default:
                return null;
        }
    }
}
