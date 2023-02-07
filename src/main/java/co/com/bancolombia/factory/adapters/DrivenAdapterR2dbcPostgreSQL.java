package co.com.bancolombia.factory.adapters;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.commons.ObjectMapperFactory;
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
    String dependency = buildImplementationFromProject(builder.isKotlin(), ":r2dbc-postgresql");
    builder.appendDependencyToModule(APP_SERVICE, dependency);
    builder.appendToSettings("r2dbc-postgresql", "infrastructure/driven-adapters");
    new ObjectMapperFactory().buildModule(builder);
  }
}
