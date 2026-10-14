package co.com.bancolombia.factory.entrypoints;

import co.com.bancolombia.exceptions.CleanException;
import co.com.bancolombia.factory.ModuleBuilder;
import co.com.bancolombia.factory.ModuleFactory;
import co.com.bancolombia.factory.validations.ReactiveTypeValidation;
import java.io.IOException;

/**
 * Factory for the A2A Spring AI Agent entry-point.
 *
 * <p>Generates a full reactive agent skeleton with:
 *
 * <ul>
 *   <li>A2A domain models (SendMessageRequest, SendMessageResponse, Message, Task, Error,
 *       AgentCard)
 *   <li>ChatGateway and AgentResponseGateway ports
 *   <li>AgentChatUseCase with REST (chatAndRespond) and Kafka (chat) transport methods
 *   <li>WebFlux functional router exposing:
 *       <ul>
 *         <li>{@code POST /message:send}
 *         <li>{@code GET /.well-known/agent-card.json}
 *       </ul>
 *   <li>SpringAiChatAdapter (implements ChatGateway via Spring AI ChatClient) when MCP client is
 *       disabled
 *   <li>Optional Kafka consumer + producer (flag: agent-enable-kafka)
 * </ul>
 *
 * <p>Usage: {@code gradle generateEntryPoint --type=agent [--name=<agentName>]
 * [--agent-enable-kafka=true] [--agent-enable-mcp-client=true]}
 */
public class EntryPointAgent implements ModuleFactory {
  private static final String ROLE_HYBRID = "hybrid";

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    builder.runValidations(ReactiveTypeValidation.class);

    String agentRole = builder.getStringParam("agent-role");
    if (agentRole == null || agentRole.isBlank()) {
      agentRole = "collaborative";
    }

    boolean isCollaborative =
        agentRole.equalsIgnoreCase("collaborative") || agentRole.equalsIgnoreCase(ROLE_HYBRID);
    boolean isSupervisor =
        agentRole.equalsIgnoreCase("supervisor") || agentRole.equalsIgnoreCase(ROLE_HYBRID);
    boolean isHybrid = agentRole.equalsIgnoreCase(ROLE_HYBRID);

    builder.addParam("agent-role", agentRole);
    builder.addParam("agent-is-collaborative", isCollaborative);
    builder.addParam("agent-is-supervisor", isSupervisor);
    builder.addParam("agent-is-hybrid", isHybrid);

    boolean enableKafka = builder.getBooleanParam("agent-enable-kafka");

    // ── Generate base templates (always) ──────────────────────────────────
    builder.setupFromTemplate("entry-point/agent");

    if (isSupervisor) {
      builder.setupFromTemplate("entry-point/agent/spring-ai");
    }

    // ── Generate Kafka modules (always) ───────────────────────────────────
    builder.setupFromTemplate("entry-point/agent/kafka");

    // ── Generate MCP Client module (conditional) ──────────────────────────
    if (isCollaborative) {
      builder.setupFromTemplate("entry-point/agent/mcp-client");
      builder.setupFromTemplate("entry-point/agent/config");
    }

    // ── Add application.yaml properties ───────────────────────────────────
    String agentName = builder.getStringParam("task-param-name");
    if (agentName == null || agentName.isBlank()) {
      agentName = builder.getProjectName();
    }

    builder.appendToProperties("spring.application").put("name", agentName);
    builder.appendToProperties("spring.ai.openai").put("api-key", "${LLM_API_KEY:lm-studio}");
    builder
        .appendToProperties("spring.ai.openai")
        .put("base-url", "${LLM_URL:http://localhost:1234}");
    builder
        .appendToProperties("spring.ai.openai.chat.options")
        .put("model", "${LLM_MODEL:local-model}")
        .put("temperature", "0.0");

    builder
        .appendToProperties("agent")
        .put("id", agentName + "-id")
        .put("name", agentName)
        .put("description", "A2A Agent " + agentName)
        .put(
            "system-prompt",
            "You are a specialised agent called '"
                + agentName
                + "'. Use available MCP tools. Respond in JSON.");

    builder
        .appendToProperties("cors")
        .put("allowed-origins", "${CORS_ALLOWED_ORIGINS:http://localhost:4200}");

    if (isCollaborative) {
      builder
          .appendToProperties("spring.ai.mcp.client.streamable-http.connections.mcp-server-1")
          .put("url", "${MCP_SERVER_URL:http://localhost:8080}")
          .put("endpoint", "${MCP_SERVER_ENDPOINT:/mcp/stream}");
    }

    if (enableKafka) {
      builder
          .appendToProperties("adapters.kafka.consumer")
          .put("topic", "${KAFKA_CONSUMER_TOPIC:" + agentName + "-commands}");
      builder
          .appendToProperties("adapters.kafka.producer")
          .put("topic", "${KAFKA_PRODUCER_TOPIC:" + agentName + "-responses}");
      builder
          .appendToProperties("reactive.commons.kafka.app.connection-properties.security")
          .put("protocol", "${KAFKA_SECURITY_PROTOCOL:PLAINTEXT}");
      builder
          .appendToProperties("reactive.commons.kafka.app.connection-properties.consumer")
          .put("group-id", "${KAFKA_CONSUMER_GROUP_ID:" + agentName + "-group}");
    }
  }
}
