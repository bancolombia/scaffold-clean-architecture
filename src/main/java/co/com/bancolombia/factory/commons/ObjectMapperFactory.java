package co.com.bancolombia.factory.commons;

import co.com.bancolombia.Constants;
import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import java.io.IOException;

public class ObjectMapperFactory implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    builder.setupFromTemplate("commons/object-mapper");
    builder.appendDependencyToModule(
        "app-service",
        "compile 'org.reactivecommons.utils:object-mapper:"
            + Constants.RCOMMONS_OBJECT_MAPPER_VERSION
            + "'");
  }
}
