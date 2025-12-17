package co.com.bancolombia.factory.entrypoints;

import static co.com.bancolombia.utils.Utils.buildImplementation;

import co.com.bancolombia.exceptions.InvalidTaskOptionException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.utils.Utils;
import java.io.IOException;

public class EntryPointRestMvcServer implements ModuleFactory {
  private static final String BUILD_GRADLE = "infrastructure/entry-points/api-rest/build.gradle";
  private static final String TOMCAT_EXCLUSION =
      "implementation.exclude group: 'org.springframework.boot', module: 'spring-boot-starter-tomcat'";

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, InvalidTaskOptionException {
    Server server = (Server) builder.getParam("task-param-server");

    switch (server) {
      case JETTY:
        String jettyDependency =
            buildImplementation("org.springframework.boot:spring-boot-starter-jetty");
        builder.updateFile(BUILD_GRADLE, current -> Utils.addDependency(current, jettyDependency));
        builder.updateFile(
            BUILD_GRADLE, current -> Utils.addConfiguration(current, TOMCAT_EXCLUSION));
        return;
      case TOMCAT:
        return;
      default:
        throw new InvalidTaskOptionException("Server option invalid");
    }
  }

  public enum Server {
    TOMCAT,
    JETTY
  }
}
