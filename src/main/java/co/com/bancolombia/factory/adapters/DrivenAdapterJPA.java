package co.com.bancolombia.factory.adapters;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.commons.ObjectMapperFactory;
import java.io.IOException;

public class DrivenAdapterJPA implements ModuleFactory {
  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    builder.setupFromTemplate("driven-adapter/jpa-repository");
    builder.appendToSettings("jpa-repository", "infrastructure/driven-adapters");
    builder
        .appendToProperties("spring.datasource")
        .put("url", "jdbc:h2:mem:test")
        .put("username", "sa")
        .put("password", "pass")
        .put("driverClassName", "org.h2.Driver");
    builder
        .appendToProperties("spring.jpa")
        .put("databasePlatform", "org.hibernate.dialect.H2Dialect");
    builder.appendDependencyToModule("app-service", "implementation project(':jpa-repository')");
    if (builder.getBooleanParam("include-secret")) {
      new DrivenAdapterSecrets().buildModule(builder);
    }
    new ObjectMapperFactory().buildModule(builder);
  }
}
