package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.entrypoints.ModuleFactoryEntryPoint;
import co.com.bancolombia.factory.entrypoints.ModuleFactoryEntryPoint.EntryPointType;
import co.com.bancolombia.utils.Utils;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.options.OptionValues;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenerateEntryPointTask extends DefaultTask {
    private final ModuleBuilder builder = new ModuleBuilder(getProject());
    private final Logger logger = getProject().getLogger();

    private EntryPointType type;
    private String name;

    @Option(option = "type", description = "Set type of entry point to be generated")
    public void setCodeEntryPoint(EntryPointType type) {
        this.type = type;
    }

    @Option(option = "name", description = "Set entry point name when GENERIC type")
    public void setEntryPointName(String name) {
        this.name = name;
    }

    @OptionValues("type")
    public List<EntryPointType> getTypes() {
        return new ArrayList<>(Arrays.asList(EntryPointType.values()));
    }

    @TaskAction
    public void generateEntryPointTask() throws IOException, CleanException {
        if (type == null) {
            throw new IllegalArgumentException("No Entry Point is set, usage: gradle generateEntryPoint --type "
                    + Utils.formatTaskOptions(getTypes()));
        }
        ModuleFactory moduleFactory = ModuleFactoryEntryPoint.getEntryPointFactory(type);
        logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
        logger.lifecycle("Entry Point type: {}", type);
        builder.addParam("task-param-name", name);
        moduleFactory.buildModule(builder);
        builder.persist();
    }
}
