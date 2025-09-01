package co.com.bancolombia.factory.adapters;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import java.io.IOException;

public class DrivenAdapterSecretsKafkaStrimzi implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    String secretName = builder.getStringParam("secretName");
    if (secretName == null || secretName.isEmpty()) {
      secretName = "strimzi-kafka-test";
    }
    builder.addParam("secretName", secretName);
    builder.setupFromTemplate("driven-adapter/secrets-kafka-strimzi");
    builder.appendToSettings("secrets", "infrastructure/driven-adapters");
    builder
        .appendToProperties("aws")
        .put("region", "us-east-1")
        .put("strimzi-kafka-secretName", secretName);
    String dependency = buildImplementationFromProject(":secrets");
    builder.appendDependencyToModule(APP_SERVICE, dependency);
  }
}
