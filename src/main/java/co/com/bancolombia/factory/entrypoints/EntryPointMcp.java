package co.com.bancolombia.factory.entrypoints;

import static co.com.bancolombia.Constants.MainFiles.APP_BUILD_GRADLE;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.validations.ReactiveTypeValidation;
import java.io.IOException;
import tools.jackson.databind.node.ObjectNode;

public class EntryPointMcp implements ModuleFactory {

  private static final String SPRING_AI_MCP_SERVER = "spring.ai.mcp.server";
  private static final String SPRING_AI_MCP_SERVER_CAPABILITIES =
      "spring.ai.mcp.server.capabilities";
  private static final String ALLOWED_ORIGINS = "allowed-origins";
  private static final String MCP_INSPECTOR_ORIGIN = "http://localhost:6274";
  private static final String JAVA_COMPILE_PARAMETERS_SENTINEL =
      "options.compilerArgs += '-parameters'";
  private static final String JAVA_COMPILE_PARAMETERS_BLOCK =
      String.join(
          "\n",
          "",
          "    doFirst {",
          "        if (!options.compilerArgs.contains('-parameters')) {",
          "            options.compilerArgs += '-parameters'",
          "        }",
          "    }");
  private static final String JAVA_COMPILE_OPTIONS_REGEX =
      "(options\\.compilerArgs\\s*=\\s*\\[[^]]*])";

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    builder.runValidations(ReactiveTypeValidation.class);
    boolean enableTools = builder.getBooleanParam("mcp-enable-tools");
    boolean enableResources = builder.getBooleanParam("mcp-enable-resources");
    boolean enablePrompts = builder.getBooleanParam("mcp-enable-prompts");
    boolean enableSecurity = builder.getBooleanParam("mcp-enable-security");
    boolean enableAudit = builder.getBooleanParam("mcp-enable-audit");

    // Validate that at least one capability is enabled
    if (!enableTools && !enableResources && !enablePrompts) {
      throw new IllegalArgumentException(
          "At least one MCP capability (tools, resources, prompts) must be enabled");
    }

    // Setup module from templates
    builder.setupFromTemplate("entry-point/mcp");
    if (enableSecurity) {
      builder.setupFromTemplate("entry-point/mcp/security");
    }
    if (enableAudit) {
      builder.setupFromTemplate("entry-point/mcp/audit");
    }

    // Add MCP configuration to app-service application.yaml
    addMcpConfigToAppService(builder, enableTools, enableResources, enablePrompts, enableSecurity);
  }

  private void addMcpConfigToAppService(
      ModuleBuilder builder,
      boolean enableTools,
      boolean enableResources,
      boolean enablePrompts,
      boolean enableSecurity)
      throws IOException {
    String serverName = builder.getStringParam("task-param-name");
    String applicationName = serverName != null ? serverName : "${spring.application.name}";

    // Protocol and Type (STATELESS and ASYNC for reactive servers)
    builder.appendToProperties(SPRING_AI_MCP_SERVER).put("protocol", "STATELESS");
    builder.appendToProperties(SPRING_AI_MCP_SERVER).put("name", applicationName);
    builder.appendToProperties(SPRING_AI_MCP_SERVER).put("version", "1.0.0");
    builder.appendToProperties(SPRING_AI_MCP_SERVER).put("type", "ASYNC");

    // Instructions
    String securityInstruction =
        enableSecurity
            ? "Security: Authenticated via Entra ID (Bearer Token)"
            : "Security: None (Development Mode)";

    String instructions =
        """
                Reactive MCP Server with capabilities:
                - Tools: Executable tools
                - Resources: Access to system and user data
                - Prompts: Custom conversation templates

                %s"""
            .formatted(securityInstruction);
    builder.appendToProperties(SPRING_AI_MCP_SERVER).put("instructions", instructions);

    if (enableSecurity) {
      // Add placeholders for Entra ID configuration
      builder
          .appendToProperties("spring.security.oauth2.resourceserver.jwt")
          .put("issuer-uri", "https://login.microsoftonline.com/${TENANT_ID}/v2.0");
      builder
          .appendToProperties("spring.security.oauth2.resourceserver.jwt")
          .put("client-id", "${CLIENT_ID}");
      builder.appendToProperties("jwt").put("json-exp-roles", "/roles");
    }

    // Streamable HTTP endpoint
    builder
        .appendToProperties(SPRING_AI_MCP_SERVER + ".streamable-http")
        .put("mcp-endpoint", "/mcp/" + applicationName);

    // Capabilities
    builder.appendToProperties(SPRING_AI_MCP_SERVER_CAPABILITIES).put("tool", enableTools);
    builder.appendToProperties(SPRING_AI_MCP_SERVER_CAPABILITIES).put("resource", enableResources);
    builder.appendToProperties(SPRING_AI_MCP_SERVER_CAPABILITIES).put("prompt", enablePrompts);
    builder.appendToProperties(SPRING_AI_MCP_SERVER_CAPABILITIES).put("completion", false);

    addCorsAllowedOrigins(builder);

    // Request timeout
    builder.appendToProperties(SPRING_AI_MCP_SERVER).put("request-timeout", "30s");

    // Add -parameters flag for JavaCompile to expose param names for MCP Tools
    builder.updateFile(
        APP_BUILD_GRADLE,
        content -> {
          if (content.contains(JAVA_COMPILE_PARAMETERS_SENTINEL)) {
            return content;
          }
          return content.replaceAll(
              JAVA_COMPILE_OPTIONS_REGEX, "$1" + JAVA_COMPILE_PARAMETERS_BLOCK);
        });
  }

  // cors.allowed-origins is shared with the other web entry points, so keep any existing value
  private static void addCorsAllowedOrigins(ModuleBuilder builder) {
    ObjectNode cors = builder.appendToProperties("cors");
    if (!cors.has(ALLOWED_ORIGINS)) {
      cors.put(ALLOWED_ORIGINS, MCP_INSPECTOR_ORIGIN);
    }
  }
}
