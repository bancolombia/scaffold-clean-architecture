package co.com.bancolombia.factory.entrypoints;

import static org.junit.Assert.assertNotNull;

import co.com.bancolombia.exceptions.InvalidTaskOptionException;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.entrypoints.ModuleFactoryEntryPoint.EntryPointType;
import org.junit.Test;

public class ModuleFactoryEntryPointTest {

  @Test
  public void shouldReturnModuleFactory() throws InvalidTaskOptionException {
    for (EntryPointType type : EntryPointType.values()) {
      ModuleFactory factory = ModuleFactoryEntryPoint.getEntryPointFactory(type);
      assertNotNull(factory);
    }
  }

  @Test(expected = NullPointerException.class)
  public void shouldHandleError() throws InvalidTaskOptionException {
    ModuleFactoryEntryPoint.getEntryPointFactory(null);
  }
}
