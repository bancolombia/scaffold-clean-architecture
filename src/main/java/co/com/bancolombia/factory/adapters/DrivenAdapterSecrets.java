package co.com.bancolombia.factory.adapters;

import co.com.bancolombia.Constants;
import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import org.gradle.api.logging.Logger;

import java.io.IOException;

public class DrivenAdapterSecrets implements ModuleFactory {
    @Override
    public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
        builder.loadPackage();
        Logger logger = builder.getProject().getLogger();
        String secretLibrary = "";
        if (builder.isReactive()) {
            secretLibrary = "aws-secrets-manager-async";
            builder.setupFromTemplate("driven-adapter/secrets-reactive");
        } else {
            secretLibrary = "aws-secrets-manager-sync";
            builder.setupFromTemplate("driven-adapter/secrets");
        }
        logger.lifecycle("Generating  mode");
        builder.appendDependencyToModule("app-service",
                "compile 'com.github.bancolombia:" + secretLibrary + ":" + Constants.SECRETS_VERSION + "'");
        builder.appendToProperties("aws")
                .put("region", "us-east-1")
                .put("secretName", "my-secret");
    }
}
