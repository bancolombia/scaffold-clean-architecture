package co.com.bancolombia.factory.pipelines;


import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.models.pipelines.AbstractModule;

import java.io.IOException;

public interface ModuleFactory {
    AbstractModule makePipeline(int pipelineCode) throws IOException, CleanException;
}
