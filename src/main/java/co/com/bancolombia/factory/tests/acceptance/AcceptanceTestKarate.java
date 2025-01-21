package co.com.bancolombia.factory.tests.acceptance;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import java.io.IOException;

public class AcceptanceTestKarate implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    String templatePath = "test/acceptance-test";

    if (Boolean.TRUE.equals(builder.getBooleanParam("task-param-exist-api-rest"))) {
      templatePath += "/api-rest";
    }

    builder.setupFromTemplate(templatePath);
  }
}
