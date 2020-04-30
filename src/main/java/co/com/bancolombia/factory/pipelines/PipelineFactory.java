package co.com.bancolombia.factory.pipelines;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.models.pipelines.AbstractModule;
import co.com.bancolombia.models.pipelines.AzureDevOpsPipeline;
import co.com.bancolombia.templates.PipelineTemplate;

import java.io.IOException;

import static co.com.bancolombia.templates.PipelineTemplate.Pipelines.NO_AVAILABLE;

public class PipelineFactory implements ModuleFactory {

    @Override
    public AbstractModule makePipeline(int pipelineCode) throws IOException, CleanException {
        AbstractModule pipeline = null;
        switch (getFactory(pipelineCode)) {
            case AZURE_DEVOPS:
                pipeline = new AzureDevOpsPipeline(pipelineCode);
                break;
            default:
                throw new CleanException("Pipeline value invalid");
        }
        return pipeline;
    }

    private PipelineTemplate.Pipelines getFactory(int pipelineCode) {

        return PipelineTemplate.Pipelines.valueOf(pipelineCode, () -> NO_AVAILABLE);

    }
}
