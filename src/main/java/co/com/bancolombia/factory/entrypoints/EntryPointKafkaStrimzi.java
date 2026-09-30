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
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

/**
 * Kafka consumer entry point built on Reactive Commons, validating every message against a JSON
 * Schema stored in an Apicurio Registry.
 *
 * @see <a href="https://bancolombia.github.io/reactive-commons-java/docs/reactive-commons/configuration_properties/kafka-schema-validation">Kafka Schema Validation (Apicurio)</a>
 */
public class EntryPointKafkaStrimzi implements ModuleFactory {

  private static final String MODULE = "kafka-consumer";
  private static final String KAFKA_DOMAIN_PROPERTIES = "reactive.commons.kafka.app";
  private static final String ASYNC_KAFKA_APICURIO_STARTER =
      "org.reactivecommons:async-kafka-apicurio-starter:" + REACTIVE_COMMONS_VERSION;
  private static final String DEFAULT_TOPIC = "test-with-registries";
  private static final String MAIN_REGISTRY = "main-registry";

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    builder.runValidations(ReactiveTypeValidation.class);
    String topicConsumer = builder.getStringParam("topicConsumer");
    if (topicConsumer == null || topicConsumer.isEmpty()) {
      topicConsumer = DEFAULT_TOPIC;
    }
    builder.addParam("topicConsumer", topicConsumer);

    builder.setupFromTemplate("entry-point/kafka-strimzi-consumer");
    builder.appendToSettings(MODULE, "infrastructure/entry-points");
    builder.appendDependencyToModule(APP_SERVICE, buildImplementationFromProject(":" + MODULE));
    // KafkaConfigHelper lives in app-service, so it needs the Reactive Commons Kafka
    // and Apicurio validation types. The starter replaces async-kafka-starter.
    builder.appendDependencyToModule(APP_SERVICE, buildImplementation(ASYNC_KAFKA_APICURIO_STARTER));

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
        .put("retryDelay", "${APP_ASYNC_RETRY_DELAY:1000}");

    appendApicurioProperties(builder, topicConsumer);
  }

  // See: https://bancolombia.github.io/reactive-commons-java/docs/reactive-commons/configuration_properties/kafka-schema-validation
  private void appendApicurioProperties(ModuleBuilder builder, String topicConsumer) {
    ObjectNode apicurio = builder.appendToProperties(KAFKA_DOMAIN_PROPERTIES + ".apicurio");
    ArrayNode registries = apicurio.putArray("registries");
    ObjectNode registry = registries.addObject();
    registry.put("name", MAIN_REGISTRY);

    ObjectNode properties = registry.putObject("properties");
    properties.put(
        "apicurio.registry.url", "${APICURIO_REGISTRY_URL:http://localhost:8080/apis/registry/v3}");
    properties.put("apicurio.registry.artifact.group-id", "kafka");
    properties.put("apicurio.registry.find-latest", true);

    ArrayNode topics = registry.putArray("topics");
    topics.addObject().put("name", topicConsumer);
  }
}
