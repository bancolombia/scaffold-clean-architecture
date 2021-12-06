package co.com.bancolombia.factory.entrypoints;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.validations.ReactiveTypeValidation;
import java.io.IOException;

public class EntryPointRestWebflux implements ModuleFactory {
  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    builder.runValidations(ReactiveTypeValidation.class);
    if (Boolean.TRUE.equals(builder.getBooleanParam("task-param-router"))) {
      builder.setupFromTemplate("entry-point/rest-webflux/router-functions");
    } else {
      builder.setupFromTemplate("entry-point/rest-webflux");
    }
    builder.appendToSettings("reactive-web", "infrastructure/entry-points");
    String dependency = buildImplementationFromProject(builder.isKotlin(), ":reactive-web");
    builder.appendDependencyToModule(APP_SERVICE, dependency);
  }
}
