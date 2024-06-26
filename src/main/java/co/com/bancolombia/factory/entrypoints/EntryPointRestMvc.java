package co.com.bancolombia.factory.entrypoints;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;
import static co.com.bancolombia.utils.Utils.buildTestImplementation;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.utils.swagger.Swagger;
import java.io.IOException;

public class EntryPointRestMvc implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    builder.setupFromTemplate("entry-point/rest-mvc");
    builder.appendToSettings("api-rest", "infrastructure/entry-points");
    builder.appendDependencyToModule(APP_SERVICE, buildImplementationFromProject(":api-rest"));
    // to run archunit validations
    builder.appendDependencyToModule(
        APP_SERVICE, buildTestImplementation("org.springframework:spring-web"));

    if (Boolean.TRUE.equals(builder.getBooleanParam("include-swagger"))) {
      builder.addParam("module", "api-rest");
      builder.setupFromTemplate("entry-point/swagger");
    }
    if (builder.withMetrics()) {
      builder
          .appendToProperties("management.endpoints.web.exposure")
          .put("include", "health,prometheus");
    } else {
      builder.appendToProperties("management.endpoints.web.exposure").put("include", "health");
    }

    if (Boolean.TRUE.equals(builder.getBooleanParam("task-param-authorize"))) {
      builder.setupFromTemplate("entry-point/rest-mvc/authorization");
      builder
          .appendToProperties("spring.security.oauth2.resourceserver.jwt")
          .put("issuer-uri", "https://idp.example.com/issuer");
      builder
          .appendToProperties("spring.security.oauth2.resourceserver.jwt")
          .put("client-id", "myclientid");
      builder.appendToProperties("jwt").put("json-exp-roles", "/roles");
    }
    builder.appendToProperties("management.endpoint.health.probes").put("enabled", true);
    builder
        .appendToProperties("cors")
        .put("allowed-origins", "http://localhost:4200,http://localhost:8080");
    new EntryPointRestMvcServer().buildModule(builder);
    Swagger.fromBuilder(builder, "infrastructure/entry-points/api-rest", true);
  }
}
