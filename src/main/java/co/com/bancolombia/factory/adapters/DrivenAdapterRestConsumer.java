package co.com.bancolombia.factory.adapters;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import java.io.IOException;
import org.gradle.api.logging.Logger;

public class DrivenAdapterRestConsumer implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    Logger logger = builder.getProject().getLogger();
    if (builder.isReactive()) {
      logger.lifecycle("Generating rest-consumer for reactive project");
      builder.setupFromTemplate("driven-adapter/consumer-rest/reactive-rest-consumer");
      builder.appendDependencyToModule(
          "app-service", "implementation 'org.springframework.boot:spring-boot-starter-webflux'");
      builder.appendToProperties("adapter.restconsumer").put("timeout", 5000);
    } else {
      logger.lifecycle("Generating rest-consumer for imperative project");
      builder.setupFromTemplate("driven-adapter/consumer-rest/rest-consumer");
      builder.appendDependencyToModule(
          "app-service", "implementation 'com.fasterxml.jackson.core:jackson-databind'");
    }
    builder
        .appendToProperties("adapter.restconsumer")
        .put("url", builder.getStringParam("task-param-url"));
    builder.appendToSettings("rest-consumer", "infrastructure/driven-adapters");
    builder.appendDependencyToModule("app-service", "implementation project(':rest-consumer')");
  }
}
