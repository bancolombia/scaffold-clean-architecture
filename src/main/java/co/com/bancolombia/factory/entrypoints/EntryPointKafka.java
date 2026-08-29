package co.com.bancolombia.factory.entrypoints;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.Constants.REACTIVE_COMMONS_VERSION;
import static co.com.bancolombia.utils.Utils.buildImplementation;
import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.validations.ReactiveTypeValidation;
import java.io.IOException;

public class EntryPointKafka implements ModuleFactory {

  private static final String MODULE = "kafka-consumer";
  private static final String KAFKA_DOMAIN_PROPERTIES = "reactive.commons.kafka.app";
  private static final String ASYNC_KAFKA_STARTER =
      "org.reactivecommons:async-kafka-starter:" + REACTIVE_COMMONS_VERSION;

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    builder.runValidations(ReactiveTypeValidation.class);
    builder.setupFromTemplate("entry-point/kafka-consumer-reactive");
    builder.appendToSettings(MODULE, "infrastructure/entry-points");
    builder.appendDependencyToModule(APP_SERVICE, buildImplementationFromProject(":" + MODULE));
    // KafkaConfigHelper lives in app-service, so it needs the Reactive Commons Kafka types
    builder.appendDependencyToModule(APP_SERVICE, buildImplementation(ASYNC_KAFKA_STARTER));

    builder
        .appendToProperties(KAFKA_DOMAIN_PROPERTIES + ".connection-properties.security")
        .put("protocol", "${KAFKA_SECURITY_PROTOCOL:PLAINTEXT}");
    builder
        .appendToProperties(KAFKA_DOMAIN_PROPERTIES + ".connection-properties.consumer")
        .put("group-id", "${KAFKA_CONSUMER_GROUP_ID:" + builder.getProjectName() + "}");
    builder
        .appendToProperties(KAFKA_DOMAIN_PROPERTIES)
        .put("withDLQRetry", "${APP_ASYNC_WITH_DLQ_RETRY:true}")
        .put("maxRetries", "${APP_ASYNC_MAX_RETRIES:5}")
        .put("retryDelay", "${APP_ASYNC_RETRY_DELAY:1000}")
        .put("createTopology", "${APP_ASYNC_CREATE_TOPOLOGY:true}");
  }
}
