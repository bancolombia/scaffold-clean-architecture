package co.com.bancolombia.factory.entrypoints;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.Utils.*;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.exceptions.ParamNotFoundException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.validations.ReactiveTypeValidation;
import co.com.bancolombia.utils.swagger.Swagger;
import java.io.IOException;

public class EntryPointWebflux implements ModuleFactory {
  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    VersioningStrategy versioningStrategy =
        (VersioningStrategy) builder.getParam("task-param-versioning-strategy");

    builder.runValidations(ReactiveTypeValidation.class);
    if (Boolean.TRUE.equals(builder.getBooleanParam("task-param-router"))) {
      setupTemplate(builder, versioningStrategy);
    } else {
      builder.setupFromTemplate("entry-point/rest-webflux");
      // to run archunit validations
      builder.appendDependencyToModule(
          APP_SERVICE, buildTestImplementation("org.springframework:spring-web"));
    }
    if (Boolean.TRUE.equals(builder.getBooleanParam("include-swagger"))) {
      builder.addParam("module", "reactive-web");
      builder
          .appendToProperties("springdoc")
          .put("swagger-ui.path", "/v3/swagger-ui.html")
          .put("api-docs.path", "/v3/api-docs")
          .put("show-actuator", true);
    }

    if (Boolean.TRUE.equals(builder.getBooleanParam("task-param-authorize"))) {
      builder.setupFromTemplate("entry-point/rest-webflux/authorization");
      builder
          .appendToProperties("spring.security.oauth2.resourceserver.jwt")
          .put("issuer-uri", "https://idp.example.com/issuer");
      builder
          .appendToProperties("spring.security.oauth2.resourceserver.jwt")
          .put("client-id", "myclientid");
      builder.appendToProperties("jwt").put("json-exp-roles", "/roles");
    }

    Swagger.fromBuilder(builder, "infrastructure/entry-points/reactive-web", true);

    builder.appendToSettings("reactive-web", "infrastructure/entry-points");
    String dependency = buildImplementationFromProject(":reactive-web");
    builder.appendDependencyToModule(APP_SERVICE, dependency);
    if (builder.withMetrics()) {
      builder
          .appendToProperties("management.endpoints.web.exposure")
          .put("include", "health,prometheus");
    } else {
      builder.appendToProperties("management.endpoints.web.exposure").put("include", "health");
    }
    builder.appendToProperties("management.endpoint.health.probes").put("enabled", true);
    builder
        .appendToProperties("cors")
        .put("allowed-origins", "http://localhost:4200,http://localhost:8080");
  }

  private static void setupTemplate(ModuleBuilder builder, VersioningStrategy versioningStrategy)
      throws IOException, ParamNotFoundException {
    String templatePath = "entry-point/rest-webflux/router-functions";

    switch (versioningStrategy) {
      case HEADER:
        templatePath += "/header";
        break;
      case PATH:
        templatePath += "/path";
        break;
      case NONE:
      default:
        break;
    }

    builder.setupFromTemplate(templatePath);
  }

  public enum VersioningStrategy {
    HEADER,
    PATH,
    NONE
  }
}
