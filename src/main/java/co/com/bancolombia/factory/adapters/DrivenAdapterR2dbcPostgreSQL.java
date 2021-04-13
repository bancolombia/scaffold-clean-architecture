package co.com.bancolombia.factory.adapters;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.validations.ReactiveTypeValidation;
import java.io.IOException;
import org.gradle.api.logging.Logger;

public class DrivenAdapterR2dbcPostgreSQL implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    Logger logger = builder.getProject().getLogger();
    builder.runValidations(ReactiveTypeValidation.class);
    logger.lifecycle("Generating for reactive project");
    builder.setupFromTemplate("driven-adapter/r2dbc-postgresql");
    builder.appendDependencyToModule("app-service", "implementation project(':r2dbc-postgresql')");
    builder.appendToSettings("r2dbc-postgresql", "infrastructure/driven-adapters");
  }
}
