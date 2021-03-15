package co.com.bancolombia.factory.pipelines;

import static org.junit.Assert.assertNotNull;

import co.com.bancolombia.exceptions.InvalidTaskOptionException;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.pipelines.ModuleFactoryPipeline.PipelineType;
import org.junit.Test;

public class ModuleFactoryPipelineTest {

  @Test
  public void shouldReturnModuleFactory() throws InvalidTaskOptionException {
    for (PipelineType type : PipelineType.values()) {
      ModuleFactory factory = ModuleFactoryPipeline.getPipelineFactory(type);
      assertNotNull(factory);
    }
  }

  @Test(expected = NullPointerException.class)
  public void shouldHandleError() throws InvalidTaskOptionException {
    ModuleFactoryPipeline.getPipelineFactory(null);
  }
}
