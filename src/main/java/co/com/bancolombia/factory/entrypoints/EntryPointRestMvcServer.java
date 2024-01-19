package co.com.bancolombia.factory.entrypoints;

import static co.com.bancolombia.utils.Utils.buildImplementation;

import co.com.bancolombia.exceptions.InvalidTaskOptionException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.utils.Utils;
import java.io.IOException;

public class EntryPointRestMvcServer implements ModuleFactory {
  private static final String BUILD_GRADLE = "infrastructure/entry-points/api-rest/build.gradle";
  private static final String BUILD_GRADLE_KTS =
      "infrastructure/entry-points/api-rest/build.gradle.kts";
  private static final String TOMCAT_EXCLUSION_KOTLIN =
      "configurations {\n\tall {\n\t\texclude(group = \"org.springframework.boot\", module = \"spring-boot-starter-tomcat\")\n\t}\n}";
  private static final String TOMCAT_EXCLUSION =
      "implementation.exclude group: 'org.springframework.boot', module: 'spring-boot-starter-tomcat'";

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, InvalidTaskOptionException {
    Server server = (Server) builder.getParam("task-param-server");
    String file = builder.isKotlin() ? BUILD_GRADLE_KTS : BUILD_GRADLE;

    switch (server) {
      case UNDERTOW:
        String undertowDependency =
            buildImplementation(
                builder.isKotlin(), "org.springframework.boot:spring-boot-starter-undertow");
        builder.updateFile(file, current -> Utils.addDependency(current, undertowDependency));
        builder.updateFile(
            file, current -> Utils.addConfiguration(current, tomcatExclusion(builder.isKotlin())));
        return;
      case JETTY:
        String jettyDependency =
            buildImplementation(
                builder.isKotlin(), "org.springframework.boot:spring-boot-starter-jetty");
        builder.updateFile(file, current -> Utils.addDependency(current, jettyDependency));
        builder.updateFile(
            file, current -> Utils.addConfiguration(current, tomcatExclusion(builder.isKotlin())));
        return;
      case TOMCAT:
        return;
      default:
        throw new InvalidTaskOptionException("Server option invalid");
    }
  }

  private static String tomcatExclusion(boolean isKotlin) {
    return isKotlin ? TOMCAT_EXCLUSION_KOTLIN : TOMCAT_EXCLUSION;
  }

  public enum Server {
    UNDERTOW,
    TOMCAT,
    JETTY
  }
}
