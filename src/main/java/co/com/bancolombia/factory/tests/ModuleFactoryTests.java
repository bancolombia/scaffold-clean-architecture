package co.com.bancolombia.factory.tests;

import co.com.bancolombia.factory.ModuleFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ModuleFactoryTests {
  public static ModuleFactory getTestsFactory() {
    return new AcceptanceTest();
  }
}
