package co.com.bancolombia.factory.helpers;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.commons.GenericModule;
import java.io.IOException;

public class HelperGeneric implements ModuleFactory {
  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    GenericModule.generateGenericModule(
        builder,
        "No Helper name is set, usage: gradle generateHelper " + "--name",
        "infrastructure/helpers",
        "helper/generic");
  }
}
