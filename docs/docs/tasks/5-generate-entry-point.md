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

| Reference for **entryPointType** | Name                                   | Additional Options                                                                                                                                                 |
|----------------------------------|----------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| generic                          | Empty Entry Point                      | --name [name]                                                                                                                                                      |
| asynceventhandler                | Async Event Handler                    | --eda [true-false] --tech [rabbitmq-kafka-rabbitmq,kafka] Default: rabbitmq                                                                                        |
| graphql                          | API GraphQL                            | --pathgql [name path] default /graphql                                                                                                                             |
| kafka                            | Kafka Consumer                         |                                                                                                                                                                    |
| mcp                              | MCP Server (Model Context Protocol)    | --name [name] --enable-tools [true,false] --enable-resources [true,false] --enable-prompts [true,false]                                                            |
| mq                               | JMS MQ Client to listen messages       |                                                                                                                                                                    |
| restmvc                          | API REST (Spring Boot Starter Web)     | --server [serverOption] default undertow --authorization [true,false] --from-swagger swagger.yaml --swagger [true,false]                                           |
| rsocket                          | Rsocket Controller Entry Point         |                                                                                                                                                                    |
| sqs                              | SQS Listener                           |                                                                                                                                                                    |
| webflux                          | API REST (Spring Boot Starter WebFlux) | --router [true, false] default true --authorization [true,false] --from-swagger swagger.yaml --versioning [HEADER, PATH,NONE] default NONE  --swagger [true,false] |
| kafkastrimzi                     | Kafka Strimzi Consumer Entry Point     | --name [name] --topicConsumer [topicName] (optional, for default 'test-with-registries')                                                                           |

Additionally, if you'll use a restmvc, you can specify the web server on which the application will run. By default,
undertow.

## Usage Example for Kafka Strimzi Consumer
```shell
gradle generateEntryPoint --type=kafkastrimzi 
gradle gep --type=kafkastrimzi
```
```shell
gradle generateEntryPoint --type=kafkastrimzi --name=myConsumer --topicConsumer=myTopic
gradle gep --type=kafkastrimzi --name=myConsumer --topicConsumer=myTopic
```

This will generate a specialized entry point for consuming Kafka messages using Strimzi, with custom parameters.

## Usage Example for MCP (Model Context Protocol)

The **`mcp`** entry point type generates a reactive MCP server with Tools, Resources, and Prompts capabilities, based on Spring AI 1.1.0+.

### Basic Command

```shell
gradle generateEntryPoint --type=mcp
gradle gep --type=mcp
```

### Available Parameters

| Parameter | Values | Default | Description |
|-----------|---------|---------|-------------|
| `--name` | String | `null` | MCP Server Name |
| `--enable-tools` | `true`/`false` | `true` | Enable Tools |
| `--enable-resources` | `true`/`false` | `true` | Enable Resources |
| `--enable-prompts` | `true`/`false` | `true` | Enable Prompts |

### Usage Examples

```shell
# Generate with all capabilities enabled (default)
gradle generateEntryPoint --type=mcp

# Only Tools
gradle generateEntryPoint --type=mcp --enable-tools=true --enable-resources=false --enable-prompts=false

# Only Resources
gradle generateEntryPoint --type=mcp --enable-tools=false --enable-resources=true --enable-prompts=false

# With custom name
gradle generateEntryPoint --type=mcp --name=BancolombiaAssistant
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
            │   └── prompts/
            │       └── ExamplePrompt.java
            └── test/java/[package]/mcp/
                ├── tools/
                ├── resources/
                └── prompts/
```

### Generated Components

**Java Classes:**
- `HealthTool.java` - Health check tool
- `ExampleTool.java` - Example tool (echo, add)
- `SystemInfoResource.java` - System info resource
- `UserInfoResource.java` - Resource template with parameters
- `ExamplePrompt.java` - Prompt templates

**Tests:**
- `HealthToolTest.java`
- `ExampleToolTest.java`
- `SystemInfoResourceTest.java`
- `UserInfoResourceTest.java`
- `ExamplePromptTest.java`

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
        streamable-http:
          mcp-endpoint: "/mcp/${spring.application.name}"
        capabilities:
          tool: true
          resource: true
          prompt: true
        request-timeout: "30s"
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

```shell
   gradle generateEntryPoint --type=restmvc --server=[serverOption]
   gradle gep --type=restmvc --server=[serverOption]
   ```

| Reference for **serverOption** | Name                      |
   |--------------------------------|---------------------------|
| undertow                       | Undertow server (default) |
| tomcat                         | Tomcat server             |
| jetty                          | Jetty server              |

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
