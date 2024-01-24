package co.com.bancolombia.factory.adapters;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.validations.ReactiveTypeValidation;
import java.io.IOException;
import org.gradle.api.logging.Logger;

public class DrivenAdapterCognitoTokenProvider implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    Logger logger = builder.getProject().getLogger();
    builder.runValidations(ReactiveTypeValidation.class);

    logger.lifecycle("Generating cognito token provider for reactive project");
    builder.setupFromTemplate("driven-adapter/cognito-token-provider/reactive");

    builder
        .appendToProperties("adapter.cognito")
        .put("secret", "<cognito-credentials-secret-name>")
        .put("timeout", 5000)
        .put("endpoint", "https://<domain>.auth.<region>.amazoncognito.com/oauth2/token");

    builder.appendToSettings("cognito-token-provider", "infrastructure/driven-adapters");
    String dependency =
        buildImplementationFromProject(builder.isKotlin(), ":cognito-token-provider");
    builder.appendDependencyToModule(APP_SERVICE, dependency);
  }
}
