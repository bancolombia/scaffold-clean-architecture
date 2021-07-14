package co.com.bancolombia.factory.tests;

import co.com.bancolombia.factory.ModuleFactory;

public class ModuleFactoryTests {
  public static ModuleFactory getTestsFactory() {
    return new AcceptanceTest();
  }
}
