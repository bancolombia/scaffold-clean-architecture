package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.adapters.ModuleFactoryDrivenAdapter;
import co.com.bancolombia.factory.adapters.ModuleFactoryDrivenAdapter.DrivenAdapterType;
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

public class GenerateDrivenAdapterTask extends DefaultTask {
    private final ModuleBuilder builder = new ModuleBuilder(getProject());
    private final Logger logger = getProject().getLogger();

    private DrivenAdapterType type;
    private String name;

    @Option(option = "type", description = "Set type of driven adapter to be generated")
    public void setDrivenAdapter(DrivenAdapterType type) {
        this.type = type;
    }

    @Option(option = "name", description = "Set driven adapter name when GENERIC type")
    public void setDrivenAdapterName(String name) {
        this.name = name;
    }

    // TODO: Enable autogeneration of secrets manager boolean

    @OptionValues("type")
    public List<DrivenAdapterType> getTypes() {
        return new ArrayList<>(Arrays.asList(DrivenAdapterType.values()));
    }

    @TaskAction
    public void generateDrivenAdapterTask() throws IOException, CleanException {
        if (type == null) {
            throw new IllegalArgumentException("No Driven Adapter type is set, usage: gradle generateDrivenAdapter " +
                    "--type " + Utils.formatTaskOptions(getTypes()));
        }
        ModuleFactory moduleFactory = ModuleFactoryDrivenAdapter.getDrivenAdapterFactory(type);
        logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
        logger.lifecycle("Driven Adapter type: {}", type);
        builder.addParam("task-param-name", name);
        moduleFactory.buildModule(builder);
        builder.persist();
    }
}
