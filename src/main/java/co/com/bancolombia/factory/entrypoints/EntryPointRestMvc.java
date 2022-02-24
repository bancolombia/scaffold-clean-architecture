package co.com.bancolombia.factory.entrypoints;

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
    builder.appendDependencyToModule("app-service", dependency);
    if (Boolean.TRUE.equals(builder.getBooleanParam("include-swagger"))) {
      builder.addParam("module", "api-rest");
      builder.setupFromTemplate("entry-point/swagger");
      if (builder.isKotlin()) {
        builder
            .appendToProperties("spring.mvc.pathmatch")
            .put("matching-strategy", "ant_path_matcher");
      }
    }
    new EntryPointRestMvcServer().buildModule(builder);
  }
}
