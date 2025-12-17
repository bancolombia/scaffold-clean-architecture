package io.swagger.codegen.v3.generators;

public class RestConsumerCodegen extends AbstractScaffoldCodegen {

  public RestConsumerCodegen() {
    super();
  }

  @Override
  public String getName() {
    return "RestConsumer";
  }

  @Override
  public String getHelp() {
    return "Generates an entrypoint rest with okhttp.";
  }

  @Override
  public void processOpts() {
    super.processOpts();
    apiTemplateFiles.put("api-client-imperative.mustache", ".java");
  }
}
