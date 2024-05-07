package co.com.bancolombia.factory.entrypoints;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import java.io.IOException;

public class EntryPointGraphql implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    String path = builder.getStringParam("task-param-pathgql");
    if (!path.startsWith("/")) {
      throw new IllegalArgumentException("The path must start with /");
    }
    builder.appendToSettings("graphql-api", "infrastructure/entry-points");
    builder.appendToProperties("spring.graphql.graphiql").put("enabled", false);
    builder.addParam("reactive", builder.isReactive());

    String dependency = buildImplementationFromProject(builder.isKotlin(), ":graphql-api");
    builder.appendDependencyToModule(APP_SERVICE, dependency);

    builder.setupFromTemplate("entry-point/graphql-api");
  }
}
