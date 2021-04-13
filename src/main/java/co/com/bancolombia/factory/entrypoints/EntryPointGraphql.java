package co.com.bancolombia.factory.entrypoints;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.validations.ReactiveTypeValidation;
import java.io.IOException;

public class EntryPointGraphql implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    builder.runValidations(ReactiveTypeValidation.class);
    String name = builder.getStringParam("task-param-pathgql");
    if (!name.startsWith("/")) {
      throw new IllegalArgumentException("The path must start with /");
    }
    builder.appendToSettings("graphql-api", "infrastructure/entry-points");
    builder.appendToProperties("graphql.servlet").put("enabled", true).put("mapping", name);
    builder
        .appendToProperties("graphql.playground")
        .put("mapping", "/playground")
        .put("endpoint", name)
        .put("enabled", true);
    builder.appendDependencyToModule("app-service", "implementation project(':graphql-api')");
    builder.setupFromTemplate("entry-point/graphql-api");
  }
}
