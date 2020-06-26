package co.com.bancolombia.factory.adapters;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import org.gradle.api.logging.Logger;

import java.io.IOException;

public class DrivenAdapterMongoDB implements ModuleFactory {
    @Override
    public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
        Logger logger = builder.getProject().getLogger();
        builder.loadPackage();
        if (builder.isReactive()) {
            logger.lifecycle("Generating for reactive project");
            builder.setupFromTemplate("driven-adapter/mongo-reactive");
        } else {
            logger.lifecycle("Generating for imperative project");
            builder.setupFromTemplate("driven-adapter/mongo-repository");
        }
        builder.appendToSettings("mongo-repository", "infrastructure/driven-adapters");
        builder.appendToProperties("spring.data.mongodb")
                .put("uri", "mongodb://localhost:27017/test");
        builder.appendDependencyToModule("app-service",
                "implementation project(':mongo-repository')");
        new DrivenAdapterSecrets().buildModule(builder);
    }
}
