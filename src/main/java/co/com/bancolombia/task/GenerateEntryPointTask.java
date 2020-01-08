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
    public void setEntryPoint(String number) {
        if (!number.isEmpty()) {
            this.numberEntryPoint = Utils.tryParse(number);
        }
    }

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


        switch (numberEntryPoint){
            case 1:
                generateApiRest(packageName);
                break;
            default:
                throw new CleanException("Entry point not is available ".concat(entryPoints));
        }
    }

    private void generateApiRest(String packageName) throws IOException {
        logger.lifecycle("Generating Childs Dirs");
        String entryPoint = "api-rest";
        String entryPointDir = Constants.INFRAESTUCTURE.concat("/").concat(Constants.ENTRY_POINTS).concat("/").concat(entryPoint);
        getProject().mkdir(entryPointDir.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(entryPoint));

        logger.lifecycle("Generated Childs Dirs");

        logger.lifecycle("Generating Base Files");
        getProject().file(entryPointDir.concat("/").concat(Constants.BUILD_GRADLE)).createNewFile();
        getProject().file(entryPointDir.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(entryPoint).concat("/").concat(Constants.API_REST_CLASS).concat(Constants.JAVA_EXTENSION)).createNewFile();
        logger.lifecycle("Generated Base Files");

        logger.lifecycle("Writing in Files");
        Utils.writeString(getProject(),entryPointDir.concat("/").concat(Constants.BUILD_GRADLE), Constants.getBuildGradleApiRest());
        Utils.writeString(getProject(),entryPointDir.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(entryPoint).concat("/").concat(Constants.API_REST_CLASS).concat(Constants.JAVA_EXTENSION), Constants.getApiRestClassContent(packageName));

        String settings = Utils.readFile(getProject(),Constants.SETTINGS_GRADLE).collect(Collectors.joining("\n"));
        settings += Constants.getSettingsApiRestContent();
        Utils.writeString(getProject(),Constants.SETTINGS_GRADLE, settings);
        logger.lifecycle("Writed in Files");
    }
}
