package co.com.bancolombia.factory.pipelines;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;

import java.io.IOException;

public class PipelineAzure implements ModuleFactory {

    @Override
    public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
        // TODO: Review this params
        builder.addParam("sonar.java.binaries", "**/build/classes/java/main");
        builder.addParam("sonar.junit.reportsPaths", "**/build/test-results/test");
        builder.setupFromTemplate("pipeline/azure");
    }
}
