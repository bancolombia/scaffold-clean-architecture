package co.com.bancolombia.factory.adapters;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.commons.ObjectMapperFactory;
import co.com.bancolombia.task.AbstractCleanArchitectureDefaultTask;

import java.io.IOException;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

public class DrivenAdapterJPA implements ModuleFactory {
  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {

    if (Boolean.TRUE.equals(builder.getBooleanParam("include-secret"))) {
      DrivenAdapterSecrets.SecretsBackend secretsBackend =
              DrivenAdapterSecrets.SecretsBackend.valueOf(builder.getSecretsBackendEnabled());
      if (secretsBackend.equals(DrivenAdapterSecrets.SecretsBackend.NONE)) {
          new DrivenAdapterSecrets().buildModule(builder);
          //when new secrets backend is added, the default is aws
          builder.addParam("include-awssecrets", AbstractCleanArchitectureDefaultTask.BooleanOption.TRUE);
      } else {
        builder.addParam("include-awssecrets",
                DrivenAdapterSecrets.SecretsBackend.AWS_SECRETS_MANAGER.equals(secretsBackend));
        builder.addParam("include-vaultsecrets",
                DrivenAdapterSecrets.SecretsBackend.VAULT.equals(secretsBackend));
      }
    }

    builder.setupFromTemplate("driven-adapter/jpa-repository");
    builder.appendToSettings("jpa-repository", "infrastructure/driven-adapters");
    builder
        .appendToProperties("spring.datasource")
        .put("url", "jdbc:h2:mem:test")
        .put("username", "sa")
        .put("password", "pass")
        .put("driverClassName", "org.h2.Driver");
    builder
        .appendToProperties("spring.jpa")
        .put("databasePlatform", "org.hibernate.dialect.H2Dialect");
    String dependency = buildImplementationFromProject(builder.isKotlin(), ":jpa-repository");
    builder.appendDependencyToModule(APP_SERVICE, dependency);

    new ObjectMapperFactory().buildModule(builder);
  }

}
