---
sidebar_position: 6
---

# Generate Entry Point

The **`generateEntryPoint | gep`** task will generate a module in Infrastructure layer, this task has one required
parameter `type`. <br/>
Whether you'll use generic one also parameter `name` is required.

```shell
gradle generateEntryPoint --type=[entryPointType]
gradle gep --type [entryPointType]
```

| Type                  | Name                                   | Parameter            | Values                                | Default                |
|-----------------------|----------------------------------------|----------------------|---------------------------------------|------------------------|
| **generic**           | Empty Entry Point                      | `--name`             | String                                | -                      |
| **asynceventhandler** | Async Event Handler                    | `--eda`              | `true`, `false`                       | `false`                |
|                       |                                        | `--tech`             | `rabbitmq`, `kafka`, `rabbitmq,kafka` | `rabbitmq`             |
| **graphql**           | API GraphQL                            | `--pathgql`          | String (path)                         | `/graphql`             |
| **kafka**             | Kafka Consumer                         | -                    | -                                     | -                      |
| **mcp**               | MCP Server (Model Context Protocol)    | `--name`             | String                                | -                      |
|                       |                                        | `--enable-tools`     | `true`, `false`                       | `true`                 |
|                       |                                        | `--enable-resources` | `true`, `false`                       | `true`                 |
|                       |                                        | `--enable-prompts`   | `true`, `false`                       | `true`                 |
|                       |                                        | `--enable-security`  | `true`, `false`                       | `true`                 |
|                       |                                        | `--enable-audit`     | `true`, `false`                       | `true`                 |
| **mq**                | JMS MQ Client to listen messages       | -                    | -                                     | -                      |
| **restmvc**           | API REST (Spring Boot Starter Webmvc)   | `--server`           | `tomcat`, `jetty`                     | `tomcat`               |
|                       |                                        | `--authorization`    | `true`, `false`                       | `false`                |
|                       |                                        | `--versioning`       | `HEADER`, `PATH`, `NONE`              | `NONE`                 | 
|                       |                                        | `--from-swagger`     | File path                             | `swagger.yaml`         |
|                       |                                        | `--swagger`          | `true`, `false`                       | `false`                |
| **rsocket**           | Rsocket Controller Entry Point         | -                    | -                                     | -                      |
| **sqs**               | SQS Listener                           | -                    | -                                     | -                      |
| **webflux**           | API REST (Spring Boot Starter WebFlux) | `--router`           | `true`, `false`                       | `true`                 |
|                       |                                        | `--authorization`    | `true`, `false`                       | `false`                |
|                       |                                        | `--versioning`       | `HEADER`, `PATH`, `NONE`              | `NONE`                 |
|                       |                                        | `--from-swagger`     | File path                             | `swagger.yaml`         |
|                       |                                        | `--swagger`          | `true`, `false`                       | `false`                |
| **kafkastrimzi**      | Kafka Strimzi Consumer Entry Point     | `--name`             | String                                | -                      |
|                       |                                        | `--topic-consumer`  | String (topic name)                   | `test-with-registries` |
| **agent**             | Spring AI A2A Reactive Agent           | `--name`             | String                                | project name           |
|                       |                                        | `--agent-enable-kafka` | `true`, `false`                     | `true`                 |
|                       |                                        | `--agent-enable-mcp-client` | `true`, `false`                | `true`                 |

Additionally, if you'll use a `restmvc`, you can specify the web server on which the application will run. By default,
Tomcat.

| Reference for **serverOption** | Name                    |
|--------------------------------|-------------------------|
| tomcat                         | Tomcat server (default) |
| jetty                          | Jetty server            |

```shell
gradle generateEntryPoint --type=restmvc --server=[serverOption]
gradle gep --type=restmvc --server=[serverOption]
```

_**This task will generate something like that:**_

   ```bash
   📦infrastructure
   ┣ 📂entry-points
   ┃ ┗ 📂generic
   ┃ ┃ ┣ 📂src
   ┃ ┃ ┃ ┣ 📂main
   ┃ ┃ ┃ ┃ ┗ 📂java
   ┃ ┃ ┃ ┃ ┃ ┗ 📂[package]
   ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂generic
   ┃ ┃ ┃ ┗ 📂test
   ┃ ┃ ┃ ┃ ┗ 📂java
   ┃ ┃ ┃ ┃ ┃ ┗ 📂[package]
   ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂generic
   ┃ ┃ ┗ 📜build.gradle
   ```

## Usage Example for Kafka Strimzi Consumer

```shell
gradle generateEntryPoint --type=kafkastrimzi 
gradle gep --type=kafkastrimzi
```

```shell
gradle generateEntryPoint --type=kafkastrimzi --name=myConsumer --topic-consumer=myTopic
gradle gep --type=kafkastrimzi --name=myConsumer --topic-consumer=myTopic
```

This will generate a specialized entry point for consuming Kafka messages using Strimzi, with custom parameters.

## Usage Example for MCP (Model Context Protocol)

The **`mcp`** entry point type generates a reactive MCP server with Tools, Resources, and Prompts capabilities, based on
Spring AI.

### Basic Command

```shell
gradle generateEntryPoint --type=mcp
gradle gep --type=mcp
```

### Available Parameters

| Parameter            | Values         | Default | Description                          |
|----------------------|----------------|---------|--------------------------------------|
| `--name`             | String         | `null`  | MCP Server Name                      |
| `--enable-tools`     | `true`/`false` | `true`  | Enable Tools                         |
| `--enable-resources` | `true`/`false` | `true`  | Enable Resources                     |
| `--enable-prompts`   | `true`/`false` | `true`  | Enable Prompts                       |
| `--enable-security`  | `true`/`false` | `true`  | Enable OAuth2/Entra ID Security      |
| `--enable-audit`     | `true`/`false` | `true`  | Enable Audit Logging (requires AOP)  |

### Usage Examples

```shell
# Generate with all capabilities enabled (default: includes security and audit)
gradle generateEntryPoint --type=mcp

# Only Tools
gradle generateEntryPoint --type=mcp --enable-tools=true --enable-resources=false --enable-prompts=false

# Only Resources
gradle generateEntryPoint --type=mcp --enable-tools=false --enable-resources=true --enable-prompts=false

# With custom name
gradle generateEntryPoint --type=mcp --name=BancolombiaAssistant

# Without security (development mode only)
gradle generateEntryPoint --type=mcp --enable-security=false

# Without audit logging
gradle generateEntryPoint --type=mcp --enable-audit=false
```

### Generated Structure

```bash
infrastructure/
└── entry-points/
    └── mcp-server/
        ├── build.gradle
        └── src/
            ├── main/java/[package]/mcp/
            │   ├── tools/
            │   │   ├── HealthTool.java
            │   │   └── ExampleTool.java
            │   ├── resources/
            │   │   ├── SystemInfoResource.java
            │   │   └── UserInfoResource.java
            │   ├── prompts/
            │   │   └── ExamplePrompt.java
            │   └── audit/                      # If audit enabled
            │       └── McpAuditAspect.java
            └── test/java/[package]/mcp/
                ├── tools/
                ├── resources/
                ├── prompts/
                └── audit/                      # If audit enabled
                    └── McpAuditAspectTest.java

applications/
└── app-service/
    └── src/
        ├── main/java/co/com/bancolombia/config/
        │   └── McpSecurityConfig.java         # If security enabled
        └── test/java/co/com/bancolombia/config/
            └── McpSecurityConfigTest.java     # If security enabled
```

### Automatic Configuration

The command also automatically updates `application.yaml` with the MCP configuration:

```yaml
spring:
  ai:
    mcp:
      server:
        protocol: "STATELESS"
        name: "${spring.application.name}"
        version: "1.0.0"
        type: "ASYNC"
        instructions: |
          Reactive MCP Server with capabilities:
          - Tools: Executable tools
          - Resources: Access to system and user data
          - Prompts: Custom conversation templates
          
          Security: Authenticated via Entra ID (Bearer Token)
        streamable-http:
          mcp-endpoint: "/mcp/${spring.application.name}"
        capabilities:
          tool: true
          resource: true
          prompt: true
        request-timeout: "30s"
  # Security configuration (if --enable-security=true)
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://login.microsoftonline.com/${TENANT_ID}/v2.0
          client-id: ${CLIENT_ID}

jwt:
  json-exp-roles: /roles  # JSON path for role extraction
```

### Security and Audit Features

**By default, the MCP entry point is generated with security and audit enabled.**

#### Security (`McpSecurityConfig`)

When `--enable-security=true` (default), the generator creates a complete OAuth2/Entra ID security configuration:

- **OAuth2 Resource Server**: Validates JWT tokens from Entra ID
- **JWT Validation**: Validates audience (`aud`), app ID (`appid`), and issuer
- **Role Extraction**: Extracts roles from JWT claims using configurable JSON path
- **Method Security**: Enables `@PreAuthorize` annotations for RBAC
- **Public Endpoints**: Actuator health and info endpoints remain public
- **Access Denied Logging**: Explicitly logs security rejections with user and path details

#### Audit Logging (`McpAuditAspect`)

When `--enable-audit=true` (default), the generator creates an AOP aspect that automatically logs all MCP operations:

- **What is audited**: All calls to `@Tool`, `@McpResource`, and `@McpPrompt` methods
- **Information logged**:
  - **Who**: Client ID extracted from JWT token (`appid`, `azp`, or `aud`)
  - **What**: Class, method name, and arguments
  - **When**: Timestamp (automatic via logging framework)
  - **Result**: Success or failure
  - **Performance**: Execution time in milliseconds
- **Reactive Support**: Integrates seamlessly with `Mono` return types
- **Security Context**: Extracts authentication details from `ReactiveSecurityContextHolder`

**Example audit log output:**
```
📊 [AUDIT] TOOL llamado por: a1b2c3d4-client-id | Método: ExampleTool.echo | Args: ["hello"]
✅ [AUDIT] TOOL exitoso | Client: a1b2c3d4-client-id | Método: ExampleTool.echo | Tiempo: 45ms
```

### Component Development

#### Create a Tool

```java

@Component
public class CalculatorTool {
    @McpTool(name = "multiply", description = "Multiplies two numbers")
    public Mono<Integer> multiply(
            @McpToolParam(description = "First number", required = true) int a,
            @McpToolParam(description = "Second number", required = true) int b) {
        return Mono.just(a * b);
    }
}
```

#### Create a Resource

```java

@Component
public class ConfigResource {
    @McpResource(
            uri = "resource://config/app",
            name = "app-config",
            description = "Application configuration")
    public Mono<ReadResourceResult> getConfig() {
        return Mono.fromCallable(() -> {
            // Implementation
        });
    }
}
```

#### Create a Prompt

```java

@Component
public class SupportPrompt {
    @McpPrompt(
            name = "customer-support",
            description = "Generates a customer support prompt")
    public Mono<GetPromptResult> customerSupport(
            @McpArg(name = "issue", required = true) String issue) {
        return Mono.fromCallable(() -> {
            // Implementation
        });
    }
}
```

---

## Usage Example for Agent (Spring AI A2A)

The **`agent`** entry point type generates a full reactive **A2A (Agent-to-Agent)** skeleton based on Spring AI and
WebFlux. It exposes a REST endpoint (`POST /a2a/v1/commands`) and optionally Kafka transport, using the **A2A protocol**
with correlation and trace identifiers for observability.

### Basic Command

```shell
gradle generateEntryPoint --type=AGENT
gradle gep --type=AGENT
```

### Available Parameters

| Parameter                   | Values         | Default        | Description                                    |
|-----------------------------|----------------|----------------|------------------------------------------------|
| `--name`                    | String         | project name   | Agent name (used in `spring.application.name`) |
| `--agent-enable-kafka`      | `true`/`false` | `true`         | Add Kafka consumer + producer transport        |
| `--agent-enable-mcp-client` | `true`/`false` | `true`         | Add MCP client for tool-calling capabilities   |

### Usage Examples

```shell
# Full agent: REST + Kafka + MCP client (Default behavior)
gradle generateEntryPoint --type=AGENT --name=my-agent

# Minimal REST-only agent without async transport and without MCP
gradle generateEntryPoint --type=AGENT --name=my-agent --agent-enable-kafka=false --agent-enable-mcp-client=false

# Agent with Kafka only
gradle generateEntryPoint --type=AGENT --name=my-agent --agent-enable-mcp-client=false

# Agent with MCP client only (REST + MCP)
gradle generateEntryPoint --type=AGENT --name=my-agent --agent-enable-kafka=false
```

### Generated Structure

```bash
domain/
├── model/src/main/java/[package]/model/
│   ├── a2a/
│   │   ├── A2ARequest.java          # Inbound A2A command envelope
│   │   ├── A2AResponse.java         # Outbound A2A response envelope
│   │   ├── A2APayload.java          # Flexible payload with parameters map
│   │   ├── A2ASecurity.java         # JWT/Entra ID security context
│   │   ├── A2ARetryPolicy.java      # Retry configuration per command
│   │   └── A2AError.java            # Structured error model
│   └── chat/gateways/
│       ├── ChatGateway.java          # Port: send prompt → LLM → Mono<String>
│       └── AgentResponseGateway.java # Port: publish response to Kafka
└── usecase/src/main/java/[package]/usecase/
    └── AgentChatUseCase.java         # Core logic: intent routing + LLM call

infrastructure/
├── entry-points/
│   └── reactive-web/
│       ├── build.gradle
│       └── src/main/java/[package]/api/
│           ├── Handler.java                  # Handles POST /a2a/v1/commands
│           ├── RouterRest.java               # WebFlux functional router
│           └── config/
│               ├── CorsConfig.java           # CORS configuration
│               └── SecurityHeadersConfig.java
└── driven-adapters/
    └── spring-ai-adapter/
        ├── build.gradle                      # Spring AI BOM + OpenAI client
        └── src/main/java/[package]/chat/
            └── SpringAiChatAdapter.java      # Implements ChatGateway via Spring AI
```

### Automatic Configuration

The command automatically updates `application.yaml`:

```yaml
spring:
  application:
    name: "my-agent"
  ai:
    openai:
      api-key: "${LLM_API_KEY:lm-studio}"
      base-url: "${LLM_URL:http://localhost:1234}"
      chat:
        options:
          model: "${LLM_MODEL:local-model}"
          temperature: "0.0"

agent:
  system-prompt: "You are a specialised agent called 'my-agent'. Use available MCP tools. Respond in JSON."

cors:
  allowed-origins: "${CORS_ALLOWED_ORIGINS:http://localhost:4200}"

# If --agent-enable-mcp-client=true
spring:
  ai:
    mcp:
      client:
        streamable-http:
          connections:
            mcp-server-1:
              url: "${MCP_SERVER_URL:http://localhost:8080}"
              endpoint: "${MCP_SERVER_ENDPOINT:/mcp/stream}"

# If --agent-enable-kafka=true
spring:
  kafka:
    consumer:
      bootstrap-servers: "${KAFKA_SERVERS:localhost:9092}"
      group-id: "my-agent-group"
      topic: "${KAFKA_CONSUMER_TOPIC:my-agent-commands}"
    producer:
      bootstrap-servers: "${KAFKA_SERVERS:localhost:9092}"
      topic: "${KAFKA_PRODUCER_TOPIC:my-agent-responses}"
```

### Extending the Agent — Adding New Intents

The core intent routing is in `AgentChatUseCase.java`. To add a new business intent:

**1. Declare the intent constant:**

```java
private static final String INTENT_SEARCH_CLIENT = "SEARCH_CLIENT";
```

**2. Register it in `mapIntentToTool()`:**

```java
return switch (intent) {
    case INTENT_EXAMPLE      -> "example_tool";
    case INTENT_SEARCH_CLIENT -> "search_client";   // ← add here
    default -> null;
};
```

**3. The use case builds a prompt and calls the LLM automatically — no further changes needed.**

### A2A Protocol — Request / Response Envelope

**Inbound request (from orchestrator or Kafka):**

```json
{
  "schemaVersion": "1.0",
  "messageId": "uuid-msg-001",
  "correlationId": "uuid-corr-001",
  "traceId": "uuid-trace-001",
  "sourceAgent": "agente-orquestador",
  "targetAgent": "my-agent",
  "intent": "SEARCH_CLIENT",
  "payload": {
    "parameters": {
      "documentType": "CC",
      "documentNumber": "123456789"
    }
  }
}
```

**Outbound response:**

```json
{
  "schemaVersion": "1.0",
  "messageId": "uuid-msg-002",
  "correlationId": "uuid-corr-001",
  "traceId": "uuid-trace-001",
  "sourceAgent": "my-agent",
  "status": "SUCCESS",
  "executionTimeMs": 342,
  "result": {
    "data": "{ ... LLM response ... }"
  }
}
```

