package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.utils.FileUtils;
import co.com.bancolombia.utils.Utils;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.logging.Logger;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public class ValidateStructureTask extends DefaultTask {
    private final Logger logger = getProject().getLogger();
    private static final String MODEL_MODULE = "model";
    private static final String USE_CASE_MODULE = "usecase";
    private static final String REACTOR_CORE = "reactor-core";
    private static final String REACTOR_EXTRA = "reactor-extra";
    private static final String SPRING_DEPENDENCIES = "spring-boot-dependencies";


    @TaskAction
    public void validateStructureTask() throws IOException, CleanException {

        String packageName = FileUtils.readProperties(getProject().getProjectDir().getPath(), "package");
        logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
        getModules().forEach(d -> logger.lifecycle("Submodules: " + d.getKey()));
        logger.lifecycle("Project Package: {}", packageName);

        if (!validateModelLayer()) {
            throw new CleanException("Model module is invalid");
        }
        if (!validateUseCaseLayer()) {
            throw new CleanException("Use case module is invalid");
        }
        if (!validateInfrastructureLayer()) {
            throw new CleanException("Infrastructure layer is invalid");
        }
        logger.lifecycle("The project is valid");
    }

    private boolean validateModelLayer() {
        if (validateExistingModule(MODEL_MODULE)) {
            logger.lifecycle("Validating Model Module");
            Configuration configuration = getConfiguration(MODEL_MODULE);
            return configuration.getAllDependencies()
                    .stream().noneMatch(this::filterExcludedDependencies);
        }
        logger.warn("Model module not found");
        return true;
    }

    private boolean validateUseCaseLayer() {
        if (validateExistingModule(USE_CASE_MODULE)) {

            logger.lifecycle("Validating Use Case Module");
            Configuration configuration = getConfiguration(USE_CASE_MODULE);
            DependencySet dependencies = configuration.getAllDependencies();
            return dependencies.stream()
                    .filter(this::filterExcludedDependencies)
                    .count() == 1
                    && dependencies.stream()
                    .filter(this::filterExcludedDependencies).iterator().next().getName().contains((MODEL_MODULE));
        }
        logger.warn("Use case module not found");
        return true;
    }

    private boolean filterExcludedDependencies(Dependency dependency) {
        return !Arrays.asList(REACTOR_EXTRA, REACTOR_CORE, SPRING_DEPENDENCIES).contains(dependency.getName());
    }

    private boolean validateInfrastructureLayer() {
        List<String> modulesExcludes = Arrays.asList(MODEL_MODULE, "app-service", USE_CASE_MODULE);
        AtomicBoolean valid = new AtomicBoolean(true);
        Set<Map.Entry<String, Project>> modules = getModules();

        modules.stream().filter(module -> !modulesExcludes.contains(module.getKey()))
                .forEach(moduleFiltered -> validateModule(valid, moduleFiltered));

        return valid.get();
    }

    private void validateModule(AtomicBoolean valid, Map.Entry<String, Project> moduleFiltered) {
        logger.lifecycle(String.format("Validating %s Module", moduleFiltered.getKey()));
        validateDependencies(valid, moduleFiltered);
    }

    private boolean validateExistingModule(String module) {
        return (getProject().getChildProjects().containsKey(module));
    }

    private Configuration getConfiguration(String moduleName) {
        printDependenciesByModule(moduleName);

        return getProject()
                .getChildProjects()
                .get(moduleName)
                .getConfigurations()
                .getByName(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME);
    }

    private void printDependenciesByModule(String moduleName) {
        getProject()
                .getChildProjects()
                .get(moduleName)
                .getConfigurations().getByName(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME).getDependencies().forEach(dependency -> logger.lifecycle("--- Dependency: " + dependency.getName()));
    }

    private void validateDependencies(AtomicBoolean valid, Map.Entry<String, Project> dependency) {
        Configuration configuration = getConfiguration(dependency.getKey());
        if (configuration
                .getDependencies().stream().anyMatch(filterDependenciesInfrastructure())) {
            valid.set(false);
        }
    }

    private Predicate<Dependency> filterDependenciesInfrastructure() {
        return dependency -> "app-service"
                .contains(dependency.getName()) && !Arrays.asList(MODEL_MODULE, USE_CASE_MODULE)
                .contains(dependency.getName());
    }

    private Set<Map.Entry<String, Project>> getModules() {
        return getProject().getChildProjects().entrySet();
    }
}
