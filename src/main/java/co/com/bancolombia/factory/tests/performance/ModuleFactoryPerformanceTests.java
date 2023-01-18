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
        return new JmeterPerformanceTests();
      default:
        throw new InvalidTaskOptionException("Pipeline value invalid");
    }
  }

  public enum PerformanceTestType {
    JMETER
  }
}
