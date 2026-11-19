package co.com.bancolombia.factory.adapters;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import java.io.IOException;

public class DrivenAdapterGrpc implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    // Only a blocking stub is generated, so reactive projects must wrap the calls themselves
    if (builder.isReactive()) {
      builder
          .getLogger()
          .warn(
              "The gRPC driven adapter generates a blocking stub; "
                  + "wrap its calls (e.g. Mono.fromCallable on boundedElastic) in reactive flows");
    }
    builder
        .appendToProperties("spring.grpc.client.channel.example-service")
        .put("target", "static://localhost:9090");
    builder.setupFromTemplate("driven-adapter/grpc");
  }
}
