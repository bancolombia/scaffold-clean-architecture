package co.com.bancolombia.task;

import co.com.bancolombia.templates.Constants;
import co.com.bancolombia.Utils;
import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.DrivenAdapterFactoryImpl;
import co.com.bancolombia.models.AbstractModule;
import co.com.bancolombia.templates.DrivenAdapterTemplate;
import co.com.bancolombia.templates.HelperTemplate;
import co.com.bancolombia.templates.PluginTemplate;
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
    private AbstractModule drivenAdapter;

    @Option(option = "value", description = "Set the number of the driven adapter  (1 -> JPA Repository, 2 -> Mongo Repository, 3 -> Secrets Manager Consumer, 4 -> Async Event Bus )")
    public void setDrivenAdapter(String number) {
        this.numberDrivenAdapter = Utils.tryParse(number);
    }

    @TaskAction
    public void generateDrivenAdapterTask() throws IOException, CleanException {
        logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
        throwDrivenAdapterTask();

        drivenAdapter = drivenAdapterFactory.makeDrivenAdapter(numberDrivenAdapter);

        logger.lifecycle("Project  Package: {}", drivenAdapter.getPackageName());
        logger.lifecycle("Driven Adapter: {} - {}", drivenAdapter.getCode(), drivenAdapter.getName());

        generateDrivenAdapter();
    }

    private void throwDrivenAdapterTask() {
        if (numberDrivenAdapter < 0) {
            throw new IllegalArgumentException("No Driven Adapter is set, usege: gradle generateDrivenAdapter --value numberDrivenAdapter");
        }
    }

    private void generateDrivenAdapter() throws IOException {
        logger.info(PluginTemplate.GENERATING_CHILDS_DIRS);

        generateDirs();

        logger.lifecycle(PluginTemplate.GENERATED_CHILDS_DIRS);
        logger.lifecycle(PluginTemplate.GENERATING_FILES);

        writedFiles();
        rewriteSettingsGradle();

        logger.lifecycle(PluginTemplate.WRITED_IN_FILES);
    }

    private void generateDirs() {
        getProject().mkdir(drivenAdapter.getModuleDir().concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(drivenAdapter.getPackageName()).concat("/").concat(drivenAdapter.getModulePackage()));
        getProject().mkdir(drivenAdapter.getModuleDir().concat("/").concat(Constants.TEST_JAVA).concat("/").concat(drivenAdapter.getPackageName()).concat("/").concat(drivenAdapter.getModulePackage()));

        if (drivenAdapter.helperModuleExist()) {
            getProject().mkdir(drivenAdapter.getHelperDir().concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(drivenAdapter.getPackageName()).concat("/").concat(drivenAdapter.getHelperPackage()));
            getProject().mkdir(drivenAdapter.getHelperDir().concat("/").concat(Constants.TEST_JAVA).concat("/").concat(drivenAdapter.getPackageName()).concat("/").concat(drivenAdapter.getHelperPackage()));
        }

        if (drivenAdapter.modelDirExist()) {
            getProject().mkdir(drivenAdapter.getModelDir().concat("/").concat(DrivenAdapterTemplate.COMMON).concat("/").concat(Constants.GATEWAYS));
        }
    }

    private void writedFiles() throws IOException {
        Utils.writeString(getProject(), drivenAdapter.getModuleDir().concat("/").concat(Constants.BUILD_GRADLE), drivenAdapter.getBuildGradleContentModule());
        Utils.writeString(getProject(), drivenAdapter.getModuleDirSrc().concat("/").concat(drivenAdapter.getClassNameModule()).concat(Constants.JAVA_EXTENSION), drivenAdapter.getModuleClassContent());

        if (drivenAdapter.getInterfaceNameModule() != null) {
            Utils.writeString(getProject(), drivenAdapter.getModuleDirSrc().concat("/").concat(drivenAdapter.getInterfaceNameModule()).concat(Constants.JAVA_EXTENSION), drivenAdapter.getModuleInterfaceContent());
        }

        if (drivenAdapter.helperModuleExist()) {
            Utils.writeString(getProject(), drivenAdapter.getHelperDir().concat("/").concat(Constants.BUILD_GRADLE), drivenAdapter.getBuildGradleModule());
            Utils.writeString(getProject(), drivenAdapter.getHelperDir().concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(drivenAdapter.getPackageName()).concat("/").concat(drivenAdapter.getHelperPackage()).concat("/").concat(HelperTemplate.JPA_HELPER_CLASS).concat(Constants.JAVA_EXTENSION), drivenAdapter.getHelperModuleClassContent());
        }

        if (drivenAdapter.modelDirExist()) {
            Utils.writeString(getProject(), drivenAdapter.getModelDir().concat("/").concat(DrivenAdapterTemplate.COMMON).concat("/").concat(Constants.GATEWAYS).concat("/").concat(drivenAdapter.getModelName()).concat(Constants.JAVA_EXTENSION),drivenAdapter.getInterfaceModule());
        }
    }

    private void rewriteSettingsGradle() throws IOException {
        String settings = Utils.readFile(getProject(), Constants.SETTINGS_GRADLE).collect(Collectors.joining("\n"));
        settings += drivenAdapter.getSettingsGradleModule();
        Utils.writeString(getProject(), Constants.SETTINGS_GRADLE, settings);
    }
}
