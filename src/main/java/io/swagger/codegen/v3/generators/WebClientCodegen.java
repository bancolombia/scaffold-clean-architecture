package io.swagger.codegen.v3.generators;

public class WebClientCodegen extends AbstractScaffoldCodegen {

  public WebClientCodegen() {
    super();
  }

  @Override
  public String getName() {
    return "WebFlux";
  }

  @Override
  public String getHelp() {
    return "Generates an entrypoint rest with WebFlux.";
  }

  @Override
  public void processOpts() {
    super.processOpts();
    apiTemplateFiles.put("apiClientReactive.mustache", ".java");
  }
}
