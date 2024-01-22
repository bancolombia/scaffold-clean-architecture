package co.com.bancolombia.factory.adapters;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.commons.ObjectMapperFactory;
import co.com.bancolombia.task.AbstractCleanArchitectureDefaultTask;
import java.io.IOException;
import org.gradle.api.logging.Logger;

public class DrivenAdapterMongoDB implements ModuleFactory {
  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    Logger logger = builder.getProject().getLogger();

    if (Boolean.TRUE.equals(builder.getBooleanParam("include-secret"))) {
      DrivenAdapterSecrets.SecretsBackend secretsBackend =
          DrivenAdapterSecrets.SecretsBackend.valueOf(builder.getSecretsBackendEnabled());
      if (!secretsBackend.equals(DrivenAdapterSecrets.SecretsBackend.NONE)) {
        builder.addParam(
            "include-awssecrets",
            DrivenAdapterSecrets.SecretsBackend.AWS_SECRETS_MANAGER.equals(secretsBackend));
        builder.addParam(
            "include-vaultsecrets",
            DrivenAdapterSecrets.SecretsBackend.VAULT.equals(secretsBackend));
      } else {
        new DrivenAdapterSecrets().buildModule(builder);
        // when new secrets backend is added, the default is aws
        builder.addParam(
            "include-awssecrets", AbstractCleanArchitectureDefaultTask.BooleanOption.TRUE);
      }
      logger.lifecycle("Adding secret dependency");
    }

    if (Boolean.TRUE.equals(builder.isReactive())) {
      logger.lifecycle("Generating for reactive project");
      builder.setupFromTemplate("driven-adapter/mongo-reactive");
    } else {
      logger.lifecycle("Generating for imperative project");
      builder.setupFromTemplate("driven-adapter/mongo-repository");
    }

    builder.appendToSettings("mongo-repository", "infrastructure/driven-adapters");
    builder.appendToProperties("spring.data.mongodb").put("uri", "mongodb://localhost:27017/test");
    String dependency = buildImplementationFromProject(builder.isKotlin(), ":mongo-repository");
    builder.appendDependencyToModule(APP_SERVICE, dependency);

    new ObjectMapperFactory().buildModule(builder);
  }
}
