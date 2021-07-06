package co.com.bancolombia.factory.adapters;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.commons.MQCommonFactory;
import co.com.bancolombia.factory.validations.ReactiveTypeValidation;
import lombok.AllArgsConstructor;

import java.io.IOException;

@AllArgsConstructor
public class DrivenAdapterMQ implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    builder.runValidations(ReactiveTypeValidation.class);
    builder.setupFromTemplate("driven-adapter/mq-sender");
    builder.appendToSettings("mq-sender", "infrastructure/driven-adapters");
    builder.appendDependencyToModule("app-service", "implementation project(':mq-sender')");
    new MQCommonFactory().buildModule(builder);
  }
}
