package co.com.bancolombia.factory.entrypoints;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.validations.ReactiveTypeValidation;
import java.io.IOException;

public class EntryPointKafka implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    builder.runValidations(ReactiveTypeValidation.class);
    builder.setupFromTemplate("entry-point/kafka-consumer-reactive");
    builder.appendToSettings("kafka-consumer", "infrastructure/entry-points");
    String dependency = buildImplementationFromProject(builder.isKotlin(), ":kafka-consumer");
    builder.appendDependencyToModule(APP_SERVICE, dependency);

    builder
        .appendToProperties("spring.kafka.consumer")
        .put("bootstrap-servers", "localhost:9092")
        .put("group-id", builder.getProject().getName());
    builder.appendToProperties("adapters.kafka.consumer").put("topic", "test");
  }
}
