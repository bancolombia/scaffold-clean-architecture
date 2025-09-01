package co.com.bancolombia.factory.entrypoints;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.validations.ReactiveTypeValidation;
import java.io.IOException;

public class EntryPointKafkaStrimzi implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    builder.runValidations(ReactiveTypeValidation.class);
    String name = builder.getStringParam("name");
    if (name == null || name.isEmpty()) {
      name = "kafkaStrimzi";
    }
    String topicConsumer = builder.getStringParam("topicConsumer");
    if (topicConsumer == null || topicConsumer.isEmpty()) {
      topicConsumer = "test-with-registries";
    }
    builder.addParam("name", name);

    builder.addParam("topicConsumer", topicConsumer);
    builder.setupFromTemplate("entry-point/kafka-strimzi-consumer");
    builder.appendToSettings("kafka-consumer", "infrastructure/entry-points");
    String dependency = buildImplementationFromProject(":kafka-consumer");
    builder.appendDependencyToModule(APP_SERVICE, dependency);

    builder
        .appendToProperties("spring.kafka.consumer")
        .put("bootstrap-servers", "localhost:9092")
        .put("group-id", builder.getProject().getName());
    builder.appendToProperties("adapters.kafka.consumer").put("topic", topicConsumer);
  }
}
