package co.com.bancolombia.factory.adapters;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import java.io.IOException;
import org.gradle.api.logging.Logger;

public class DrivenAdapterKms implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    Logger logger = builder.getProject().getLogger();
    String typePath = getPathType(builder.isReactive());
    logger.lifecycle("Generating {}", typePath);

    builder.addAwsBom();
    builder.setupFromTemplate("driven-adapter/" + typePath);
    builder.appendToSettings("kms-repository", "infrastructure/driven-adapters");
    builder
        .appendToProperties("adapter.aws.kms")
        .put("region", "us-east-1")
        .put("host", "localhost")
        .put("protocol", "http")
        .put("port", "4566")
        .put("keyId", "add-your-key-here"); // implementation project('kms-repository')
    String dependency = buildImplementationFromProject(builder.isKotlin(), ":kms-repository");
    builder.appendDependencyToModule(APP_SERVICE, dependency);
    new DrivenAdapterSecrets().buildModule(builder);
  }

  protected String getPathType(boolean isReactive) {
    return isReactive ? "kms-reactive" : "kms";
  }
}
