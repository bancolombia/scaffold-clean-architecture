package co.com.bancolombia.factory.adapters;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.Utils.buildImplementation;
import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.utils.swagger.Swagger;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import org.gradle.api.logging.Logger;

public class DrivenAdapterRestConsumer implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    Logger logger = builder.getProject().getLogger();
    if (Boolean.TRUE.equals(builder.isReactive())) {
      logger.lifecycle("Generating rest-consumer for reactive project");
      builder.setupFromTemplate("driven-adapter/consumer-rest/reactive-rest-consumer");
      String implementation =
          buildImplementation(
              builder.isKotlin(), "org.springframework.boot:spring-boot-starter-webflux");
      builder.appendDependencyToModule(APP_SERVICE, implementation);
      builder.appendToProperties("adapter.restconsumer").put("timeout", 5000);
      Swagger.fromBuilder(builder, "infrastructure/driven-adapters/rest-consumer", false);
    } else {
      logger.lifecycle("Generating rest-consumer for imperative project");
      builder.setupFromTemplate("driven-adapter/consumer-rest/rest-consumer");
      String implementation =
          buildImplementation(builder.isKotlin(), "com.fasterxml.jackson.core:jackson-databind");
      builder.appendDependencyToModule(APP_SERVICE, implementation);
    }
    builder
        .appendToProperties("adapter.restconsumer")
        .put("url", builder.getStringParam("task-param-url"));
    builder.appendToProperties("management.health.circuitbreakers").put("enabled", true);
    withCircuitBreaker(builder.appendToProperties("resilience4j.circuitbreaker.instances.testGet"));
    withCircuitBreaker(
        builder.appendToProperties("resilience4j.circuitbreaker.instances.testPost"));
    builder.appendToSettings("rest-consumer", "infrastructure/driven-adapters");
    String dependency = buildImplementationFromProject(builder.isKotlin(), ":rest-consumer");
    builder.appendDependencyToModule(APP_SERVICE, dependency);
  }

  private void withCircuitBreaker(ObjectNode instance) {
    instance
        .put("registerHealthIndicator", true)
        .put("failureRateThreshold", 50)
        .put("slowCallRateThreshold", 50)
        .put("slowCallDurationThreshold", "2s")
        .put("permittedNumberOfCallsInHalfOpenState", 3)
        .put("slidingWindowSize", 10)
        .put("minimumNumberOfCalls", 10)
        .put("waitDurationInOpenState", "10s");
  }
}
