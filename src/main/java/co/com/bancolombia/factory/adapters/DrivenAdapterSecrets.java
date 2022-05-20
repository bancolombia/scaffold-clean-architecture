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
    if (Boolean.TRUE.equals(builder.isReactive())) {
      secretLibrary = "aws-secrets-manager-async";
      builder.setupFromTemplate("driven-adapter/secrets-reactive");
    } else {
      secretLibrary = "aws-secrets-manager-sync";
      builder.setupFromTemplate("driven-adapter/secrets");
    }
    logger.lifecycle("Generating  mode");
    GenericModule.addAwsBom(builder);
    String dependency =
        buildImplementation(
            builder.isKotlin(),
            "com.github.bancolombia:" + secretLibrary + ":" + Constants.SECRETS_VERSION);
    builder.appendDependencyToModule(APP_SERVICE, dependency);
    builder.appendToProperties("aws").put("region", "us-east-1").put("secretName", "my-secret");
  }
}
