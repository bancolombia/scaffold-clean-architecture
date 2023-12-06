package co.com.bancolombia.factory.entrypoints;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.validations.ReactiveTypeValidation;
import co.com.bancolombia.utils.swagger.Swagger;
import java.io.IOException;

public class EntryPointWebflux implements ModuleFactory {
  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    builder.runValidations(ReactiveTypeValidation.class);
    if (Boolean.TRUE.equals(builder.getBooleanParam("task-param-router"))) {
      builder.setupFromTemplate("entry-point/rest-webflux/router-functions");
    } else {
      builder.setupFromTemplate("entry-point/rest-webflux");
      if (Boolean.TRUE.equals(builder.getBooleanParam("include-swagger"))) {
        builder.addParam("module", "reactive-web");
        builder.setupFromTemplate("entry-point/swagger");
        if (builder.isKotlin()) {
          builder
              .appendToProperties("spring.mvc.pathmatch")
              .put("matching-strategy", "ant_path_matcher");
        }
      }
    }
    Swagger.fromBuilder(builder, "infrastructure/entry-points/reactive-web");
    builder.appendToSettings("reactive-web", "infrastructure/entry-points");
    String dependency = buildImplementationFromProject(builder.isKotlin(), ":reactive-web");
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
}
