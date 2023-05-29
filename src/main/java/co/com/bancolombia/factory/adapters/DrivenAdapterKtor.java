package co.com.bancolombia.factory.adapters;

import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.validations.LanguageValidation;
import java.io.IOException;

public class DrivenAdapterKtor implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    builder.runValidations(LanguageValidation.class);
    builder.appendToSettings("ktor-client", "infrastructure/driven-adapters");
    String dependency = buildImplementationFromProject(builder.isKotlin(), ":ktor-client");
    builder.appendDependencyToModule("app-service", dependency);
    builder.setupFromTemplate("driven-adapter/ktor-client");
  }
}
