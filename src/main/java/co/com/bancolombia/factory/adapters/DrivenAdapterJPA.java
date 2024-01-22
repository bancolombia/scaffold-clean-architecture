package co.com.bancolombia.factory.adapters;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.commons.ObjectMapperFactory;
import java.io.IOException;

public class DrivenAdapterJPA implements ModuleFactory {
  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {

    builder.setUpSecretsInAdapter();

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
    String dependency = buildImplementationFromProject(builder.isKotlin(), ":jpa-repository");
    builder.appendDependencyToModule(APP_SERVICE, dependency);

    new ObjectMapperFactory().buildModule(builder);
  }
}
