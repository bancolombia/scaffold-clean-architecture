package io.swagger.codegen.v3.generators;

import io.swagger.codegen.v3.CodegenType;

public class RestControllerCodegen extends AbstractScaffoldCodegen {

  public RestControllerCodegen() {
    super();
  }

  @Override
  public CodegenType getTag() {
    return CodegenType.CLIENT;
  }

  @Override
  public String getName() {
    return "WebClient";
  }

  @Override
  public String getHelp() {
    return "Generates a driven adapter rest with WebClient.";
  }

  @Override
  public void processOpts() {
    super.processOpts();
    apiTemplateFiles.put("apiController.mustache", "Controller.java");
  }
}
