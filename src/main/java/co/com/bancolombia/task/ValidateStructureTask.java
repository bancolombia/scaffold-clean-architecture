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
    private final String ConfigurationDependenciesName ="implementation";
    private final String modelModule = "model";
    private final String useCaseModule = "usecase";


    @TaskAction
    public void validateStructureTask() throws IOException, CleanException {

        String packageName = FileUtils.readProperties("package");
        logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
        logger.lifecycle("Project Package: {}", packageName);
        if (!validateModelLayer()) {
            throw new CleanException("the model layer is invalid");
        }
        if (!validateUseCaseLayer()) {
            throw new CleanException("the use case layer is invalid");
        }
        if (!validateInfrastructureLayer()) {
            throw new CleanException("the infrastructure layer is invalid");
        }

        logger.lifecycle("The project is valid");

    }

    private boolean validateInfrastructureLayer() {
        List<String> modulesExcludes = Arrays.asList(modelModule, "app-service", useCaseModule);
        AtomicBoolean valid = new AtomicBoolean(true);
        Set<Map.Entry<String, Project>> modules = getModules();
        List<String> dependencies = new LinkedList<>();
        modules.forEach(stringProjectEntry -> dependencies.add(stringProjectEntry.getKey()));

        modules.stream().filter(module -> !modulesExcludes.contains(module.getKey()))
                .forEach(module -> {
                            if (getConfiguration(module.getKey(), ConfigurationDependenciesName)
                                    .getDependencies().stream().anyMatch(filterDependenciesInfrastructure(dependencies))) {
                                valid.set(false);
                            }
                        }
                );

        return valid.get();
    }

    private Predicate<Dependency> filterDependenciesInfrastructure(List<String> dependencies) {
        return dependency -> dependencies
                .contains(dependency.getName()) && !Arrays.asList(modelModule, useCaseModule)
                .contains(dependency.getName());
    }


    private Set<Map.Entry<String, Project>> getModules() {
        return getProject().getChildProjects().entrySet();

    }


    private Configuration getConfiguration(String moduleName, String configurationName) {

        return getProject().getChildProjects()
                .get(moduleName)
                .getConfigurations()
                .getByName(configurationName);


    }

    private void validateExistingModule(String module) {
        if (!getProject().getChildProjects().containsKey(module)) {
            throw new IllegalArgumentException(module + " module don't exist ");
        }

    }

    private boolean validateModelLayer() {

        validateExistingModule(modelModule);
        Configuration configuration = getConfiguration(modelModule, ConfigurationDependenciesName);

        return configuration.getAllDependencies().size() == 0;
    }


    private boolean validateUseCaseLayer() {
        validateExistingModule(useCaseModule);
        Configuration configuration = getConfiguration(useCaseModule, ConfigurationDependenciesName);
        return configuration.getAllDependencies().size() == 1
                && configuration.getAllDependencies().iterator().next().getName().contains((modelModule));
    }

}

