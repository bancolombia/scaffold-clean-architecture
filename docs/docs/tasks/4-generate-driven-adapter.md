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

| Reference for **drivenAdapterType** | Name                           | Additional Options                                                                                          |
|-------------------------------------|--------------------------------|-------------------------------------------------------------------------------------------------------------|
| generic                             | Empty Driven Adapter           | --name [name]                                                                                               |
| asynceventbus                       | Async Event Bus                | --eda [true-false] --tech [rabbitmq-kafka-rabbitmq,kafka] Default: rabbitmq                                 |
| binstash                            | Bin Stash                      |                                                                                                             |
| cognitotokenprovider                | Cognito token generator        |                                                                                                             |
| dynamodb                            | Dynamo DB adapter              |                                                                                                             |
| jpa                                 | JPA Repository                 | --secret [true-false]                                                                                       |
| kms                                 | AWS Key Management Service     |                                                                                                             |
| mongodb                             | Mongo Repository               | --secret [true-false]                                                                                       |
| mq                                  | JMS MQ Client to send messages |                                                                                                             |
| r2dbc                               | R2dbc Postgresql Client        |                                                                                                             |
| redis                               | Redis                          | --mode [template-repository] --secret [true-false]                                                          |
| restconsumer                        | Rest Client Consumer           | --url [url] --from-swagger swagger.yaml                                                                     |
| rsocket                             | RSocket Requester              |                                                                                                             |
| s3                                  | AWS Simple Storage Service     |                                                                                                             |
| secrets                             | Secrets Manager Bancolombia    | --secrets-backend [backend] <br/> Valid options for backend are "aws_secrets_manager" (default) or "vault". |
| secretskafkastrimzi                 | Secrets for Kafka Strimzi      | --secretName [name] (Opcional)                                                                              |
| sqs                                 | SQS message sender             |                                                                                                             |

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
