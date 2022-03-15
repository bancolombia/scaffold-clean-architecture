package co.com.bancolombia.factory.adapters;

import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.commons.ObjectMapperFactory;
import java.io.IOException;
import org.gradle.api.logging.Logger;

public class DrivenAdapterBinStash implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {

    CacheMode cacheMode = (CacheMode) builder.getParam("task-param-cache-mode");
    builder.setupFromTemplate("driven-adapter/bin-stash");

    builder.addParam("include-local", cacheMode.equals(CacheMode.LOCAL));
    builder.addParam("include-hybrid", cacheMode.equals(CacheMode.HYBRID));
    builder.addParam("include-centralized", cacheMode.equals(CacheMode.CENTRALIZED));
    builder.appendToSettings("bin-stash", "infrastructure/driven-adapters");
    String dependency = buildImplementationFromProject(builder.isKotlin(), ":bin-stash");
    builder.appendDependencyToModule("app-service", dependency);

    builder.appendToProperties("stash.memory").put("maxSize", "10000");
    builder.appendToProperties("stash.redis").put("host", "myredis.host");
    builder.appendToProperties("stash.redis").put("port", "6379");
    builder.appendToProperties("stash.redis").put("database", "0");
    builder.appendToProperties("stash.redis").put("password", "mypwd");

    new ObjectMapperFactory().buildModule(builder);
  }

  public enum CacheMode {
    LOCAL,
    HYBRID,
    CENTRALIZED
  }
}
