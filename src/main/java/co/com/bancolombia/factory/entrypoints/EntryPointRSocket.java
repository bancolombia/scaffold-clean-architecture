package co.com.bancolombia.factory.entrypoints;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.validations.ReactiveTypeValidation;
import java.io.IOException;

public class EntryPointRSocket implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    builder.runValidations(ReactiveTypeValidation.class);
    builder.appendToSettings("rsocket-responder", "infrastructure/entry-points");
    builder.appendToProperties("spring.rsocket.server").put("port", 7000);
    String dependency = buildImplementationFromProject(builder.isKotlin(), ":rsocket-responder");
    builder.appendDependencyToModule(APP_SERVICE, dependency);
    builder.setupFromTemplate("entry-point/rsocket-responder");
  }
}
