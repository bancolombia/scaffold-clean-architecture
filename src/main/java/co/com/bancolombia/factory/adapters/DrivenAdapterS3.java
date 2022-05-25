package co.com.bancolombia.factory.adapters;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.commons.GenericModule;
import java.io.IOException;
import org.gradle.api.logging.Logger;

public class DrivenAdapterS3 implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    Logger logger = builder.getProject().getLogger();
    String typePath = getPathType(builder.isReactive());
    logger.lifecycle("Generating {}", typePath);

    GenericModule.addAwsBom(builder);
    builder.setupFromTemplate("driven-adapter/" + typePath);
    builder.appendToSettings("s3-repository", "infrastructure/driven-adapters");
    builder
        .appendToProperties("adapter.aws.s3")
        .put("bucketName", "test")
        .put("region", "us-east-1")
        .put("endpoint", "http://localhost:49153");
    String dependency = buildImplementationFromProject(builder.isKotlin(), ":s3-repository");
    builder.appendDependencyToModule(APP_SERVICE, dependency);
  }

  protected String getPathType(boolean isReactive) {
    return isReactive ? "s3-reactive" : "s3";
  }
}
