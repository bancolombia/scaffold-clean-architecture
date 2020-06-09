package co.com.bancolombia.task;

import co.com.bancolombia.Utils;
import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.entrypoints.EntryPointModuleFactory;
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

    private EntryPointModuleFactory.EntryPointType type;

    @Option(option = "type", description = "Set the type of entry point)")
    public void setCodeEntryPoint(EntryPointModuleFactory.EntryPointType type) {
        this.type = type;
    }

    @OptionValues("type")
    public List<EntryPointModuleFactory.EntryPointType> getAvailablePipelineTypes() {
        return new ArrayList<>(Arrays.asList(EntryPointModuleFactory.EntryPointType.values()));
    }

    @TaskAction
    public void generateEntryPointTask() throws IOException, CleanException {
        if (type == null) {
            throw new IllegalArgumentException("No Entry Point is set, usage: gradle generateEntryPoint --type entryPointType");
        }
        ModuleFactory entryPointFactory = EntryPointModuleFactory.getEntryPointFactory(type);
        logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
        logger.lifecycle("Entry Point type: {}", type);
        entryPointFactory.buildModule(builder);
        builder.persist();
    }
}
