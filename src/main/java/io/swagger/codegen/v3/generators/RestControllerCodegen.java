package io.swagger.codegen.v3.generators;

import io.swagger.codegen.v3.CodegenType;

public class RestControllerCodegen extends AbstractScaffoldCodegen {

  public RestControllerCodegen() {
    super();
  }

  @Override
  public CodegenType getTag() {
    return CodegenType.SERVER;
  }

  @Override
  public String getName() {
    return "RestController";
  }

  @Override
  public String getHelp() {
    return "Generates a entry point rest with RestController.";
  }

  @Override
  public void processOpts() {
    super.processOpts();
    apiTemplateFiles.put("apiController.mustache", "Controller.java");
  }
}
