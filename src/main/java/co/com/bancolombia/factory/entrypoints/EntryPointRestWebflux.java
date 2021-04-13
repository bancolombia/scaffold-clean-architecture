package co.com.bancolombia.factory.entrypoints;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.validations.ReactiveTypeValidation;
import java.io.IOException;

public class EntryPointRestWebflux implements ModuleFactory {
  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    builder.runValidations(ReactiveTypeValidation.class);
    if (builder.getBooleanParam("task-param-router")) {
      builder.setupFromTemplate("entry-point/rest-webflux/router-functions");
    } else {
      builder.setupFromTemplate("entry-point/rest-webflux");
    }
    builder.appendToSettings("reactive-web", "infrastructure/entry-points");
    builder.appendDependencyToModule("app-service", "implementation project(':reactive-web')");
  }
}
