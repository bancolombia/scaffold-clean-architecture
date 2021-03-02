package co.com.bancolombia.factory.adapters;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import lombok.AllArgsConstructor;
import org.gradle.api.logging.Logger;

import java.io.IOException;

@AllArgsConstructor
public class DrivenAdapterKms implements ModuleFactory {

    @Override
    public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
        Logger logger = builder.getProject().getLogger();
        builder.loadPackage();
        String typePath = getPathType(builder.isReactive());
        logger.lifecycle("Generating {}", typePath);
        builder.setupFromTemplate("driven-adapter/" + typePath);
        builder.appendToSettings("kms-repository", "infrastructure/driven-adapters");
        builder.appendToProperties("adapter.aws.kms")
                .put("region", "us-east-1")
                .put("host", "localhost")
                .put("protocol", "http")
                .put("port", "4566")
                .put("keyId", "add-your-key-here");
        builder.appendDependencyToModule("app-service", "implementation project(':kms-repository')");
        new DrivenAdapterSecrets().buildModule(builder);
    }

    protected String getPathType(boolean isReactive) {
        return isReactive ? "kms-reactive" : "kms";
    }
}
