package co.com.bancolombia.factory.adapters;

import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import java.io.IOException;

public class DrivenAdapterDynamoDB implements ModuleFactory {

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    builder.appendToSettings("dynamo-db", "infrastructure/driven-adapters");
    String dependency = buildImplementationFromProject(builder.isKotlin(), ":dynamo-db");
    builder.appendDependencyToModule("app-service", dependency);
    builder.setupFromTemplate("driven-adapter/dynamo-db");
    builder.appendToProperties("aws").put("access-key", "").put("secret-key", "");
    builder.appendToProperties("aws.dynamodb").put("endpoint", "http://localhost:8000");
  }
}
