package co.com.bancolombia.factory.adapters;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.gradle.api.logging.Logger;

@AllArgsConstructor
public class DrivenAdapterS3 implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    Logger logger = builder.getProject().getLogger();
    builder.loadPackage();
    String typePath = getPathType(builder.isReactive());
    logger.lifecycle("Generating {}", typePath);
    builder.setupFromTemplate("driven-adapter/" + typePath);
    builder.appendToSettings("s3-repository", "infrastructure/driven-adapters");
    builder
        .appendToProperties("adapter.aws.s3")
        .put("bucketName", "")
        .put("region", "us-east-1")
        .put("endpoint", "");
    builder.appendDependencyToModule("app-service", "implementation project(':s3-repository')");
  }

  protected String getPathType(boolean isReactive) {
    return isReactive ? "s3-reactive" : "s3";
  }
}
