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

public class GenerateDrivenAdapterTask extends DefaultTask {
    private int numberDrivenAdapter = -1;
    private Logger logger = getProject().getLogger();
    private static String drivenAdapters =  "(1 -> JPA Repository, 2 -> Mongo Repository, 3 -> Secrets Manager Consumer )";
    private static String gChildDirs = "Generating Childs Dirs";
    private static String generatedChildDirs = "Generated Childs Dirs";
    private static String gBaseFiles = "Generating Base Files";
    private static String generatedBaseFiles = "Generated Base Files";
    private static String wFiles = "Writing in Files";
    private static String writedFiles = "Writed in Files";

    @Option(option = "value", description = "Set the number of the driven adapter  (1 -> JPA Repository, 2 -> Mongo Repository, 3 -> Secrets Manager Consumer )")
    public void setDrivenAdapter(String number) { this.numberDrivenAdapter = Utils.tryParse(number); }

    @TaskAction
    public void generateDrivenAdapter() throws IOException, CleanException {
        String packageName;
        String nameDrivenAdapter;
        if (numberDrivenAdapter < 0) {
            throw new IllegalArgumentException("No Driven Adapter is set, usege: gradle generateDrivenAdapter --value numberDrivenAdapter");
        }

        nameDrivenAdapter = Constants.getNameDrivenAdapter(numberDrivenAdapter);
        if (nameDrivenAdapter == null) {
            throw new IllegalArgumentException("Entry Point not is available ".concat(drivenAdapters));
        }
        packageName = Utils.readProperties("package");

        logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
        logger.lifecycle("Project  Package: {}", packageName);
        packageName = packageName.replaceAll("\\.", "\\/");
        logger.lifecycle("Driven Adapter: {} - {}", numberDrivenAdapter, nameDrivenAdapter);


        switch (numberDrivenAdapter){
            case 1:
                generateJPARepository(packageName);
                break;
            case 2:
                generateMongoRepository(packageName);
                break;
            case 3:
                generateSecretsManager(packageName);
                break;
            default:
                throw new CleanException("Driven Adapter not is available ".concat(drivenAdapters));
        }
    }
    private void generateJPARepository(String packageName) throws IOException {
        logger.info(gChildDirs);
        String drivenAdapter = "jpa-repository";
        String helperDrivenAdapter = "jpa-repository-commons";
        String drivenAdapterDir = Constants.INFRASTRUCTURE.concat("/").concat(Constants.DRIVEN_ADAPTERS).concat("/").concat(drivenAdapter);
        String helperDir = Constants.INFRASTRUCTURE.concat("/").concat(Constants.HELPERS).concat("/").concat(helperDrivenAdapter);
        getProject().mkdir(drivenAdapterDir.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(drivenAdapter));

        getProject().mkdir(helperDir.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(helperDrivenAdapter));

        logger.lifecycle(generatedChildDirs);

        logger.lifecycle(gBaseFiles);
        getProject().file(drivenAdapterDir.concat("/").concat(Constants.BUILD_GRADLE)).createNewFile();
        getProject().file(drivenAdapterDir.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(drivenAdapter).concat("/").concat(Constants.JPA_REPOSITORY_CLASS).concat(Constants.JAVA_EXTENSION));
        getProject().file(drivenAdapterDir.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(drivenAdapter).concat("/").concat(Constants.JPA_REPOSITORY_INTERFACE).concat(Constants.JAVA_EXTENSION));

        getProject().file(helperDir.concat("/").concat(Constants.BUILD_GRADLE)).createNewFile();
        getProject().file(helperDir.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(helperDrivenAdapter).concat("/").concat(Constants.JPA_HELPER_CLASS).concat(Constants.JAVA_EXTENSION));

        logger.lifecycle(generatedBaseFiles);

        logger.lifecycle(wFiles);
        Utils.writeString(getProject(),drivenAdapterDir.concat("/").concat(Constants.BUILD_GRADLE), Constants.getBuildGradleJPARepository());
        Utils.writeString(getProject(),drivenAdapterDir.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(drivenAdapter).concat("/").concat(Constants.JPA_REPOSITORY_CLASS).concat(Constants.JAVA_EXTENSION), Constants.getJPARepositoryClassContent(packageName));
        Utils.writeString(getProject(),drivenAdapterDir.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(drivenAdapter).concat("/").concat(Constants.JPA_REPOSITORY_INTERFACE).concat(Constants.JAVA_EXTENSION), Constants.getJPARepositoryInterfaceContent(packageName));

        Utils.writeString(getProject(),helperDir.concat("/").concat(Constants.BUILD_GRADLE), Constants.getBuildGradleHelperJPARepository());
        Utils.writeString(getProject(),helperDir.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(helperDrivenAdapter).concat("/").concat(Constants.JPA_HELPER_CLASS).concat(Constants.JAVA_EXTENSION), Constants.getHelperJPARepositoryClassContent(packageName));

        String settings = Utils.readFile(getProject(),Constants.SETTINGS_GRADLE).collect(Collectors.joining("\n"));
        settings += Constants.getSettingsJPARepositoryContent();
        settings += Constants.getSettingsHelperJPAContent();
        Utils.writeString(getProject(),Constants.SETTINGS_GRADLE, settings);
        logger.lifecycle(writedFiles);
    }

    private void generateMongoRepository(String packageName) throws IOException {
        logger.lifecycle(gChildDirs);
        String drivenAdapter = "mongo-repository";
        String helperDrivenAdapter = "mongo-repository-commons";
        String drivenAdapterDir = Constants.INFRASTRUCTURE.concat("/").concat(Constants.DRIVEN_ADAPTERS).concat("/").concat(drivenAdapter);
        String helperDir = Constants.INFRASTRUCTURE.concat("/").concat(Constants.HELPERS).concat("/").concat(helperDrivenAdapter);
        getProject().mkdir(drivenAdapterDir.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(drivenAdapter));

        getProject().mkdir(helperDir.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(helperDrivenAdapter));

        logger.lifecycle(generatedChildDirs);

        logger.lifecycle(gBaseFiles);
        getProject().file(drivenAdapterDir.concat("/").concat(Constants.BUILD_GRADLE)).createNewFile();
        getProject().file(drivenAdapterDir.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(drivenAdapter).concat("/").concat(Constants.MONGO_REPOSITORY_CLASS).concat(Constants.JAVA_EXTENSION));
        getProject().file(drivenAdapterDir.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(drivenAdapter).concat("/").concat(Constants.MONGO_REPOSITORY_INTERFACE).concat(Constants.JAVA_EXTENSION));

        getProject().file(helperDir.concat("/").concat(Constants.BUILD_GRADLE)).createNewFile();
        getProject().file(helperDir.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(helperDrivenAdapter).concat("/").concat(Constants.MONGO_HELPER_CLASS).concat(Constants.JAVA_EXTENSION));

        logger.lifecycle(generatedBaseFiles);

        logger.lifecycle(wFiles);
        Utils.writeString(getProject(),drivenAdapterDir.concat("/").concat(Constants.BUILD_GRADLE), Constants.getBuildGradleMongoRepository());
        Utils.writeString(getProject(),drivenAdapterDir.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(drivenAdapter).concat("/").concat(Constants.MONGO_REPOSITORY_CLASS).concat(Constants.JAVA_EXTENSION), Constants.getMongoRepositoryClassContent(packageName));
        Utils.writeString(getProject(),drivenAdapterDir.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(drivenAdapter).concat("/").concat(Constants.MONGO_REPOSITORY_INTERFACE).concat(Constants.JAVA_EXTENSION), Constants.getMongoRepositoryInterfaceContent(packageName));

        Utils.writeString(getProject(),helperDir.concat("/").concat(Constants.BUILD_GRADLE), Constants.getBuildGradleHelperMongoRepository());
        Utils.writeString(getProject(),helperDir.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(helperDrivenAdapter).concat("/").concat(Constants.JPA_HELPER_CLASS).concat(Constants.JAVA_EXTENSION), Constants.getHelperMongoRepositoryClassContent(packageName));

        String settings = Utils.readFile(getProject(),Constants.SETTINGS_GRADLE).collect(Collectors.joining("\n"));
        settings += Constants.getSettingsMongoRepositoryContent();
        settings += Constants.getSettingsHelperMongoContent();
        Utils.writeString(getProject(),Constants.SETTINGS_GRADLE, settings);
        logger.lifecycle( writedFiles);
    }

    private void generateSecretsManager(String packageName) throws IOException {
        logger.lifecycle(gChildDirs);
        String drivenAdapter = "secrets-manager-consumer";
        String drivenAdapterDir = Constants.INFRASTRUCTURE.concat("/").concat(Constants.DRIVEN_ADAPTERS).concat("/").concat(drivenAdapter);
        getProject().mkdir(drivenAdapterDir.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(drivenAdapter));

        logger.lifecycle(generatedChildDirs);

        logger.lifecycle(gBaseFiles);
        getProject().file(drivenAdapterDir.concat("/").concat(Constants.BUILD_GRADLE)).createNewFile();
        getProject().file(drivenAdapterDir.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(drivenAdapter).concat("/").concat(Constants.SECRET_MANAGER_CLASS).concat(Constants.JAVA_EXTENSION));
        logger.lifecycle(generatedBaseFiles);

        logger.lifecycle(wFiles);
        Utils.writeString(getProject(),drivenAdapterDir.concat("/").concat(Constants.BUILD_GRADLE), Constants.getBuildGradleSecretsManager());
        Utils.writeString(getProject(),drivenAdapterDir.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(packageName).concat("/").concat(drivenAdapter).concat("/").concat(Constants.SECRET_MANAGER_CLASS).concat(Constants.JAVA_EXTENSION), Constants.getSecretsManagerClassContent(packageName));

        String settings = Utils.readFile(getProject(),Constants.SETTINGS_GRADLE).collect(Collectors.joining("\n"));
        settings += Constants.getSettingsGradleSecretsManagerContent();
        Utils.writeString(getProject(),Constants.SETTINGS_GRADLE, settings);
        logger.lifecycle(writedFiles);
    }
}
