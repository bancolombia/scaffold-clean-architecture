package co.com.bancolombia.factory.adapters;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.commons.ObjectMapperFactory;
import org.gradle.api.logging.Logger;

import java.io.IOException;

import static co.com.bancolombia.utils.Utils.buildImplementation;
import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

public class DrivenAdapterBinStash implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {

    CacheMode cacheMode = (CacheMode) builder.getParam("task-param-cache-mode");
    Logger logger = builder.getProject().getLogger();

    builder.addParam("include-local",cacheMode.equals(CacheMode.LOCAL));
    builder.addParam("include-hybrid",cacheMode.equals(CacheMode.HIBRID));
    builder.addParam("include-centralized",cacheMode.equals(CacheMode.CENTRALIZED));
    builder.appendToSettings("bin-stash", "infrastructure/driven-adapters");

    builder.appendToProperties("stash.memory").put("maxSize", "10000");
    builder.appendToProperties("stash.redis").put("host", "myredis.host");
    builder.appendToProperties("stash.redis").put("port", "6379");
    builder.appendToProperties("stash.redis").put("database", "0");
    builder.appendToProperties("stash.redis").put("password", "mypwd");

    new ObjectMapperFactory().buildModule(builder);

  }

  protected String getPathType(boolean isReactive) {
    return isReactive ? "dynamo-db-reactive" : "dynamo-db";
  }


  public enum CacheMode {
    LOCAL,
    HIBRID,
    CENTRALIZED
  }
}
