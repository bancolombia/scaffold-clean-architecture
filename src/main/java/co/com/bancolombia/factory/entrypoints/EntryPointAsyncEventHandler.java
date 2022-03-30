package co.com.bancolombia.factory.entrypoints;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.validations.ReactiveTypeValidation;
import java.io.IOException;

public class EntryPointAsyncEventHandler implements ModuleFactory {
  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    builder.runValidations(ReactiveTypeValidation.class);
    builder.setupFromTemplate("entry-point/async-event-handler");
    builder.appendToSettings("async-event-handler", "infrastructure/entry-points");
    String dependency = buildImplementationFromProject(builder.isKotlin(), ":async-event-handler");
    builder.appendDependencyToModule(APP_SERVICE, dependency);
  }
}
