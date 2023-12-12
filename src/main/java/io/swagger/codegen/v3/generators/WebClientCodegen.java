package io.swagger.codegen.v3.generators;

public class WebClientCodegen extends AbstractScaffoldCodegen {

  public WebClientCodegen() {
    super();
  }

  @Override
  public String getName() {
    return "WebClient";
  }

  @Override
  public String getHelp() {
    return "Generates an entrypoint rest with WebClient.";
  }

  @Override
  public void processOpts() {
    super.processOpts();
    apiTemplateFiles.put("apiClientReactive.mustache", ".java");
  }
}
