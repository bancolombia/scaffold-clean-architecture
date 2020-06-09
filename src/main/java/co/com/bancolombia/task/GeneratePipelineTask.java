package co.com.bancolombia.task;

import co.com.bancolombia.Utils;
import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.pipelines.PipelineModuleFactory;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.gradle.api.tasks.options.OptionValues;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GeneratePipelineTask extends DefaultTask {
    private final ModuleBuilder builder = new ModuleBuilder(getProject());
    private final Logger logger = getProject().getLogger();

    private PipelineModuleFactory.PipelineType type;

    @Option(option = "type", description = "Set the type of pipeline")
    public void setPipelineValueProject(PipelineModuleFactory.PipelineType type) {
        this.type = type;
    }

    @OptionValues("type")
    public List<PipelineModuleFactory.PipelineType> getAvailablePipelineTypes() {
        return new ArrayList<>(Arrays.asList(PipelineModuleFactory.PipelineType.values()));
    }

    @TaskAction
    public void generatePipelineTask() throws IOException, CleanException {
        if (type == null) {
            throw new IllegalArgumentException("No pipeline value was set, usage: gradle generatePipeline --type azure");
        }
        ModuleFactory pipelineFactory = PipelineModuleFactory.getPipelineFactory(type);
        logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
        logger.lifecycle("Pipeline type: {}", type);
        pipelineFactory.buildModule(builder);
        builder.persist();
    }

}
