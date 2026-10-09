package co.com.bancolombia.factory.adapters;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.commons.GenericModule;
import java.io.IOException;
import org.gradle.api.logging.Logger;

public class DrivenAdapterS3 implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    Logger logger = builder.getLogger();
    String typePath = getPathType(builder.isReactive());
    logger.lifecycle("Generating {}", typePath);

    GenericModule.addAwsBom(builder);
    builder.setupFromTemplate("driven-adapter/" + typePath);
    builder
        .appendToProperties("adapter.aws.s3")
        .put("bucketName", "test")
        .put("region", "us-east-1")
        .put("endpoint", "https://s3.localhost.localstack.cloud:4566");
  }

  protected String getPathType(boolean isReactive) {
    return isReactive ? "s3-reactive" : "s3";
  }
}
