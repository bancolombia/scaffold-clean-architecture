package co.com.bancolombia.factory.adapters;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import java.io.IOException;

public class DrivenAdapterGrpc implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {

    builder
        .appendToProperties("spring.grpc.client.channels.example-service")
        .put("target", "static://localhost:9090");
    builder.setupFromTemplate("driven-adapter/grpc");
  }
}
