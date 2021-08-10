package co.com.bancolombia.factory.helpers;

import static org.junit.Assert.assertNotNull;

import co.com.bancolombia.exceptions.InvalidTaskOptionException;
import co.com.bancolombia.factory.ModuleFactory;
import org.junit.Test;

public class ModuleFactoryHelperTest {

  @Test
  public void shouldReturnModuleFactory() throws InvalidTaskOptionException {
    ModuleFactory factory = ModuleFactoryHelpers.getDrivenAdapterFactory();
    assertNotNull(factory);
  }
}
