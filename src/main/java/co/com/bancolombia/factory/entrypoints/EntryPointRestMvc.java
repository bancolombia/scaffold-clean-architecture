package co.com.bancolombia.factory.entrypoints;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import java.io.IOException;

public class EntryPointRestMvc implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    builder.setupFromTemplate("entry-point/rest-mvc");
    builder.appendToSettings("api-rest", "infrastructure/entry-points");
    String dependency = buildImplementationFromProject(builder.isKotlin(), ":api-rest");
    builder.appendDependencyToModule(APP_SERVICE, dependency);
    new EntryPointRestMvcServer().buildModule(builder);
  }
}
