package co.com.bancolombia.factory.adapters;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.Utils.buildImplementation;

import co.com.bancolombia.Constants;
import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.commons.GenericModule;
import java.io.IOException;
import org.gradle.api.logging.Logger;

public class DrivenAdapterSecrets implements ModuleFactory {
  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    Logger logger = builder.getProject().getLogger();

    String secretLibrary = "";
    SecretsBackend secretsBackend = (SecretsBackend) builder.getParam("secrets-backend");
    if (secretsBackend == null || secretsBackend.equals(SecretsBackend.AWS_SECRETS_MANAGER)) {
      if (Boolean.TRUE.equals(builder.isReactive())) {
        secretLibrary = "aws-secrets-manager-async";
        builder.setupFromTemplate("driven-adapter/secrets-reactive");
      } else {
        secretLibrary = "aws-secrets-manager-sync";
        builder.setupFromTemplate("driven-adapter/secrets");
      }
      logger.lifecycle("Generating mode for aws secrets");
      builder.appendToProperties("aws").put("region", "us-east-1").put("secretName", "my-secret");
      GenericModule.addAwsBom(builder);
    } else {
      if (Boolean.TRUE.equals(builder.isReactive())) {
        secretLibrary = "vault-async";
        builder.setupFromTemplate("driven-adapter/secrets-vault-reactive");
      } else {
        secretLibrary = "vault-sync";
        builder.setupFromTemplate("driven-adapter/secrets-vault");
      }
      builder
          .appendToProperties("vault")
          .put("host", "localhost")
          .put("port", 8200)
          .put("roleId", "<set your roleId>")
          .put("secretId", "<set your secretId>")
          .put("secretName", "my-secret");
      logger.lifecycle("Generating mode for vault secrets");
    }
    String dependency =
        buildImplementation(
            builder.isKotlin(),
            "com.github.bancolombia:" + secretLibrary + ":" + Constants.SECRETS_VERSION);
    builder.appendDependencyToModule(APP_SERVICE, dependency);
  }

  public enum SecretsBackend {
    AWS_SECRETS_MANAGER,
    VAULT,
    NONE
  }
}
