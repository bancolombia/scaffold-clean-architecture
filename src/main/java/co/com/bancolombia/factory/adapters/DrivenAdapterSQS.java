package co.com.bancolombia.factory.adapters;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.validations.ReactiveTypeValidation;
import java.io.IOException;

public class DrivenAdapterSQS implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    builder.runValidations(ReactiveTypeValidation.class);
    builder.addAwsBom();
    builder.setupFromTemplate("driven-adapter/sqs");
    builder.appendToSettings("sqs-sender", "infrastructure/driven-adapters");
    builder
        .appendToProperties("adapter.sqs")
        .put("region", "us-east-1")
        .put("queueUrl", "http://localhost:4566/000000000000/sample");
    String dependency = buildImplementationFromProject(builder.isKotlin(), ":sqs-sender");
    builder.appendDependencyToModule(APP_SERVICE, dependency);
  }
}
