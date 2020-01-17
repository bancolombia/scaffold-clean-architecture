package co.com.bancolombia.task;

import co.com.bancolombia.Constants;
import co.com.bancolombia.Utils;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.DrivenAdapterFactoryImpl;
import co.com.bancolombia.models.DrivenAdapter;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import java.io.IOException;
import java.util.stream.Collectors;

public class GenerateDrivenAdapterTask extends DefaultTask {
    private int numberDrivenAdapter = -1;
    private Logger logger = getProject().getLogger();
    private ModuleFactory drivenAdapterFactory = new DrivenAdapterFactoryImpl();
    private DrivenAdapter drivenAdapter;

    @Option(option = "value", description = "Set the number of the driven adapter  (1 -> JPA Repository, 2 -> Mongo Repository, 3 -> Secrets Manager Consumer )")
    public void setDrivenAdapter(String number) {
        this.numberDrivenAdapter = Utils.tryParse(number);
    }

    @TaskAction
    public void generateDrivenAdapterTask() throws IOException {
        logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
        throwDrivenAdapterTask();

        drivenAdapter = drivenAdapterFactory.makeDrivenAdapter(numberDrivenAdapter);

        logger.lifecycle("Project  Package: {}", drivenAdapter.getPackageName());
        logger.lifecycle("Driven Adapter: {} - {}", drivenAdapter.getNumberDrivenAdapter(), drivenAdapter.getNameDrivenAdapter());

        generateDrivenAdapter();
    }

    private void throwDrivenAdapterTask() {
        if (numberDrivenAdapter < 0) {
            throw new IllegalArgumentException("No Driven Adapter is set, usege: gradle generateDrivenAdapter --value numberDrivenAdapter");
        }
    }

    private void generateDrivenAdapter() throws IOException {
        logger.info(Constants.GENERATING_CHILDS_DIRS);

        generateDirs();

        logger.lifecycle(Constants.GENERATED_CHILDS_DIRS);
        logger.lifecycle(Constants.WRITING_IN_FILES);

        writedFiles();
        rewriteSettingsGradle();

        logger.lifecycle(Constants.WRITED_IN_FILES);
    }

    private void generateDirs() {
        getProject().mkdir(drivenAdapter.getDrivenAdapterDir().concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(drivenAdapter.getPackageName()).concat("/").concat(drivenAdapter.getDrivenAdapterPackage()));
        getProject().mkdir(drivenAdapter.getDrivenAdapterDir().concat("/").concat(Constants.TEST_JAVA).concat("/").concat(drivenAdapter.getPackageName()).concat("/").concat(drivenAdapter.getDrivenAdapterPackage()));

        if (drivenAdapter.helperDrivenAdapterExist()) {
            getProject().mkdir(drivenAdapter.getHelperDir().concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(drivenAdapter.getPackageName()).concat("/").concat(drivenAdapter.getHelperDrivenAdapterPackage()));
            getProject().mkdir(drivenAdapter.getHelperDir().concat("/").concat(Constants.TEST_JAVA).concat("/").concat(drivenAdapter.getPackageName()).concat("/").concat(drivenAdapter.getHelperDrivenAdapterPackage()));
        }

        if (drivenAdapter.modelDirExist()) {
            getProject().mkdir(drivenAdapter.getModelDir().concat("/").concat(Constants.COMMON).concat("/").concat(Constants.GATEWAYS));
        }
    }

    private void writedFiles() throws IOException {
        Utils.writeString(getProject(), drivenAdapter.getDrivenAdapterDir().concat("/").concat(Constants.BUILD_GRADLE), drivenAdapter.getBuildGradleContentDrivenAdapter());
        Utils.writeString(getProject(), drivenAdapter.getDrivenAdapterDirSrc().concat("/").concat(drivenAdapter.getClassNameEntryPoint()).concat(Constants.JAVA_EXTENSION), drivenAdapter.getDrivenAdapterClassContent());

        if (drivenAdapter.getInterfaceNameEntryPoint() != null) {
            Utils.writeString(getProject(), drivenAdapter.getDrivenAdapterDirSrc().concat("/").concat(drivenAdapter.getInterfaceNameEntryPoint()).concat(Constants.JAVA_EXTENSION), drivenAdapter.getDrivenAdapterInterfaceContent());
        }

        if (drivenAdapter.helperDrivenAdapterExist()) {
            Utils.writeString(getProject(), drivenAdapter.getHelperDir().concat("/").concat(Constants.BUILD_GRADLE), drivenAdapter.getBuildGradleDrivenAdapter());
            Utils.writeString(getProject(), drivenAdapter.getHelperDir().concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(drivenAdapter.getPackageName()).concat("/").concat(drivenAdapter.getHelperDrivenAdapterPackage()).concat("/").concat(Constants.JPA_HELPER_CLASS).concat(Constants.JAVA_EXTENSION), drivenAdapter.getHelperDrivenAdapterClassContent());
        }

        if (drivenAdapter.modelDirExist()) {
            Utils.writeString(getProject(), drivenAdapter.getModelDir().concat("/").concat(Constants.COMMON).concat("/").concat(Constants.GATEWAYS).concat("/").concat(Constants.SECRET_MANAGER_CONSUMER_CLASS).concat(Constants.JAVA_EXTENSION), Constants.getSecretsManagerInterfaceContent(drivenAdapter.getPackageName().concat(".").concat(Constants.COMMON).concat(".").concat(Constants.GATEWAYS)));
        }
    }

    private void rewriteSettingsGradle() throws IOException {
        String settings = Utils.readFile(getProject(), Constants.SETTINGS_GRADLE).collect(Collectors.joining("\n"));
        settings += drivenAdapter.getSettingsGradleDrivenAdapter();
        Utils.writeString(getProject(), Constants.SETTINGS_GRADLE, settings);
    }
}
