package co.com.bancolombia.factory.helpers;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.utils.Utils;
import java.io.IOException;

public class HelperGeneric implements ModuleFactory {
  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    String name = builder.getStringParam("task-param-name");
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException(
          "No Helper name is set, usage: gradle generateHelper " + "--name");
    }
    String dashName = Utils.toDashName(name);
    builder.addParam("name-dash", dashName);
    builder.addParam("name-package", name.toLowerCase());
    builder.appendToSettings(dashName, "infrastructure/helpers");
    builder.appendDependencyToModule("app-service", "implementation project(':" + dashName + "')");
    builder.setupFromTemplate("helper/generic");
  }
}
