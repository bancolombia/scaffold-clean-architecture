package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.utils.FileUtils;
import co.com.bancolombia.utils.Utils;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public class ValidateStructureTask extends DefaultTask {
    private Logger logger = getProject().getLogger();
    private final String ConfigurationDependenciesName = "implementation";
    private final String modelModule = "model";
    private final String useCaseModule = "usecase";


    @TaskAction
    public void validateStructureTask() throws IOException, CleanException {

        String packageName = FileUtils.readProperties(getProject().getProjectDir().getPath(), "package");
        logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
        getModules().forEach(d -> logger.lifecycle("Submodules: " + d.getKey()));
        logger.lifecycle("Project Package: {}", packageName);

        if (!validateModelLayer()) {
            throw new CleanException("The model layer is invalid");
        }
        if (!validateUseCaseLayer()) {
            throw new CleanException("The use case layer is invalid");
        }
        if (!validateInfrastructureLayer()) {
            throw new CleanException("The infrastructure layer is invalid");
        }
        logger.lifecycle("The project is valid");
    }

    private boolean validateInfrastructureLayer() {
        List<String> modulesExcludes = Arrays.asList(modelModule, "app-service", useCaseModule);
        AtomicBoolean valid = new AtomicBoolean(true);
        Set<Map.Entry<String, Project>> modules = getModules();

        modules.stream().filter(module -> !modulesExcludes.contains(module.getKey()))
                .forEach(dependency -> {
                    validateDependencies(valid, dependency);
                });

        return valid.get();
    }

    private void validateDependencies(AtomicBoolean valid,  Map.Entry<String, Project> dependency) {
        Configuration configuration = getConfiguration(dependency.getKey());
        if (configuration
                .getDependencies().stream().anyMatch(filterDependenciesInfrastructure())) {
            valid.set(false);
        }
    }

    private Predicate<Dependency> filterDependenciesInfrastructure() {
        return dependency -> "app-service"
                .contains(dependency.getName()) && !Arrays.asList(modelModule, useCaseModule)
                .contains(dependency.getName());
    }


    private Set<Map.Entry<String, Project>> getModules() {
        return getProject().getChildProjects().entrySet();

    }

    private Configuration getConfiguration(String moduleName) {

        return getProject().getChildProjects()
                .get(moduleName)
                .getConfigurations()
                .getByName(ConfigurationDependenciesName);
    }

    private boolean validateExistingModule(String module) {
        return (getProject().getChildProjects().containsKey(module));
    }

    private boolean validateModelLayer() {

        if (validateExistingModule(modelModule)) {
            Configuration configuration = getConfiguration(modelModule);
            return configuration.getAllDependencies().size() == 0;
        }
        return true;

    }

    private boolean validateUseCaseLayer() {
        if (validateExistingModule(useCaseModule)) {
            Configuration configuration = getConfiguration(useCaseModule);
            return configuration.getAllDependencies().size() == 1
                    && configuration.getAllDependencies().iterator().next().getName().contains((modelModule));
        }
        return true;
    }
}
