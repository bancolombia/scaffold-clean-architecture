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

| Reference for **entryPointType** | Name                                   | Additional Options                                                                                |
   |----------------------------------|----------------------------------------|---------------------------------------------------------------------------------------------------|
| generic                          | Empty Entry Point                      | --name [name]                                                                                     |
| asynceventhandler                | Async Event Handler                    |                                                                                                   |
| graphql                          | API GraphQL                            | --pathgql [name path] default /graphql                                                            |
| kafka                            | Kafka Consumer                         |                                                                                                   |
| mq                               | JMS MQ Client to listen messages       |                                                                                                   |
| restmvc                          | API REST (Spring Boot Starter Web)     | --server [serverOption] default undertow --authorization [true-false] --from-swagger swagger.yaml |
| rsocket                          | Rsocket Controller Entry Point         |                                                                                                   |
| sqs                              | SQS Listener                           |                                                                                                   |
| webflux                          | API REST (Spring Boot Starter WebFlux) | --router [true, false] default true --authorization [true-false] --from-swagger swagger.yaml      |

Additionally, if you'll use a restmvc, you can specify the web server on which the application will run. By default,
undertow.

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
