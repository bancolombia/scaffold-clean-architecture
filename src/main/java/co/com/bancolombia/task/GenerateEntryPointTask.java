package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.entrypoints.ModuleFactoryEntryPoint;
import co.com.bancolombia.factory.entrypoints.EntryPointRestMvcServer.Server;
import co.com.bancolombia.factory.entrypoints.ModuleFactoryEntryPoint.EntryPointType;
import co.com.bancolombia.utils.Utils;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.options.OptionValues;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenerateEntryPointTask extends CleanArchitectureDefaultTask {
    private EntryPointType type;
    private String name;
    private Server server = Server.UNDERTOW;

    @Option(option = "type", description = "Set type of entry point to be generated")
    public void setType(EntryPointType type) {
        this.type = type;
    }

    @Option(option = "name", description = "Set entry point name when GENERIC type")
    public void setName(String name) {
        this.name = name;
    }

    @Option(option = "server", description = "Set server on which the application will run when RESTMVC type")
    public void setServer(Server server) { this.server = server; }

    @OptionValues("server")
    public List<Server> getServerOptions() { return new ArrayList<>(Arrays.asList(Server.values())); }

    @OptionValues("type")
    public List<EntryPointType> getTypes() {
        return new ArrayList<>(Arrays.asList(EntryPointType.values()));
    }

    @TaskAction
    public void generateEntryPointTask() throws IOException, CleanException {
        if (type == null) {
            printHelp();
            throw new IllegalArgumentException("No Entry Point is set, usage: gradle generateEntryPoint --type "
                    + Utils.formatTaskOptions(getTypes()));
        }
        ModuleFactory moduleFactory = ModuleFactoryEntryPoint.getEntryPointFactory(type);
        logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
        logger.lifecycle("Entry Point type: {}", type);
        builder.addParam("task-param-name", name);
        builder.addParam("task-param-server", server);
        moduleFactory.buildModule(builder);
        builder.persist();
    }
}
