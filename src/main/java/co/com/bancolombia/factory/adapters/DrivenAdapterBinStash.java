package co.com.bancolombia.factory.adapters;

import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

import co.com.bancolombia.Constants;
import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.commons.ObjectMapperFactory;
import java.io.IOException;

public class DrivenAdapterBinStash implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {

    CacheMode cacheMode = (CacheMode) builder.getParam("task-param-cache-mode");
    builder.addParam("include-local", cacheMode.equals(CacheMode.LOCAL));
    builder.addParam("include-hybrid", cacheMode.equals(CacheMode.HYBRID));
    builder.addParam("include-centralized", cacheMode.equals(CacheMode.CENTRALIZED));

    builder.setupFromTemplate("driven-adapter/bin-stash");

    builder.appendToSettings("bin-stash", "infrastructure/driven-adapters");
    String dependency = buildImplementationFromProject(":bin-stash");
    builder.appendDependencyToModule(Constants.APP_SERVICE, dependency);

    builder.appendToProperties("stash.memory").put("maxSize", "10000");
    builder
        .appendToProperties("stash.redis")
        .put("host", "myredis.host")
        .put("port", "6379")
        .put("database", "0")
        .put("password", "mypwd");

    new ObjectMapperFactory().buildModule(builder);
  }

  public enum CacheMode {
    LOCAL,
    HYBRID,
    CENTRALIZED
  }
}
