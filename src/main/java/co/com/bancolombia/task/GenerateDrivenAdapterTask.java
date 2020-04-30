package co.com.bancolombia.task;

import co.com.bancolombia.templates.*;
import co.com.bancolombia.Utils;
import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.adapters.ModuleFactory;
import co.com.bancolombia.factory.adapters.DrivenAdapterFactory;
import co.com.bancolombia.models.adapters.AbstractModule;
import co.com.bancolombia.templates.config.JpaConfigTemplate;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import java.io.IOException;
import java.util.stream.Collectors;

public class GenerateDrivenAdapterTask extends DefaultTask {
    private int numberDrivenAdapter = -1;
    private Logger logger = getProject().getLogger();
    private ModuleFactory drivenAdapterFactory = new DrivenAdapterFactory();
    private AbstractModule drivenAdapter;

    @Option(option = "value", description = "Set the number of the driven adapter  (1 -> JPA Repository, 2 -> Mongo Repository, 3 -> Async Event Bus )")
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
            throw new IllegalArgumentException("No Driven Adapter is set, usage: gradle generateDrivenAdapter --value numberDrivenAdapter");
        }
    }

    private void generateDrivenAdapter() throws IOException {
        logger.info(PluginTemplate.GENERATING_CHILDS_DIRS);

        generateDirs();

        logger.lifecycle(PluginTemplate.GENERATED_CHILDS_DIRS);
        logger.lifecycle(PluginTemplate.GENERATING_FILES);

        writedFiles();
        rewriteSettingsGradle();
        rewriteAppServiceBuildGradle();
        logger.lifecycle(PluginTemplate.WRITED_IN_FILES);
    }

    private void generateDirs() {
        getProject().mkdir(drivenAdapter.getModuleDir().concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(drivenAdapter.getPackageName()).concat("/").concat(drivenAdapter.getModulePackage()));
        getProject().mkdir(drivenAdapter.getModuleDir().concat("/").concat(Constants.TEST_JAVA).concat("/").concat(drivenAdapter.getPackageName()).concat("/").concat(drivenAdapter.getModulePackage()));

        if (drivenAdapter.hasHelperModule()) {
            getProject().mkdir(drivenAdapter.getHelperDir().concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(drivenAdapter.getPackageName()).concat("/").concat(drivenAdapter.getHelperPackage()));
            getProject().mkdir(drivenAdapter.getHelperDir().concat("/").concat(Constants.TEST_JAVA).concat("/").concat(drivenAdapter.getPackageName()).concat("/").concat(drivenAdapter.getHelperPackage()));
        }

        if (drivenAdapter.hasModelDir()) {
            getProject().mkdir(drivenAdapter.getModelDir().concat("/").concat(Constants.GATEWAYS));
        }

        if (drivenAdapter.hasGateway()) {
            getProject().mkdir(drivenAdapter.getGatewayDir());
        }

        if(drivenAdapter.hasConfigFile()) {
            getProject().mkdir(Constants.APPLICATION.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(drivenAdapter.getPackageName()).concat("/").concat(Constants.CONFIG).concat("/").concat(drivenAdapter.getModulePackage()));
        }
    }

    private void writedFiles() throws IOException {
        Utils.writeString(getProject(), drivenAdapter.getModuleDir().concat("/").concat(Constants.BUILD_GRADLE), drivenAdapter.getBuildGradleContentModule());
        Utils.writeString(getProject(), drivenAdapter.getModuleDirSrc().concat("/").concat(drivenAdapter.getClassNameModule()).concat(Constants.JAVA_EXTENSION), drivenAdapter.getModuleClassContent());

        if (drivenAdapter.getInterfaceNameModule() != null) {
            Utils.writeString(getProject(), drivenAdapter.getModuleDirSrc().concat("/").concat(drivenAdapter.getInterfaceNameModule()).concat(Constants.JAVA_EXTENSION), drivenAdapter.getModuleInterfaceContent());
        }

        if (drivenAdapter.hasHelperModule()) {
            Utils.writeString(getProject(), drivenAdapter.getHelperDir().concat("/").concat(Constants.BUILD_GRADLE), drivenAdapter.getBuildGradleModule());
            Utils.writeString(getProject(), drivenAdapter.getHelperDir().concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(drivenAdapter.getPackageName()).concat("/").concat(drivenAdapter.getHelperPackage()).concat("/").concat(HelperTemplate.JPA_HELPER_CLASS).concat(Constants.JAVA_EXTENSION), drivenAdapter.getHelperModuleClassContent());
        }

        if (drivenAdapter.hasModelDir()) {
            Utils.writeString(getProject(), drivenAdapter.getModelDir().concat("/").concat(drivenAdapter.getModelName()).concat(Constants.JAVA_EXTENSION), ModelTemplate.getModel(drivenAdapter.getModelName(), drivenAdapter.getPackageName()));
        }

        if (drivenAdapter.hasGateway()){
            Utils.writeString(getProject(), drivenAdapter.getGatewayDir().concat("/").concat(drivenAdapter.getGatewayName()).concat("Repository").concat(Constants.JAVA_EXTENSION), ModelTemplate.getInterfaceModel(drivenAdapter.getGatewayName(), drivenAdapter.getPackageName()));

        }

        if(drivenAdapter.hasConfigFile()) {
            Utils.writeString(getProject(), Constants.APPLICATION.concat("/").concat(Constants.MAIN_JAVA).concat("/").concat(drivenAdapter.getPackageName()).concat("/").concat(Constants.CONFIG).concat("/").concat(drivenAdapter.getModulePackage()).concat("/").concat(drivenAdapter.getConfigFileName().concat(Constants.JAVA_EXTENSION)), JpaConfigTemplate.getJpaConfigFileContent(drivenAdapter.getPackageName()));
        }

        if(drivenAdapter.hasPropertiesFile()) {
            Utils.writeString(getProject(), Constants.APPLICATION.concat("/").concat(Constants.MAIN_RESOURCES).concat("/").concat("application-").concat(drivenAdapter.getPropertiesFileName().concat(".yaml")), drivenAdapter.getPropertiesFileContent());
            rewriteApplicationProperties(drivenAdapter.getPropertiesFileName());
        }
    }


    private void rewriteSettingsGradle() throws IOException {
        String settings = Utils.readFile(getProject(), Constants.SETTINGS_GRADLE).collect(Collectors.joining("\n"));
        settings += drivenAdapter.getSettingsGradleModule();
        Utils.writeString(getProject(), Constants.SETTINGS_GRADLE, settings);
    }

    private void rewriteAppServiceBuildGradle() throws IOException {
        String buildGradlePath = Constants.APPLICATION.concat("/").concat(Constants.BUILD_GRADLE);
        String dependencies = Utils.readFile(getProject(), buildGradlePath).collect(Collectors.joining("\n"));
        dependencies = dependencies.replace("dependencies {", drivenAdapter.getAppServiceImports());
        Utils.writeString(getProject(), buildGradlePath, dependencies);
    }

    private void rewriteApplicationProperties(String propertieFileToInclude) throws IOException {
        String propertiesPath = Constants.APPLICATION.concat("/").concat(Constants.MAIN_RESOURCES).concat("/").concat(ScaffoldTemplate.APPLICATION_PROPERTIES);
        String propertiesContent = Utils.readFile(getProject(), propertiesPath).collect(Collectors.joining("\n"));
        propertiesContent += "\n      - "+propertieFileToInclude +"\n";
        Utils.writeString(getProject(), propertiesPath, propertiesContent);
    }
}
