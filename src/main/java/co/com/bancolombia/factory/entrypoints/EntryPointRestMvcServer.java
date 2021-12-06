package co.com.bancolombia.factory.entrypoints;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.Utils.buildImplementation;
import static co.com.bancolombia.utils.Utils.tomcatExclusion;

import co.com.bancolombia.Constants;
import co.com.bancolombia.exceptions.InvalidTaskOptionException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import java.io.IOException;

public class EntryPointRestMvcServer implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, InvalidTaskOptionException {
    Server server = (Server) builder.getParam("task-param-server");

    switch (server) {
      case UNDERTOW:
        String undertowDependency =
            buildImplementation(
                builder.isKotlin(),
                "org.springframework.boot:spring-boot-starter-undertow:"
                    + Constants.UNDERTOW_VERSION);
        builder.appendDependencyToModule(APP_SERVICE, undertowDependency);
        builder.appendConfigurationToModule(APP_SERVICE, tomcatExclusion(builder.isKotlin()));
        return;
      case JETTY:
        String jettyDependency =
            buildImplementation(
                builder.isKotlin(),
                "org.springframework.boot:spring-boot-starter-jetty:" + Constants.UNDERTOW_VERSION);
        builder.appendDependencyToModule(APP_SERVICE, jettyDependency);
        builder.appendConfigurationToModule(APP_SERVICE, tomcatExclusion(builder.isKotlin()));
        return;
      case TOMCAT:
        return;
      default:
        throw new InvalidTaskOptionException("Server option invalid");
    }
  }

  public enum Server {
    UNDERTOW,
    TOMCAT,
    JETTY
  }
}
