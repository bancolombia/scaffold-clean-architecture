package co.com.bancolombia.factory.entrypoints;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.commons.GenericModule;
import co.com.bancolombia.factory.validations.ReactiveTypeValidation;
import java.io.IOException;

public class EntryPointSQS implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    builder.runValidations(ReactiveTypeValidation.class);

    builder.setupFromTemplate("entry-point/sqs");
    builder.appendToSettings("sqs-listener", "infrastructure/entry-points");
    String dependency = buildImplementationFromProject(builder.isKotlin(), ":sqs-listener");
    builder.appendDependencyToModule(APP_SERVICE, dependency);

    GenericModule.addAwsBom(builder);
    builder
        .appendToProperties("entrypoint.sqs")
        .put("region", "us-east-1")
        .put("queueUrl", "http://localhost:4566/000000000000/sample")
        .put("waitTimeSeconds", 20)
        .put("maxNumberOfMessages", 10)
        .put("visibilityTimeout", 10000)
        .put("numberOfThreads", 1);
  }
}
