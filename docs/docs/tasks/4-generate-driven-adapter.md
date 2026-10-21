---
sidebar_position: 5
---

# Generate Driven Adapter

The **`generateDrivenAdapter | gda`** task will generate a module in Infrastructure layer, this task has one required
parameter `type`.<br/>
Whether you'll use generic one also parameter `name` is required.

```shell
gradle generateDrivenAdapter --type=[drivenAdapterType]
gradle gda --type [drivenAdapterType]
```

| Type                     | Name                           | Parameter           | Values                                | Default               |
|--------------------------|--------------------------------|---------------------|---------------------------------------|-----------------------|
| **generic**              | Empty Driven Adapter           | `--name`            | String                                | -                     |
| **asynceventbus**        | Async Event Bus                | `--eda`             | `true`, `false`                       | `false`               |
|                          |                                | `--tech`            | `rabbitmq`, `kafka`, `rabbitmq,kafka` | `rabbitmq`            |
| **binstash**             | Bin Stash                      | `--cache-mode`      | `LOCAL`, `CENTRALIZED`, `HYBRID`      | `LOCAL`               |
| **cognitotokenprovider** | Cognito token generator        | -                   | -                                     | -                     |
| **dynamodb**             | Dynamo DB adapter              | -                   | -                                     | -                     |
| **grpc**                 | gRPC Client Consumer           | -                   | -                                     | -                     |
| **jpa**                  | JPA Repository                 | `--secret`          | `true`, `false`                       | `false`               | 
| **kms**                  | AWS Key Management Service     | -                   | -                                     | -                     |
| **mongodb**              | Mongo Repository               | `--secret`          | `true`, `false`                       | `false`               |
| **mq**                   | JMS MQ Client to send messages | -                   | -                                     | -                     |
| **r2dbc**                | R2dbc Postgresql Client        | -                   | -                                     | -                     |
| **redis**                | Redis                          | `--mode`            | `template`, `repository`              | `template`            |
|                          |                                | `--secret`          | `true`, `false`                       | `false`               |
| **restconsumer**         | Rest Client Consumer           | `--url`             | String (URL)                          | -                     |
|                          |                                | `--from-swagger`    | File path                             | `swagger.yaml`        |
| **rsocket**              | RSocket Requester              | -                   | -                                     | -                     |
| **s3**                   | AWS Simple Storage Service     | -                   | -                                     | -                     |
| **secrets**              | Secrets Manager Bancolombia    | `--secrets-backend` | `aws_secrets_manager`, `vault`        | `aws_secrets_manager` |
| **secretskafkastrimzi**  | Secrets for Kafka Strimzi      | `--secret-name`     | String                                | -                     |
| **sqs**                  | SQS message sender             | -                   | -                                     | -                     |

_**This task will generate something like that:**_

   ```bash
   📦infrastructure
   ┣ 📂driven-adapters
   ┃ ┗ 📂jpa-repository
   ┃ ┃ ┣ 📂src
   ┃ ┃ ┃ ┣ 📂main
   ┃ ┃ ┃ ┃ ┗ 📂java
   ┃ ┃ ┃ ┃ ┃ ┗ 📂[package]
   ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂jpa
   ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂config
   ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜DBSecret.java
   ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂helper
   ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜AdapterOperations.java
   ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜JPARepository.java
   ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜JPARepositoryAdapter.java
   ┃ ┃ ┃ ┗ 📂test
   ┃ ┃ ┃ ┃ ┗ 📂java
   ┃ ┃ ┃ ┃ ┃ ┗ 📂[package]
   ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂jpa
   ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂helper
   ┃ ┃ ┗ 📜build.gradle
   ```


---

## Usage Example for gRPC Client Consumer

The **`grpc`** driven adapter type generates a gRPC client module using the official [Spring gRPC](https://docs.spring.io/spring-grpc/reference/) integration (`spring-boot-starter-grpc-client`). It includes Protocol Buffers configuration, a sample client adapter, and configuration for connecting to external gRPC services.

### Basic Command

```shell
gradle generateDrivenAdapter --type=grpc
gradle gda --type=grpc
```

### Generated Structure

```bash
infrastructure/
└── driven-adapters/
    └── grpc-consumer/
        ├── build.gradle
        └── src/
            ├── main/
            │   ├── java/[package]/grpc/
            │   │   ├── GrpcConsumerAdapter.java
            │   │   └── config/
            │   │       └── GrpcClientConfig.java
            │   └── proto/
            │       └── service.proto
            └── test/java/[package]/grpc/
                └── GrpcConsumerAdapterTest.java
```

### Automatic Configuration

The command automatically updates `application.yaml` with gRPC client configuration:

```yaml
spring:
  grpc:
    client:
      channels:
        example-service:
          target: static://localhost:9090
```

### Dependencies

The generated module includes:

| Dependency | Description |
|------------|-------------|
| `spring-boot-starter-grpc-client` | Official Spring Boot integration for gRPC client |
| `grpc-services` | gRPC services utilities |
| `spring-grpc-test` | Spring gRPC testing utilities |
| `protobuf-gradle-plugin` | Gradle plugin for proto compilation |

### Proto File

The generated `service.proto` includes a sample service definition that should match the server you want to consume:

```protobuf
syntax = "proto3";

package grpc;

option java_multiple_files = true;
option java_package = "co.com.bancolombia.grpc";
option java_outer_classname = "ExampleServiceProto";

// Example gRPC Service for client consumption
// TODO: Replace this with the actual service definition from the server you want to consume.
service ExampleService {
  rpc getById(Request) returns (Response);
}

message Request {
  string id = 1;
}

message Response {
  string id = 1;
  string message = 2;
}
```

### Compiling Proto Files

After generating the driven adapter, compile the proto files to generate Java classes:

```shell
# Generate Java classes from proto files
./gradlew :grpc-consumer:generateProto

# Compile the module
./gradlew :grpc-consumer:compileJava
```

### Customization

1. **Modify the proto file**: Replace `service.proto` with the actual service definition from the gRPC server you want to consume
2. **Update the configuration**: Modify `GrpcClientConfig.java` to create the appropriate stubs for your service
3. **Implement the gateway**: Have `GrpcConsumerAdapter` implement a gateway interface from your domain layer

### Example: Implementing a Domain Gateway

1. Create a gateway interface in the domain model:

```java
// domain/model/src/main/java/[package]/model/gateway/ExampleGateway.java
public interface ExampleGateway {
    String getById(String id);
}
```

2. Implement the gateway in the driven adapter:

```java
// infrastructure/driven-adapters/grpc-consumer/src/main/java/[package]/grpc/GrpcConsumerAdapter.java
@Slf4j
@Service
@RequiredArgsConstructor
public class GrpcConsumerAdapter implements ExampleGateway {

    private final ExampleServiceGrpc.ExampleServiceBlockingStub exampleServiceStub;

    @Override
    public String getById(String id) {
        log.info("Calling gRPC service with id: {}", id);
        try {
            Request request = Request.newBuilder()
                    .setId(id)
                    .build();
            Response response = exampleServiceStub.getById(request);
            return response.getMessage();
        } catch (StatusRuntimeException e) {
            log.error("gRPC call failed with status: {}", e.getStatus(), e);
            throw new RuntimeException("Failed to call gRPC service", e);
        }
    }
}
```

### Configuration Options

The gRPC client can be configured with additional options in `application.yaml`:

```yaml
spring:
  grpc:
    client:
      channels:
        example-service:
          target: static://localhost:9090
          # TLS Configuration (optional)
          tls:
            enabled: false
            # bundle: my-bundle  # Reference to SSL bundle
          # Negotiation type: PLAINTEXT or TLS
          negotiation-type: PLAINTEXT
          # Keep alive settings
          keep-alive-time: 30s
          keep-alive-timeout: 10s
          # Load balancing policy
          default-load-balancing-policy: round_robin
```

### Testing

The generated test class uses Mockito to mock the gRPC stub:

```java
@ExtendWith(MockitoExtension.class)
class GrpcConsumerAdapterTest {
    @Mock
    private ExampleServiceGrpc.ExampleServiceBlockingStub exampleServiceStub;
    
    private GrpcConsumerAdapter grpcConsumerAdapter;

    @BeforeEach
    void setUp() {
        grpcConsumerAdapter = new GrpcConsumerAdapter(exampleServiceStub);
    }

    @Test
    void shouldCallGrpcServiceSuccessfully() {
        // Arrange
        String testId = "test-id";
        Response mockResponse = Response.newBuilder()
                .setId(testId)
                .setMessage("Success response")
                .build();
        when(exampleServiceStub.getById(any(Request.class))).thenReturn(mockResponse);
        
        // Act
        String result = grpcConsumerAdapter.getById(testId);
        
        // Assert
        assertEquals("Success response", result);
        verify(exampleServiceStub).getById(any(Request.class));
    }
}
```

### Related

- For creating a gRPC server, see [Generate Entry Point - gRPC Server](./5-generate-entry-point.md#usage-example-for-grpc-server)
