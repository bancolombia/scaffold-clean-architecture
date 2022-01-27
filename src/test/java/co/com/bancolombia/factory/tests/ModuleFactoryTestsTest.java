package co.com.bancolombia.factory.tests;

import static org.junit.Assert.assertNotNull;

import co.com.bancolombia.exceptions.InvalidTaskOptionException;
import co.com.bancolombia.factory.ModuleFactory;
import org.junit.Test;

public class ModuleFactoryTestsTest {

  @Test
  public void shouldReturnModuleFactory() throws InvalidTaskOptionException {
    ModuleFactory factory = ModuleFactoryTests.getTestsFactory();
    assertNotNull(factory);
  }
}
