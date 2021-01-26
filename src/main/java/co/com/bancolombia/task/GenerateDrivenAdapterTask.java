package co.com.bancolombia.task;

import co.com.bancolombia.Constants.BooleanOption;
import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.adapters.ModuleFactoryDrivenAdapter;
import co.com.bancolombia.factory.adapters.ModuleFactoryDrivenAdapter.DrivenAdapterType;
import co.com.bancolombia.utils.Utils;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.options.OptionValues;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GenerateDrivenAdapterTask extends CleanArchitectureDefaultTask {
    private DrivenAdapterType type;
    private String name;
    private BooleanOption secret = BooleanOption.FALSE;

    @Option(option = "type", description = "Set type of driven adapter to be generated")
    public void setType(DrivenAdapterType type) {
        this.type = type;
    }

    @Option(option = "name", description = "Set driven adapter name when GENERIC type")
    public void setName(String name) {
        this.name = name;
    }

    @Option(option = "secret", description = "Enable secrets for this driven adapter")
    public void setSecret(BooleanOption secret) {
        this.secret = secret;
    }

    @OptionValues("type")
    public List<DrivenAdapterType> getTypes() {
        return Arrays.asList(DrivenAdapterType.values());
    }

    @OptionValues("secret")
    public List<BooleanOption> getSecretOptions() {
        return Arrays.asList(BooleanOption.values());
    }

    @TaskAction
    public void generateDrivenAdapterTask() throws IOException, CleanException {
        if (type == null) {
            printHelp();
            throw new IllegalArgumentException("No Driven Adapter type is set, usage: gradle generateDrivenAdapter " +
                    "--type " + Utils.formatTaskOptions(getTypes()));
        }
        ModuleFactory moduleFactory = ModuleFactoryDrivenAdapter.getDrivenAdapterFactory(type);
        logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
        logger.lifecycle("Driven Adapter type: {}", type);
        builder.addParam("task-param-name", name);
        builder.addParam("include-secret", secret == BooleanOption.TRUE);
        builder.addParam("lombok", builder.isEnableLombok());

        moduleFactory.buildModule(builder);
        builder.persist();
    }
}
