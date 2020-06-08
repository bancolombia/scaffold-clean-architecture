package co.com.bancolombia.task;

import co.com.bancolombia.Utils;
import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.exceptions.ParamNotFoundException;
import co.com.bancolombia.templates.Constants;
import co.com.bancolombia.templates.PluginTemplate;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.options.OptionValues;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenerateStructureTask extends GenerateBaseTask {
    private Logger logger = getProject().getLogger();
    private String packageName = "co.com.bancolombia";
    private ProjectType type = ProjectType.IMPERATIVE;
    private String projectName = "cleanArchitecture";

    @Option(option = "package", description = "Set the principal package to use in the project")
    public void setPackage(String packageName) {
        this.packageName = packageName;
    }

    @Option(option = "type", description = "Set project type, the options are  (reactive | imperative) ")
    public void setType(ProjectType type) {
        this.type = type;
    }

    @Option(option = "name", description = "Set the project name, by default is cleanArchitecture ")
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @OptionValues("type")
    public List<ProjectType> getAvailableProjectTypes() {
        return new ArrayList<>(Arrays.asList(ProjectType.values()));
    }

    @TaskAction
    public void generateStructureTask() throws IOException, CleanException {
        logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
        logger.lifecycle("Package: {}", packageName);
        logger.lifecycle("Project Type: {}", type);
        logger.lifecycle("Project Name: {}", projectName);
        logger.lifecycle(PluginTemplate.GENERATING_CHILDS_DIRS);
        addParamPackage(packageName);
        addParam("projectName", projectName);
        addParam("reactive", type == ProjectType.REACTIVE);
        createDirs();
        logger.lifecycle(PluginTemplate.GENERATED_CHILDS_DIRS);
        logger.lifecycle(PluginTemplate.GENERATING_FILES);
        createFiles();
        logger.lifecycle(PluginTemplate.WRITED_IN_FILES);
    }

    @Override
    protected void setupDirs() {
        String packagePath = getPackagePath();
        addDir(Constants.DEPLOYMENT);
        addDir(Constants.APPLICATION, Constants.MAIN_JAVA, packagePath, Constants.CONFIG);
        addDir(Constants.APPLICATION, Constants.TEST_JAVA, packagePath);
        addDir(Constants.APPLICATION, Constants.MAIN_RESOURCES);
        addDir(Constants.INFRASTRUCTURE, Constants.DRIVEN_ADAPTERS);
        addDir(Constants.INFRASTRUCTURE, Constants.ENTRY_POINTS);
        addDir(Constants.INFRASTRUCTURE, Constants.HELPERS);
        addDir(Constants.DOMAIN, Constants.MODEL, Constants.MAIN_JAVA, packagePath, Constants.MODEL);
        addDir(Constants.DOMAIN, Constants.MODEL, Constants.TEST_JAVA, packagePath, Constants.MODEL);
        addDir(Constants.DOMAIN, Constants.USECASE_FOLDER, Constants.MAIN_JAVA, packagePath, Constants.USECASE_FOLDER);
        addDir(Constants.DOMAIN, Constants.USECASE_FOLDER, Constants.TEST_JAVA, packagePath, Constants.USECASE_FOLDER);
    }

    @Override
    protected void setupFiles() throws ParamNotFoundException, IOException {
        addFileTemplates("default");
    }

    public enum ProjectType {
        REACTIVE, IMPERATIVE
    }
}
