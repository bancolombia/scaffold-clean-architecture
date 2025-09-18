package co.com.bancolombia.factory.tests.performance;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import java.io.IOException;

public class PerformanceTestJmeter implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    String templatePath = "test/performance-test/jmeter";

    if (builder.getBooleanParam("task-param-exist-api-rest")) {
      templatePath += "/Jmeter/Api";
    }

    builder.setupFromTemplate(templatePath);
  }
}
