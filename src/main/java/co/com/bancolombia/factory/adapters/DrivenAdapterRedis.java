package co.com.bancolombia.factory.adapters;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.gradle.api.logging.Logger;

@AllArgsConstructor
public class DrivenAdapterRedis implements ModuleFactory {
  public static final String PARAM_MODE = "task-param-mode";

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    Logger logger = builder.getProject().getLogger();
    builder.loadPackage();
    String typePath = getPathType(builder.isReactive());
    String modePath = getPathMode((Mode) builder.getParam(PARAM_MODE));
    logger.lifecycle("Generating {} in {} mode", typePath, modePath);
    builder.setupFromTemplate("driven-adapter/" + typePath + "/" + modePath);
    builder.appendToSettings("redis", "infrastructure/driven-adapters");
    if (builder.getBooleanParam("include-secret")) {
      builder.setupFromTemplate("driven-adapter/" + typePath + "/secret");
    } else {
      builder.appendToProperties("spring.redis").put("host", "localhost").put("port", 6379);
    }
    builder.appendDependencyToModule("app-service", "implementation project(':redis')");
    if (builder.getBooleanParam("include-secret")) {
      new DrivenAdapterSecrets().buildModule(builder);
    }
  }

  protected String getPathMode(Mode mode) {
    return mode == Mode.REPOSITORY ? "redis-repository" : "redis-template";
  }

  protected String getPathType(boolean isReactive) {
    return isReactive ? "redis-reactive" : "redis";
  }

  public enum Mode {
    REPOSITORY,
    TEMPLATE
  }
}
