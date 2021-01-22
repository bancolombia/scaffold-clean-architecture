package co.com.bancolombia.factory.entrypoints;

import co.com.bancolombia.Constants;
import co.com.bancolombia.exceptions.InvalidTaskOptionException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;

import java.io.IOException;

public class EntryPointRestMvcServer implements ModuleFactory {

    @Override
    public void buildModule(ModuleBuilder builder) throws IOException, InvalidTaskOptionException {
        Server server = (Server) builder.getParam("task-param-server");

        switch (server){
            case UNDERTOW:
                builder.appendDependencyToModule("app-service",
                        "compile group: 'org.springframework.boot', name: 'spring-boot-starter-undertow', " +
                                "version: '" + Constants.UNDERTOW_VERSION + "'");
                builder.appendConfigurationToModule("app-service",
                        "compile.exclude group: \"org.springframework.boot\", module:\"spring-boot-starter-tomcat\"");
                return;
            case JETTY:
                builder.appendDependencyToModule("app-service",
                        "compile group: 'org.springframework.boot', name: 'spring-boot-starter-jetty', " +
                                "version: '" + Constants.JETTY_VERSION + "'");
                builder.appendConfigurationToModule("app-service",
                        "compile.exclude group: \"org.springframework.boot\", module:\"spring-boot-starter-tomcat\"");
                return;
            case TOMCAT:
                return;
            default:
                throw new InvalidTaskOptionException("Server option invalid");
        }
    }

    public enum Server {
        UNDERTOW, TOMCAT, JETTY
    }

}
