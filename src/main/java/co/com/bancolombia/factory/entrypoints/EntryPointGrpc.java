package co.com.bancolombia.factory.entrypoints;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import java.io.IOException;

public class EntryPointGrpc implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    builder.appendToProperties("spring.grpc.server").put("port", "${GRPC_SERVER_PORT:9090}");

    builder.setupFromTemplate("entry-point/grpc");
  }
}
