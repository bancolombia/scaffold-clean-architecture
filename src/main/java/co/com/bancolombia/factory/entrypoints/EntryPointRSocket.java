package co.com.bancolombia.factory.entrypoints;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.validations.ReactiveTypeValidation;
import java.io.IOException;

public class EntryPointRSocket implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    builder.runValidations(ReactiveTypeValidation.class);
    builder.appendToProperties("spring.rsocket.server").put("port", 7000);
    builder.setupFromTemplate("entry-point/rsocket-responder");
  }
}
