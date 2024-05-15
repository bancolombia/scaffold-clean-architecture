package co.com.bancolombia.factory.entrypoints;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import java.io.IOException;

public class EntryPointMQ implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    builder.setupFromTemplate(getTemplate(builder.isReactive()));
    builder.appendToSettings("mq-listener", "infrastructure/entry-points");
    String dependency = buildImplementationFromProject(":mq-listener");
    builder.appendDependencyToModule(APP_SERVICE, dependency);

    builder
        .appendToProperties("commons.jms")
        .put("input-concurrency", 10)
        .put("input-queue", "DEV.QUEUE.2")
        .put("input-queue-alias", "")
        .put("reactive", builder.isReactive());
    builder.appendToProperties("ibm.mq").put("channel", "DEV.APP.SVRCONN").put("user", "app");
  }

  private String getTemplate(boolean reactive) {
    return reactive ? "entry-point/mq-listener" : "entry-point/mq-listener-sync";
  }
}
