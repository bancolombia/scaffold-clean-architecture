package co.com.bancolombia.factory.adapters;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.utils.Utils;
import org.gradle.api.logging.Logger;

import java.io.IOException;

public class DrivenAdapterRestClient implements ModuleFactory {

    @Override
    public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
        Logger logger = builder.getProject().getLogger();
        builder.loadPackage();
        if (builder.isReactive()) {
            logger.lifecycle("Generating for reactive project");
            builder.setupFromTemplate("driven-adapter/reactive-rest-client");
        } else {
            logger.lifecycle("Generating for imperative project");
            builder.setupFromTemplate("driven-adapter/rest-client");
        }
        builder.appendToSettings("rest-client", "infrastructure/driven-adapters");
        builder.appendDependencyToModule("app-service", "implementation project(':rest-client')");

    }

}
