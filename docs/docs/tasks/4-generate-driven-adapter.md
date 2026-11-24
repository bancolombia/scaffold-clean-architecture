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
| **jpa**                  | JPA Repository                 | `--secret`          | `true`, `false`                       | `false`               | 
| **kms**                  | AWS Key Management Service     | -                   | -                                     | -                     |
| **mongodb**              | Mongo Repository               | `--secret`          | `true`, `false`                       | `false`               |
| **mq**                   | JMS MQ Client to send messages | -                   | -                                     | -                     |
| **r2dbc**                | R2dbc Postgresql Client        | -                   | -                                     | -                     |
| **redis**                | Redis                          | `--mode`            | `template`, `repository`              | `template`            |
|                          |                                | `--secret`          | `true`, `false`                       | `false`               |
| **restconsumer**         | Rest Client Consumer           | `--url`             | String (URL)                          | -                     |
|                          |                                | `--from-swagger`    | Contract path or URL (see below)      | `swagger.yaml`        |
| **rsocket**              | RSocket Requester              | -                   | -                                     | -                     |
| **s3**                   | AWS Simple Storage Service     | -                   | -                                     | -                     |
| **secrets**              | Secrets Manager Bancolombia    | `--secrets-backend` | `aws_secrets_manager`, `vault`        | `aws_secrets_manager` |
| **secretskafkastrimzi**  | Secrets for Kafka Strimzi      | `--secret-name`     | String                                | -                     |
| **sqs**                  | SQS message sender             | -                   | -                                     | -                     |

`--from-swagger` receives the OpenAPI contract file (`.yaml` or `.json`) in one of these forms:

- A path relative to the project root, for example `--from-swagger=swagger.yaml` or `--from-swagger=contracts/swagger.yaml`.
- An absolute path, for example `--from-swagger=C:\contracts\swagger.yaml` or `--from-swagger=/home/user/contracts/swagger.yaml`.
- A `file:` URI, for example `--from-swagger=file:///home/user/contracts/swagger.yaml`.
- An `http(s)` URL that returns the contract file itself, for example `--from-swagger=https://example.com/api/swagger.yaml`.
  A web page such as the Swagger UI does not work; use the URL of the YAML or JSON document it loads.

```shell
gradle gda --type=restconsumer --from-swagger=swagger.yaml
```

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
