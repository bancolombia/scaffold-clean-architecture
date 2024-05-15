package co.com.bancolombia.factory.adapters;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import java.io.IOException;

public class DrivenAdapterMQ implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    builder.setupFromTemplate(getTemplate(builder.isReactive()));
    builder.appendToSettings("mq-sender", "infrastructure/driven-adapters");
    String dependency = buildImplementationFromProject(":mq-sender");

    builder.appendDependencyToModule(APP_SERVICE, dependency);

    builder
        .appendToProperties("commons.jms")
        .put("output-concurrency", 10)
        .put("output-queue", "DEV.QUEUE.1")
        .put("producer-ttl", 0)
        .put("reactive", builder.isReactive());
    builder.appendToProperties("ibm.mq").put("channel", "DEV.APP.SVRCONN").put("user", "app");
  }

  private String getTemplate(boolean reactive) {
    return reactive ? "driven-adapter/mq-sender" : "driven-adapter/mq-sender-sync";
  }
}
