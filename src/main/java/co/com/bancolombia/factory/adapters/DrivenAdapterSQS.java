package co.com.bancolombia.factory.adapters;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.commons.GenericModule;
import java.io.IOException;

public class DrivenAdapterSQS implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    GenericModule.addAwsBom(builder);
    String template = "driven-adapter/sqs";
    if (builder.isReactive()) {
      template = "driven-adapter/sqs-reactive";
    }
    builder.setupFromTemplate(template);
    builder.appendToSettings("sqs-sender", "infrastructure/driven-adapters");
    builder
        .appendToProperties("adapter.sqs")
        .put("region", "us-east-1")
        .put("queueUrl", "http://localhost:4566/000000000000/sample")
        .put("endpoint", "http://localhost:4566 # For localstack only");
    String dependency = buildImplementationFromProject(":sqs-sender");
    builder.appendDependencyToModule(APP_SERVICE, dependency);
  }
}
