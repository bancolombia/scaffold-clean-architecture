package co.com.bancolombia.factory.adapters;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.commons.GenericModule;
import java.io.IOException;
import org.gradle.api.logging.Logger;

public class DrivenAdapterKms implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    Logger logger = builder.getLogger();
    String typePath = getPathType(builder.isReactive());
    logger.lifecycle("Generating {}", typePath);

    GenericModule.addAwsBom(builder);
    builder.setupFromTemplate("driven-adapter/" + typePath);
    builder
        .appendToProperties("adapters.aws.kms")
        .put("region", "us-east-1")
        .put("host", "localhost")
        .put("protocol", "http")
        .put("port", "4566")
        .put("keyId", "add-your-key-here"); // implementation project('kms-repository')
    new DrivenAdapterSecrets().buildModule(builder);
  }

  protected String getPathType(boolean isReactive) {
    return isReactive ? "kms-reactive" : "kms";
  }
}
