package co.com.bancolombia.factory.adapters;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.exceptions.ValidationException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.commons.ObjectMapperFactory;
import java.io.IOException;
import org.gradle.api.logging.Logger;

public class DrivenAdapterRedis implements ModuleFactory {
  public static final String PARAM_MODE = "task-param-mode";

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    if (builder.isReactive() && Mode.REPOSITORY == builder.getParam(PARAM_MODE)) {
      // https://github.com/spring-projects/spring-data-redis/issues/1823
      throw new ValidationException(
          "This mode is only available for imperative projects, please use `template` mode");
    }
    Logger logger = builder.getProject().getLogger();
    String typePath = getPathType(builder.isReactive());
    String modePath = getPathMode((Mode) builder.getParam(PARAM_MODE));
    logger.lifecycle("Generating {} in {} mode", typePath, modePath);
    builder.setupFromTemplate("driven-adapter/" + typePath + "/" + modePath);
    builder.appendToSettings("redis", "infrastructure/driven-adapters");
    if (Boolean.TRUE.equals(builder.getBooleanParam("include-secret"))) {
      builder.setupFromTemplate("driven-adapter/" + typePath + "/secret");
    } else {
      builder.appendToProperties("spring.redis").put("host", "localhost").put("port", 6379);
    }
    String dependency = buildImplementationFromProject(builder.isKotlin(), ":redis");
    builder.appendDependencyToModule(APP_SERVICE, dependency);
    if (Boolean.TRUE.equals(builder.getBooleanParam("include-secret"))) {
      new DrivenAdapterSecrets().buildModule(builder);
    }
    new ObjectMapperFactory().buildModule(builder);
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
