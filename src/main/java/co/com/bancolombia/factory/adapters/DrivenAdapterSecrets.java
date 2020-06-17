package co.com.bancolombia.factory.adapters;

import co.com.bancolombia.Constants;
import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;

import java.io.IOException;

public class DrivenAdapterSecrets implements ModuleFactory {
    @Override
    public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
        if (builder.getBooleanParam("include-secret")) {
            builder.setupFromTemplate("driven-adapter/secrets");
            builder.appendDependencyToModule("app-service",
                    "compile 'co.com.bancolombia:secretsmanager:" + Constants.SECRETS_VERSION + "'");
            builder.appendToProperties("aws")
                    .put("region", "us-east-1")
                    .put("secretName", "my-secret");
        }
    }
}
