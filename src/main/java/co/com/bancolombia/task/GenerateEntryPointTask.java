package co.com.bancolombia.task;

import co.com.bancolombia.templates.Constants;
import co.com.bancolombia.Utils;
import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.EntryPointFactoryImpl;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.models.Module;
import co.com.bancolombia.templates.PluginTemplate;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import java.io.IOException;
import java.util.stream.Collectors;

public class GenerateEntryPointTask extends DefaultTask {

    private int codeEntryPoint = -1;
    private Logger logger = getProject().getLogger();

    private ModuleFactory entryPointFactory = new EntryPointFactoryImpl();
    private Module entryPoint;

    @Option(option = "value", description = "Set the number of the entry point (1 -> API REST, 2 -> API REACTIVE)")
    public void setCodeEntryPoint(String number) {
        this.codeEntryPoint = Utils.tryParse(number);
    }

    @TaskAction
    public void generateEntryPointTask() throws IOException, CleanException {

        throwEntryPointTask();

        entryPoint = entryPointFactory.makeDrivenAdapter(codeEntryPoint);

        logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
        logger.lifecycle("Project  Package: {}", entryPoint.getPackageName());
        logger.lifecycle("Entry Point: {} - {}", codeEntryPoint, entryPoint.getName());

        generateEntryPoint();
    }

    private void throwEntryPointTask() {
        if (codeEntryPoint < 0) {
            throw new IllegalArgumentException("No Entry Point is set, usege: gradle generateEntryPoint --value numberEntryPoint");
        }
    }

    private void generateEntryPoint() throws IOException {
        logger.lifecycle(PluginTemplate.GENERATING_CHILDS_DIRS);

        makeDirs();

        logger.lifecycle(PluginTemplate.GENERATED_CHILDS_DIRS);
        logger.lifecycle(PluginTemplate.GENERATING_FILES);

        writedFiles();
        rewriteSettingsGradle();

        logger.lifecycle(PluginTemplate.WRITED_IN_FILES);
    }

    private void makeDirs() {
        getProject().mkdir(entryPoint.getModuleDirSrc());
        getProject().mkdir(entryPoint.getModuleDirTest());
    }

    private void writedFiles() throws IOException {
        Utils.writeString(getProject(), entryPoint.getModuleDir().concat("/").concat(Constants.BUILD_GRADLE), entryPoint.getBuildGradleContentModule());
        Utils.writeString(getProject(), entryPoint.getModuleDirSrc().concat("/").concat(entryPoint.getClassNameModule()).concat(Constants.JAVA_EXTENSION), entryPoint.getModuleClassContent());

    }

    private void rewriteSettingsGradle() throws IOException {
        String settings = Utils.readFile(getProject(), Constants.SETTINGS_GRADLE).collect(Collectors.joining("\n"));
        settings += entryPoint.getSettingsGradleModule();
        Utils.writeString(getProject(), Constants.SETTINGS_GRADLE, settings);
    }
}
