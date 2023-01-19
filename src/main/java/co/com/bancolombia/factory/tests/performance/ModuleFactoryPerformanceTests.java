package co.com.bancolombia.factory.tests.performance;

import co.com.bancolombia.exceptions.InvalidTaskOptionException;
import co.com.bancolombia.factory.ModuleFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ModuleFactoryPerformanceTests {

  public static ModuleFactory getPerformanceTestsFactory(PerformanceTestType type)
      throws InvalidTaskOptionException {
    switch (type) {
      case JMETER:
        return new JmeterPerformanceTest();
      default:
        throw new InvalidTaskOptionException("Performance test type value invalid");
    }
  }

  public enum PerformanceTestType {
    JMETER
  }
}
