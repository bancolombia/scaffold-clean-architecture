package co.com.bancolombia.factory.entrypoints;

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
    builder.appendToProperties("spring.graphql.graphiql").put("enabled", false);
    builder.addParam("reactive", builder.isReactive());

    builder.setupFromTemplate("entry-point/graphql-api");
  }
}
