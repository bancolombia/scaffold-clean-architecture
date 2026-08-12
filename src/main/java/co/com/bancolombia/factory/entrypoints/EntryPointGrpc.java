package co.com.bancolombia.factory.entrypoints;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import java.io.IOException;

public class EntryPointGrpc implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {

    builder.appendToSettings("grpc", "infrastructure/entry-points");

    String dependency = buildImplementationFromProject(":grpc");

    builder.appendDependencyToModule(APP_SERVICE, dependency);

    builder.appendToProperties("spring.grpc.server").put("port", "${GRPC_SERVER_PORT:9090}");

    builder.setupFromTemplate("entry-point/grpc");
  }
}
