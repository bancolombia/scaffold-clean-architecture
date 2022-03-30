package co.com.bancolombia.factory.commons;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.utils.Utils;
import java.io.IOException;

public class GenericModule {
  private GenericModule() {}

  public static void generateGenericModule(
      ModuleBuilder builder, String exceptionMessage, String baseDir, String template)
      throws IOException, CleanException {
    String name = builder.getStringParam("task-param-name");

    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException(exceptionMessage);
    }
    String dashName = Utils.toDashName(name);
    builder.addParam("name-dash", dashName);
    builder.addParam("name-package", name.toLowerCase());
    builder.appendToSettings(dashName, baseDir);
    String dependency = buildImplementationFromProject(builder.isKotlin(), ":" + dashName);
    builder.appendDependencyToModule(APP_SERVICE, dependency);
    builder.setupFromTemplate(template);
  }
}
