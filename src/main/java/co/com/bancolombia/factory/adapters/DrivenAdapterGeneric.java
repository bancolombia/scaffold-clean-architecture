package co.com.bancolombia.factory.adapters;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.commons.GenericModule;
import java.io.IOException;

public class DrivenAdapterGeneric implements ModuleFactory {
  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    GenericModule.generateGenericModule(
        builder,
        "No name is set for GENERIC type, usage: gradle generateDrivenAdapter "
            + "--type GENERIC --name [name]",
        "infrastructure/driven-adapters",
        "driven-adapter/generic");
  }
}
