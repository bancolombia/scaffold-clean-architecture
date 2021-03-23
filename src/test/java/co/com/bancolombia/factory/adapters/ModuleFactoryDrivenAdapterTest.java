package co.com.bancolombia.factory.adapters;

import static org.junit.Assert.assertNotNull;

import co.com.bancolombia.exceptions.InvalidTaskOptionException;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.adapters.ModuleFactoryDrivenAdapter.DrivenAdapterType;
import org.junit.Test;

public class ModuleFactoryDrivenAdapterTest {

  @Test
  public void shouldReturnModuleFactory() throws InvalidTaskOptionException {
    for (DrivenAdapterType type : DrivenAdapterType.values()) {
      ModuleFactory factory = ModuleFactoryDrivenAdapter.getDrivenAdapterFactory(type);
      assertNotNull(factory);
    }
  }

  @Test(expected = NullPointerException.class)
  public void shouldHandleError() throws InvalidTaskOptionException {
    ModuleFactoryDrivenAdapter.getDrivenAdapterFactory(null);
  }
}
