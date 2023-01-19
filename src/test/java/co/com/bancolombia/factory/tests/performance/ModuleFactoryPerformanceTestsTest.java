package co.com.bancolombia.factory.tests.performance;

import static org.junit.Assert.assertNotNull;

import co.com.bancolombia.exceptions.InvalidTaskOptionException;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.tests.performance.ModuleFactoryPerformanceTests.PerformanceTestType;
import org.junit.Test;

public class ModuleFactoryPerformanceTestsTest {

  @Test
  public void shouldReturnModuleFactory() throws InvalidTaskOptionException {
    for (PerformanceTestType type : PerformanceTestType.values()) {
      ModuleFactory factory = ModuleFactoryPerformanceTests.getPerformanceTestsFactory(type);
      assertNotNull(factory);
    }
  }
}
