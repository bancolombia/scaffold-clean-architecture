package co.com.bancolombia.factory.entrypoints;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.validations.ReactiveTypeValidation;
import java.io.IOException;

public class EntryPointMcp implements ModuleFactory {

  private static final String SPRING_AI_MCP_SERVER = "spring.ai.mcp.server";
  private static final String SPRING_AI_MCP_SERVER_CAPABILITIES =
      "spring.ai.mcp.server.capabilities";

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    builder.runValidations(ReactiveTypeValidation.class);
    // Get MCP configuration parameters
    boolean enableTools = builder.getBooleanParam("mcp-enable-tools");
    boolean enableResources = builder.getBooleanParam("mcp-enable-resources");
    boolean enablePrompts = builder.getBooleanParam("mcp-enable-prompts");

    // Validate that at least one capability is enabled
    if (!enableTools && !enableResources && !enablePrompts) {
      throw new IllegalArgumentException(
          "At least one MCP capability (tools, resources, prompts) must be enabled");
    }

    // Setup module from templates
    builder.setupFromTemplate("entry-point/mcp");

    // Add to settings.gradle
    builder.appendToSettings("mcp-server", "infrastructure/entry-points");

    // Add dependency to app-service
    builder.appendDependencyToModule(APP_SERVICE, buildImplementationFromProject(":mcp-server"));

    // Add MCP configuration to app-service application.yaml
    addMcpConfigToAppService(builder, enableTools, enableResources, enablePrompts);
  }

  private void addMcpConfigToAppService(
      ModuleBuilder builder, boolean enableTools, boolean enableResources, boolean enablePrompts)
      throws IOException {
    String serverName = builder.getStringParam("task-param-name");
    String applicationName = serverName != null ? serverName : "${spring.application.name}";

    // Protocol and Type (STATELESS and ASYNC for reactive servers)
    builder.appendToProperties(SPRING_AI_MCP_SERVER).put("protocol", "STATELESS");
    builder.appendToProperties(SPRING_AI_MCP_SERVER).put("name", applicationName);
    builder.appendToProperties(SPRING_AI_MCP_SERVER).put("version", "1.0.0");
    builder.appendToProperties(SPRING_AI_MCP_SERVER).put("type", "ASYNC");

    // Instructions
    String instructions =
        """
            Reactive MCP Server with capabilities:
            - Tools: Executable tools
            - Resources: Access to system and user data
            - Prompts: Custom conversation templates

            Security: Requires authentication via API Key (Header: X-API-Key)""";
    builder.appendToProperties(SPRING_AI_MCP_SERVER).put("instructions", instructions);

    // Streamable HTTP endpoint
    builder
        .appendToProperties(SPRING_AI_MCP_SERVER + ".streamable-http")
        .put("mcp-endpoint", "/mcp/" + applicationName);

    // Capabilities
    builder.appendToProperties(SPRING_AI_MCP_SERVER_CAPABILITIES).put("tool", enableTools);
    builder.appendToProperties(SPRING_AI_MCP_SERVER_CAPABILITIES).put("resource", enableResources);
    builder.appendToProperties(SPRING_AI_MCP_SERVER_CAPABILITIES).put("prompt", enablePrompts);
    builder.appendToProperties(SPRING_AI_MCP_SERVER_CAPABILITIES).put("completion", false);

    // Request timeout
    builder.appendToProperties(SPRING_AI_MCP_SERVER).put("request-timeout", "30s");
  }
}
