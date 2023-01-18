package co.com.bancolombia.factory.tests.performance;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import java.io.IOException;

public class JmeterPerformanceTests implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    builder.setupFromTemplate("test/performance-test/jmeter");
  }
}
