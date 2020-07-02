package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.utils.Utils;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.options.OptionValues;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenerateStructureTask extends CleanArchitectureDefaultTask {
    private String packageName = "co.com.bancolombia";
    private ProjectType type = ProjectType.IMPERATIVE;
    private CoveragePlugin coverage = CoveragePlugin.JACOCO;
    private String name = "cleanArchitecture";

    @Option(option = "package", description = "Set principal package to use in the project")
    public void setPackage(String packageName) {
        this.packageName = packageName;
    }

    @Option(option = "type", description = "Set project type")
    public void setType(ProjectType type) {
        this.type = type;
    }

    @Option(option = "name", description = "Set project name, by default is cleanArchitecture ")
    public void setName(String projectName) {
        this.name = projectName;
    }

    @Option(option = "coverage", description = "Set project coverage plugin")
    public void setCoveragePlugin(CoveragePlugin coverage) {
        this.coverage = coverage;
    }

    @OptionValues("type")
    public List<ProjectType> getAvailableProjectTypes() {
        return new ArrayList<>(Arrays.asList(ProjectType.values()));
    }

    @OptionValues("coverage")
    public List<CoveragePlugin> getCoveragePlugins() {
        return new ArrayList<>(Arrays.asList(CoveragePlugin.values()));
    }

    @TaskAction
    public void generateStructureTask() throws IOException, CleanException {
        logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
        logger.lifecycle("Package: {}", packageName);
        logger.lifecycle("Project Type: {}", type);
        logger.lifecycle("Project Name: {}", name);
        builder.addParamPackage(packageName);
        builder.addParam("projectName", name);
        builder.addParam("reactive", type == ProjectType.REACTIVE);
        builder.addParam("jacoco", coverage == CoveragePlugin.JACOCO);
        builder.addParam("cobertura", coverage == CoveragePlugin.COBERTURA);
        builder.setupFromTemplate("structure");
        builder.persist();
    }

    public enum ProjectType {
        REACTIVE, IMPERATIVE
    }

    public enum CoveragePlugin {
        JACOCO, COBERTURA
    }
}
