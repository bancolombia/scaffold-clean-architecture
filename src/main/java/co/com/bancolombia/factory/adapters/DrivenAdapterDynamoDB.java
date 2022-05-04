package co.com.bancolombia.factory.adapters;

import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.commons.ObjectMapperFactory;
import java.io.IOException;

public class DrivenAdapterDynamoDB implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    builder.addParam("reactive", builder.isReactive());
    String typePath = getPathType(builder.isReactive());

    builder.addAwsBom();
    builder.appendToSettings("dynamo-db", "infrastructure/driven-adapters");
    String dependency = buildImplementationFromProject(builder.isKotlin(), ":dynamo-db");
    builder.appendDependencyToModule("app-service", dependency);
    builder.setupFromTemplate("driven-adapter/" + typePath);
    builder.appendToProperties("aws.dynamodb").put("endpoint", "http://localhost:8000");
    builder.appendToProperties("aws.dynamodb").put("threads", "10");
    new ObjectMapperFactory().buildModule(builder);
  }

  protected String getPathType(boolean isReactive) {
    return isReactive ? "dynamo-db-reactive" : "dynamo-db";
  }
}
