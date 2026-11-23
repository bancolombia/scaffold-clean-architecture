package co.com.bancolombia.utils.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

public record OpenApiSpecContext(SwaggerParseResult parseResult, String specContent) {
  public OpenAPI openAPI() {
    return parseResult.getOpenAPI();
  }
}
