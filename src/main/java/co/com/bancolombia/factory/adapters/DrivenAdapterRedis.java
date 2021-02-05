package co.com.bancolombia.factory.adapters;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import lombok.AllArgsConstructor;
import org.gradle.api.logging.Logger;

import java.io.IOException;

@AllArgsConstructor
public class DrivenAdapterRedis implements ModuleFactory {
    private final Mode mode;

    @Override
    public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
        Logger logger = builder.getProject().getLogger();
        builder.loadPackage();
        String typePath = getPathType(builder.isReactive());
        String modePath = getPathMode();
        logger.lifecycle("Generating {} in {} mode", typePath, modePath);
        builder.setupFromTemplate("driven-adapter/" + typePath + "/" + modePath);
        builder.appendToSettings("redis", "infrastructure/driven-adapters");
        if (builder.getBooleanParam("include-secret")) {
            builder.setupFromTemplate("driven-adapter/" + typePath + "/secret");
        } else {
            builder.appendToProperties("spring.redis")
                    .put("host", "localhost")
                    .put("port", 6379);
        }
        builder.appendDependencyToModule("app-service", "implementation project(':redis')");
        new DrivenAdapterSecrets().buildModule(builder);
    }

    protected String getPathMode() {
        return mode == Mode.REPOSITORY ? "redis-repository" : "redis-template";
    }

    protected String getPathType(boolean isReactive) {
        return isReactive ? "redis-reactive" : "redis";
    }

    public enum Mode {
        REPOSITORY, TEMPLATE
    }
}
