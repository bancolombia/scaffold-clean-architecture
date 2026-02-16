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
