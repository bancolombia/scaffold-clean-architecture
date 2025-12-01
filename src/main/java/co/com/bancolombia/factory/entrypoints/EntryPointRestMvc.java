package co.com.bancolombia.factory.entrypoints;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;
import static co.com.bancolombia.utils.Utils.buildTestImplementation;

import co.com.bancolombia.VersioningStrategy;
import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.exceptions.ParamNotFoundException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.utils.swagger.Swagger;
import java.io.IOException;

public class EntryPointRestMvc implements ModuleFactory {

  private static void setupTemplate(ModuleBuilder builder, VersioningStrategy versioningStrategy)
      throws IOException, ParamNotFoundException {
    String templatePath = "entry-point/rest-mvc";
    switch (versioningStrategy) {
      case HEADER:
        templatePath += "/header";
        builder.addParam("task-param-versioning-strategy-header", Boolean.TRUE);
        builder.addParam("task-param-versioning-strategy-source", Boolean.TRUE);
        break;
      case PATH:
        templatePath += "/path";
        builder.addParam("task-param-versioning-strategy-path", Boolean.TRUE);
        builder.addParam("task-param-versioning-strategy-source", Boolean.TRUE);
        break;
      case NONE:
      default:
        break;
    }
    builder.setupFromTemplate(templatePath);
  }

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    VersioningStrategy versioningStrategy =
        (VersioningStrategy) builder.getParam("task-param-versioning-strategy");
    setupTemplate(builder, versioningStrategy);
    builder.setupFromTemplate("entry-point/rest-mvc");
    builder.appendToSettings("api-rest", "infrastructure/entry-points");
    builder.appendDependencyToModule(APP_SERVICE, buildImplementationFromProject(":api-rest"));
    // to run archunit validations
    builder.appendDependencyToModule(
        APP_SERVICE, buildTestImplementation("org.springframework:spring-web"));

    if (builder.getBooleanParam("include-swagger")) {
      builder.addParam("module", "api-rest");
      builder
          .appendToProperties("springdoc")
          .put("swagger-ui.path", "/v3/swagger-ui.html")
          .put("api-docs.path", "/v3/api-docs")
          .put("show-actuator", true);
    }
    if (builder.withMetrics()) {
      builder
          .appendToProperties("management.endpoints.web.exposure")
          .put("include", "health,prometheus");
    } else {
      builder.appendToProperties("management.endpoints.web.exposure").put("include", "health");
    }

    if (builder.getBooleanParam("task-param-authorize")) {
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
