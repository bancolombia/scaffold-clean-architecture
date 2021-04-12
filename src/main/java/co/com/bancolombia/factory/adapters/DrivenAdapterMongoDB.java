package co.com.bancolombia.factory.adapters;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import java.io.IOException;
import org.gradle.api.logging.Logger;

public class DrivenAdapterMongoDB implements ModuleFactory {
  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    Logger logger = builder.getProject().getLogger();
    if (builder.isReactive()) {
      logger.lifecycle("Generating for reactive project");
      builder.setupFromTemplate("driven-adapter/mongo-reactive");
    } else {
      logger.lifecycle("Generating for imperative project");
      builder.setupFromTemplate("driven-adapter/mongo-repository");
    }
    builder.appendToSettings("mongo-repository", "infrastructure/driven-adapters");
    builder.appendToProperties("spring.data.mongodb").put("uri", "mongodb://localhost:27017/test");
    builder.appendDependencyToModule("app-service", "implementation project(':mongo-repository')");
    if (builder.getBooleanParam("include-secret")) {
      new DrivenAdapterSecrets().buildModule(builder);
    }
  }
}
