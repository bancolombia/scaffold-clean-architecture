package co.com.bancolombia.factory.entrypoints;

import static co.com.bancolombia.Constants.APP_SERVICE;
import static co.com.bancolombia.utils.Utils.buildImplementationFromProject;

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

  @Override
  public void buildModule(ModuleBuilder builder) throws IOException, CleanException {
    builder.runValidations(ReactiveTypeValidation.class);

    final String ENTRY_POINT_DIR = "infrastructure/entry-points";
    final String DRIVEN_ADAPTER_DIR = "infrastructure/driven-adapters";

    boolean enableKafka = builder.getBooleanParam("agent-enable-kafka");
    boolean enableMcpClient = builder.getBooleanParam("agent-enable-mcp-client");

    // ── Generate base templates (always) ──────────────────────────────────
    builder.setupFromTemplate("entry-point/agent");

    if (!enableMcpClient) {
      builder.setupFromTemplate("entry-point/agent/spring-ai");
    }

    // ── Generate Kafka modules (conditional) ──────────────────────────────
    if (enableKafka) {
      builder.setupFromTemplate("entry-point/agent/kafka");
    }

    // ── Generate MCP Client module (conditional) ──────────────────────────
    if (enableMcpClient) {
      builder.setupFromTemplate("entry-point/agent/mcp-client");
      builder.setupFromTemplate("entry-point/agent/config");
    }

    // ── Register modules in settings.gradle ───────────────────────────────
    builder.appendToSettings("reactive-web", ENTRY_POINT_DIR);

    if (!enableMcpClient) {
      // Only add spring-ai-adapter if MCP client is NOT enabled
      // (ChatGatewayAdapter in mcp-client replaces SpringAiChatAdapter)
      builder.appendToSettings("spring-ai-adapter", DRIVEN_ADAPTER_DIR);
    }

    if (enableKafka) {
      builder.appendToSettings("kafka-consumer", ENTRY_POINT_DIR);
      builder.appendToSettings("kafka-producer", DRIVEN_ADAPTER_DIR);
    }

    if (enableMcpClient) {
      builder.appendToSettings("mcp-client", DRIVEN_ADAPTER_DIR);
    }

    // ── Wire dependencies into app-service ────────────────────────────────
    builder.appendDependencyToModule(APP_SERVICE, buildImplementationFromProject(":reactive-web"));

    if (!enableMcpClient) {
      builder.appendDependencyToModule(
          APP_SERVICE, buildImplementationFromProject(":spring-ai-adapter"));
    }

    builder.appendDependencyToModule(
        APP_SERVICE, "implementation 'org.springframework.boot:spring-boot-starter-webflux'");
    builder.appendDependencyToModule(
        APP_SERVICE, "implementation 'org.springframework.boot:spring-boot-starter-actuator'");

    if (enableKafka) {
      builder.appendDependencyToModule(
          APP_SERVICE, buildImplementationFromProject(":kafka-consumer"));
      builder.appendDependencyToModule(
          APP_SERVICE, buildImplementationFromProject(":kafka-producer"));
    }

    if (enableMcpClient) {
      builder.appendDependencyToModule(APP_SERVICE, buildImplementationFromProject(":mcp-client"));
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

    if (enableMcpClient) {
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
          .appendToProperties("spring.kafka.consumer")
          .put("bootstrap-servers", "${KAFKA_SERVERS:localhost:9092}")
          .put("group-id", agentName + "-group");
      builder
          .appendToProperties("spring.kafka.producer")
          .put("bootstrap-servers", "${KAFKA_SERVERS:localhost:9092}");
    }
  }
}
