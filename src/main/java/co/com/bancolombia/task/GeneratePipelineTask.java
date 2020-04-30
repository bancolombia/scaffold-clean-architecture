package co.com.bancolombia.task;

import co.com.bancolombia.Utils;
import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.pipelines.ModuleFactory;
import co.com.bancolombia.factory.pipelines.PipelineFactory;
import co.com.bancolombia.models.pipelines.AbstractModule;
import co.com.bancolombia.templates.Constants;
import co.com.bancolombia.templates.ScaffoldTemplate;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GeneratePipelineTask extends DefaultTask {

    private int pipelineValue = -1;
    private Logger logger = getProject().getLogger();
    private ModuleFactory pipelineFactory = new PipelineFactory();
    private AbstractModule pipeline;

    @Option(option = "value", description = "Set the value to generate the pipeline")
    public void setPipelineValueProject(String pipelineValue) {
        this.pipelineValue = Utils.tryParse(pipelineValue);
    }

    @TaskAction
    public void generatePipelineTask() throws IOException, CleanException {
        if (pipelineValue < 0) {
            throw new IllegalArgumentException("No pipeline value was set, usage: gradle generatePipeline --value 1");
        }

        pipeline = pipelineFactory.makePipeline(pipelineValue);
        logger.lifecycle("Clean Architecture plugin version: {}", Utils.getVersionPlugin());
        logger.lifecycle("Value set: {}", pipelineValue);
        writeFiles();
    }

    private void writeFiles() throws IOException {
        String settings = Utils.readFile(getProject(), Constants.SETTINGS_GRADLE).collect(Collectors.joining("\n"));
        String content = pipeline.getPipelineContent(settings);
        Utils.writeString(getProject(), ScaffoldTemplate.DEPLOYMENT.concat("/").concat(pipeline.getProjectName().concat("_Build").concat(".yaml")), content);
    }

}
