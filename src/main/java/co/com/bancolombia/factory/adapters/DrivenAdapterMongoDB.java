package co.com.bancolombia.factory.adapters;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.utils.FileUtils;

import java.io.IOException;

public class DrivenAdapterMongoDB implements ModuleFactory {
    @Override
    public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
        builder.addParamPackage(FileUtils.readProperties("package"));
        builder.setupFromTemplate("driven-adapter/mongo-repository");
        builder.appendToSettings("mongo-repository", "infrastructure/driven-adapters");
        builder.appendToProperties("spring.data.mongodb")
                .put("uri", "mongodb://localhost:27017/test");
        builder.appendDependencyToModule("app-service",
                "implementation project(':mongo-repository')");
        new DrivenAdapterSecrets().buildModule(builder);
    }
}
