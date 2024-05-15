package co.com.bancolombia.factory.adapters;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.commons.ObjectMapperFactory;
import java.io.IOException;
import org.gradle.api.logging.Logger;

public class DrivenAdapterMongoDB implements ModuleFactory {
  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    Logger logger = builder.getProject().getLogger();

    builder.setUpSecretsInAdapter();

    if (Boolean.TRUE.equals(builder.isReactive())) {
      logger.lifecycle("Generating for reactive project");
      builder.setupFromTemplate("driven-adapter/mongo-reactive");
    } else {
      logger.lifecycle("Generating for imperative project");
      builder.setupFromTemplate("driven-adapter/mongo-repository");
    }

    builder.appendToSettings("mongo-repository", "infrastructure/driven-adapters");
    builder.appendToProperties("spring.data.mongodb").put("uri", "mongodb://localhost:27017/test");
    String dependency = buildImplementationFromProject(":mongo-repository");
    builder.appendDependencyToModule(APP_SERVICE, dependency);

    new ObjectMapperFactory().buildModule(builder);
  }
}
