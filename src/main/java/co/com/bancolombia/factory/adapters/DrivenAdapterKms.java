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
        builder.appendToSettings("kms", "infrastructure/driven-adapters");
        builder.setupFromTemplate("driven-adapter/" + typePath + "/secret");

        builder.appendDependencyToModule("app-service", "implementation project(':kms')");
        new DrivenAdapterSecrets().buildModule(builder);
    }

    protected String getPathType(boolean isReactive) {
        return isReactive ? "kms-reactive" : "kms";
    }
}
