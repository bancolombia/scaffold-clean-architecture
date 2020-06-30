package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.pipelines.ModuleFactoryPipeline;
import co.com.bancolombia.factory.pipelines.ModuleFactoryPipeline.PipelineType;
import co.com.bancolombia.utils.Utils;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.options.OptionValues;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GeneratePipelineTask extends CleanArchitectureDefaultTask {
    private PipelineType type;

    @Option(option = "type", description = "Set type of pipeline to be generated")
    public void setType(PipelineType type) {
        this.type = type;
    }

    @OptionValues("type")
    public List<PipelineType> getTypes() {
        return new ArrayList<>(Arrays.asList(PipelineType.values()));
    }

    @TaskAction
    public void generatePipelineTask() throws IOException, CleanException {
        if (type == null) {
            printHelp();
            throw new IllegalArgumentException("No Pipeline type was set, usage: gradle generatePipeline --type "
                    + Utils.formatTaskOptions(getTypes()));
        }
        ModuleFactory pipelineFactory = ModuleFactoryPipeline.getPipelineFactory(type);
        logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
        logger.lifecycle("Pipeline type: {}", type);
        pipelineFactory.buildModule(builder);
        builder.persist();
    }

}
