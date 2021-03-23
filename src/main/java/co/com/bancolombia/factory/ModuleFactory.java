package co.com.bancolombia.factory;

import co.com.bancolombia.exceptions.CleanException;
import java.io.IOException;

public interface ModuleFactory {
  void buildModule(ModuleBuilder builder) throws IOException, CleanException;
}
