package co.com.bancolombia.factory.adapters;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.commons.GenericModule;
import co.com.bancolombia.factory.commons.ObjectMapperFactory;
import java.io.IOException;

public class DrivenAdapterDynamoDB implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    builder.addParam("reactive", builder.isReactive());
    String typePath = getPathType(builder.isReactive());

    GenericModule.addAwsBom(builder);
    builder.setupFromTemplate("driven-adapter/" + typePath);
    builder.appendToProperties("aws.dynamodb").put("endpoint", "http://localhost:8000");
    new ObjectMapperFactory().buildModule(builder);
  }

  protected String getPathType(boolean isReactive) {
    return isReactive ? "dynamo-db-reactive" : "dynamo-db";
  }
}
