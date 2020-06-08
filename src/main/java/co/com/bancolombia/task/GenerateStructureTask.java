package co.com.bancolombia.task;

import co.com.bancolombia.Utils;
import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.options.OptionValues;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenerateStructureTask extends DefaultTask {
    private final ModuleBuilder builder = new ModuleBuilder(getProject());
    private final Logger logger = getProject().getLogger();

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
        builder.addParamPackage(packageName);
        builder.addParam("projectName", projectName);
        builder.addParam("reactive", type == ProjectType.REACTIVE);
        builder.setupFromTemplate("structure");
        builder.persist();
    }

    public enum ProjectType {
        REACTIVE, IMPERATIVE
    }
}
