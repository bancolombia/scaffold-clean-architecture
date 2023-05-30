package co.com.bancolombia.task;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.exceptions.InvalidTaskOptionException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import java.io.IOException;

public abstract class AfterModuleFactory implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    // some custom module should be called
    if ("CleanException".equals(builder.getStringParam("throw"))) {
      throw new CleanException("Thrown out for testing");
    }
    if ("Exception".equals(builder.getStringParam("throw"))) {
      throw new RuntimeException("Thrown out for testing");
    }
    if ("InvalidTaskOptionException".equals(builder.getStringParam("throw"))) {
      throw new InvalidTaskOptionException("Thrown out for testing");
    }
    builder.addParam(check(), "OK");
  }

  protected abstract String check();
}
