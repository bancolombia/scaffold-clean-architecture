package io.swagger.codegen.v3.generators;

public class WebFluxRouterCodegen extends AbstractScaffoldCodegen {

  public WebFluxRouterCodegen() {
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
    apiTemplateFiles.put("apiHandler.mustache", "Handler.java");
    apiTemplateFiles.put("apiRouter.mustache", "Router.java");
  }
}
