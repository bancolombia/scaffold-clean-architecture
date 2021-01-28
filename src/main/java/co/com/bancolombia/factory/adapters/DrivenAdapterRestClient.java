package co.com.bancolombia.factory.adapters;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import org.gradle.api.logging.Logger;

import java.io.IOException;

public class DrivenAdapterRestClient implements ModuleFactory {

    @Override
    public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
        Logger logger = builder.getProject().getLogger();
        builder.loadPackage();
        if (builder.isReactive()) {
            logger.lifecycle("Generating rest-consumer for reactive project");
            builder.setupFromTemplate("driven-adapter/reactive-rest-consumer");
        } else {
            logger.lifecycle("Generating rest-consumer for imperative project");
            builder.setupFromTemplate("driven-adapter/rest-consumer");
            builder.appendDependencyToModule("app-service", "compile 'com.fasterxml.jackson.core:jackson-databind'");
        }
        builder.appendToProperties("adapter.restconsumer")
                .put("url", builder.getStringParam("task-param-url"));
        builder.appendToSettings("rest-consumer", "infrastructure/driven-adapters");
        builder.appendDependencyToModule("app-service", "implementation project(':rest-consumer')");

    }

}
