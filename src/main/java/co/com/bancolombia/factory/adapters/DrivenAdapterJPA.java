package co.com.bancolombia.factory.adapters;

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
    builder
        .appendToProperties("spring.datasource")
        .put("url", "jdbc:h2:mem:test")
        .put("username", "sa")
        .put("password", "")
        .put("driverClassName", "org.h2.Driver");
    builder
        .appendToProperties("spring.jpa")
        .put("databasePlatform", "org.hibernate.dialect.H2Dialect");
    // The H2 console is servlet-only, so it is only configured for imperative projects
    if (!builder.isReactive()) {
      builder.appendToProperties("spring.h2.console").put("enabled", true).put("path", "/h2");
    }

    new ObjectMapperFactory().buildModule(builder);
  }
}
