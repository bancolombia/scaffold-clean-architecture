package co.com.bancolombia.task;

import co.com.bancolombia.Utils;
import co.com.bancolombia.exceptions.CleanException;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.options.OptionValues;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenerateStructureTask extends GenerateBaseTask {
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
        addParamPackage(packageName);
        addParam("projectName", projectName);
        addParam("reactive", type == ProjectType.REACTIVE);
        setupFromTemplate("structure");
        executeTask();
    }

    public enum ProjectType {
        REACTIVE, IMPERATIVE
    }
}
