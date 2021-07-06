package co.com.bancolombia.factory.commons;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import java.io.IOException;

public class MQCommonFactory implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    builder.setupFromTemplate("commons/mq-common");
    builder
        .appendToProperties("ibm")
        .put("output-concurrency", 10)
        .put("output-queue", "DEV.QUEUE.1")
        .put("input-concurrency", 10)
        .put("input-queue", "DEV.QUEUE.2");
    builder.appendToProperties("ibm.mq").put("channel", "DEV.APP.SVRCONN").put("user", "app");
    builder.appendToSettings("mq-common", "infrastructure/helpers");
  }
}
