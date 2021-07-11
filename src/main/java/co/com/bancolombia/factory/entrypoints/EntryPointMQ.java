package co.com.bancolombia.factory.entrypoints;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.validations.ReactiveTypeValidation;
import java.io.IOException;

public class EntryPointMQ implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    builder.runValidations(ReactiveTypeValidation.class);
    builder.setupFromTemplate("entry-point/mq-listener");
    builder.appendToSettings("mq-listener", "infrastructure/entry-points");
    builder.appendDependencyToModule("app-service", "implementation project(':mq-listener')");

    builder
        .appendToProperties("commons.jms")
        .put("input-concurrency", 10)
        .put("input-queue", "DEV.QUEUE.2")
        .put("input-queue-alias", "")
        .put("reactive", builder.isReactive());
    builder.appendToProperties("ibm.mq").put("channel", "DEV.APP.SVRCONN").put("user", "app");
  }
}
