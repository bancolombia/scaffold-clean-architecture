package co.com.bancolombia.factory.adapters;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.commons.ObjectMapperFactory;
import co.com.bancolombia.factory.validations.ReactiveTypeValidation;
import java.io.IOException;
import org.gradle.api.logging.Logger;

public class DrivenAdapterR2dbc implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    Logger logger = builder.getLogger();
    builder.runValidations(ReactiveTypeValidation.class);
    logger.lifecycle("Generating for reactive project");
    builder.setupFromTemplate("driven-adapter/r2dbc-postgresql");
    new ObjectMapperFactory().buildModule(builder);
  }
}
