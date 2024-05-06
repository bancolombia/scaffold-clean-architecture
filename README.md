![](https://github.com/bancolombia/scaffold-clean-architecture/workflows/gradle-actions/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=bancolombia_scaffold-clean-architecture&metric=alert_status)](https://sonarcloud.io/dashboard?id=bancolombia_scaffold-clean-architecture)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=bancolombia_scaffold-clean-architecture&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=bancolombia_scaffold-clean-architecture)
[![codecov](https://codecov.io/gh/bancolombia/scaffold-clean-architecture/branch/master/graph/badge.svg)](https://codecov.io/gh/bancolombia/scaffold-clean-architecture)
[![GitHub license](https://shields.io/badge/license-Apache%202-blue)](https://github.com/bancolombia/scaffold-clean-architecture/blob/master/LICENSE)
[![Scorecards supply-chain security](https://github.com/bancolombia/scaffold-clean-architecture/actions/workflows/scorecards-analysis.yml/badge.svg)](https://github.com/bancolombia/scaffold-clean-architecture/actions/workflows/scorecards-analysis.yml)

# Scaffolding of Clean Architecture

Gradle plugin to create a java and kotlin application based on Clean Architecture following our best practices!

- [Scaffolding of Clean Architecture](#scaffolding-of-clean-architecture)
- [Plugin Implementation](#plugin-implementation)
- [Tasks](#tasks)
  - [Generate Project](#generate-project)
  - [Generate Model for Java and Kotlin](#generate-model-for-java-and-kotlin)
  - [Generate Use Case for Java and Kotlin](#generate-use-case-for-java-and-kotlin)
  - [Generate Driven Adapter](#generate-driven-adapter)
  - [Generate Entry Point](#generate-entry-point)
  - [Generate Helper](#generate-helper)
  - [Generate Pipeline](#generate-pipeline)
  - [Generate Acceptance Test](#generate-acceptance-test)
  - [Generate Performance Test](#generate-performance-test)
  - [Validate Structure](#validate-structure)
  - [Delete Module](#delete-module)
  - [Update Project](#update-project)
- [How can I help?](#how-can-i-help)
- [Whats Next?](#whats-next)

# Plugin Implementation

To use the [plugin](https://plugins.gradle.org/plugin/co.com.bancolombia.cleanArchitecture) you need Gradle version 8.2 or later, to start add the following section into your **build.gradle** file.

```groovy
plugins {
    id "co.com.bancolombia.cleanArchitecture" version "3.16.2"
}
```
Or if is a new  project execute this script in the root directory of your project.
```sh
echo "plugins {
    id \"co.com.bancolombia.cleanArchitecture\" version \"3.16.2\"
}" > build.gradle
```

To use the [plugin](https://plugins.gradle.org/plugin/co.com.bancolombia.cleanArchitecture) you need Gradle version 6.9 or later, to start add the following section into your **build.gradle.kts** file.

```kotlin dls
plugins {
    id("co.com.bancolombia.cleanArchitecture") version "3.16.2"
}
```
Or if is a new  project execute this script in the root directory of your project.
```sh
echo "plugins {
    id(\"co.com.bancolombia.cleanArchitecture\") version \"3.16.2\"
}" > build.gradle.kts
```

# Tasks

The Scaffolding Clean Architecture plugin will allow you run 8 tasks:

## Generate Project

The **`cleanArchitecture | ca`** task will generate a clean architecture structure in your project, this task has four optional parameters; `package` , `type`, `name` and `coverage`.
If you run this task on an existing project it will override the `main.gradle`, `build.gradle` and `gradle.properties` files. 

   - **`package`** `= <package.we.need>`: You can specify the main or default package of your project. `Default Value = co.com.bancolombia`

   - **`type`** `= <imperative | reactive>`: With this parameter the task will generate a POO project. `Default Value = reactive`

   - **`name`** `= NameProject`: This parameter is going to specify the name of the project. `Default Value = cleanArchitecture`

   
   - **`lombok`** `= <true | false>`: Specify if you want to use this plugin  . `Default Value = true`

   - **`metrics`** `= <true | false>`: Specify if you want to enable this feature with micrometer  . `Default Value = true`

   - **`language`** `= <JAVA | KOTLIN>`: Specify if you want to use this plugin  . `Default Value = JAVA`

   - **`javaVersion`** `= <VERSION_17 | VERSION_21>`: Java version  . `Default Value = VERSION_17`
   
   ```shell
   gradle cleanArchitecture --package=co.com.bancolombia --type=reactive --name=NameProject --lombok=true
   gradle ca --package=co.com.bancolombia --type=reactive --name=NameProject --lombok=true
   ```

   **_The structure will look like this for java:_**

   ```bash
   📦NameProject
   ┣ 📂applications
   ┃ ┗ 📂app-service
   ┃ ┃ ┣ 📂src
   ┃ ┃ ┃ ┣ 📂main
   ┃ ┃ ┃ ┃ ┣ 📂java
   ┃ ┃ ┃ ┃ ┃ ┗ 📂[package]
   ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂config
   ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜[configs and beans]
   ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜MainApplication.java
   ┃ ┃ ┃ ┃ ┗ 📂resources
   ┃ ┃ ┃ ┃ ┃ ┣ 📜[properties]
   ┃ ┃ ┃ ┗ 📂test
   ┃ ┃ ┃ ┃ ┗ 📂java
   ┃ ┃ ┃ ┃ ┃ ┗ 📂[package]
   ┃ ┃ ┗ 📜build.gradle
   ┣ 📂deployment
   ┃ ┣ 📜[Dockerfile, Pipelines as a code]
   ┣ 📂domain
   ┃ ┣ 📂model
   ┃ ┃ ┣ 📂src
   ┃ ┃ ┃ ┣ 📂main
   ┃ ┃ ┃ ┃ ┗ 📂java
   ┃ ┃ ┃ ┃ ┃ ┗ 📂[package]
   ┃ ┃ ┃ ┗ 📂test
   ┃ ┃ ┃ ┃ ┗ 📂java
   ┃ ┃ ┃ ┃ ┃ ┗ 📂[package]
   ┃ ┃ ┗ 📜build.gradle
   ┃ ┗ 📂usecase
   ┃ ┃ ┣ 📂src
   ┃ ┃ ┃ ┣ 📂main
   ┃ ┃ ┃ ┃ ┗ 📂java
   ┃ ┃ ┃ ┃ ┃ ┗ 📂[package]
   ┃ ┃ ┃ ┗ 📂test
   ┃ ┃ ┃ ┃ ┗ 📂java
   ┃ ┃ ┃ ┃ ┃ ┗ 📂[package]
   ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂usecase
   ┃ ┃ ┗ 📜build.gradle
   ┣ 📂infrastructure
   ┃ ┣ 📂driven-adapters
   ┃ ┣ 📂entry-points
   ┃ ┗ 📂helpers
   ┣ 📜.gitignore
   ┣ 📜build.gradle
   ┣ 📜gradle.properties
   ┣ 📜lombok.config
   ┣ 📜main.gradle
   ┣ 📜README.md
   ┗ 📜settings.gradle
   ```


**_The structure will look like this for kotlin:_**

   ```bash
   📦NameProject
   ┣ 📂applications
   ┃ ┗ 📂app-service
   ┃ ┃ ┣ 📂src
   ┃ ┃ ┃ ┣ 📂main
   ┃ ┃ ┃ ┃ ┣ 📂kotlin
   ┃ ┃ ┃ ┃ ┃ ┗ 📂[package]
   ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂config
   ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜[configs and beans]
   ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜MainApplication.kt
   ┃ ┃ ┃ ┃ ┗ 📂resources
   ┃ ┃ ┃ ┃ ┃ ┣ 📜[properties]
   ┃ ┃ ┃ ┗ 📂test
   ┃ ┃ ┃ ┃ ┗ 📂kotlin
   ┃ ┃ ┃ ┃ ┃ ┗ 📂[package]
   ┃ ┃ ┗ 📜build.gradle.kts
   ┣ 📂deployment
   ┃ ┣ 📜[Dockerfile, Pipelines as a code]
   ┣ 📂domain
   ┃ ┣ 📂model
   ┃ ┃ ┣ 📂src
   ┃ ┃ ┃ ┣ 📂main
   ┃ ┃ ┃ ┃ ┗ 📂kotlin
   ┃ ┃ ┃ ┃ ┃ ┗ 📂[package]
   ┃ ┃ ┃ ┗ 📂test
   ┃ ┃ ┃ ┃ ┗ 📂kotlin
   ┃ ┃ ┃ ┃ ┃ ┗ 📂[package]
   ┃ ┃ ┗ 📜build.gradle.kts
   ┃ ┗ 📂usecase
   ┃ ┃ ┣ 📂src
   ┃ ┃ ┃ ┣ 📂main
   ┃ ┃ ┃ ┃ ┗ 📂kotlin
   ┃ ┃ ┃ ┃ ┃ ┗ 📂[package]
   ┃ ┃ ┃ ┗ 📂test
   ┃ ┃ ┃ ┃ ┗ 📂kotlin
   ┃ ┃ ┃ ┃ ┃ ┗ 📂[package]
   ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂usecase
   ┃ ┃ ┗ 📜build.gradle.kts
   ┣ 📂infrastructure
   ┃ ┣ 📂driven-adapters
   ┃ ┣ 📂entry-points
   ┃ ┗ 📂helpers
   ┣ 📜.gitignore
   ┣ 📜build.gradle.kts
   ┣ 📜gradle.properties
   ┣ 📜lombok.config
   ┣ 📜README.md
   ┗ 📜settings.gradle.kts
   ```

## Generate Model for Java and Kotlin

The **`generateModel | gm`** task will generate a class and interface in model layer, this task has one required parameter `name`.

```shell
   gradle generateModel --name=[modelName]
   gradle gm --name [modelName]
  ```

   **_This task will generate something like that:_**

   ```bash
   📦domain
   ┣ 📂model
   ┃ ┣ 📂src
   ┃ ┃ ┣ 📂main
   ┃ ┃ ┃ ┗ 📂java
   ┃ ┃ ┃ ┃ ┗ 📂[package]
   ┃ ┃ ┃ ┃ ┃ ┗ 📂model
   ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂gateways
   ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ModelRepository.java
   ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜Model.java
   ┃ ┃ ┗ 📂test
   ┃ ┃ ┃ ┗ 📂java
   ┃ ┃ ┃ ┃ ┗ 📂[package]
   ┃ ┃ ┃ ┃ ┃ ┗ 📂model
   ┃ ┗ 📜build.gradle
   ```

**_This task will generate something like that for kotlin:_**

   ```bash
   📦domain
   ┣ 📂model
   ┃ ┣ 📂src
   ┃ ┃ ┣ 📂main
   ┃ ┃ ┃ ┗ 📂kotlin
   ┃ ┃ ┃ ┃ ┗ 📂[package]
   ┃ ┃ ┃ ┃ ┃ ┗ 📂model
   ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂gateways
   ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ModelRepository.kt
   ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜Model.kt
   ┃ ┃ ┗ 📂test
   ┃ ┃ ┃ ┗ 📂kotlin
   ┃ ┃ ┃ ┃ ┗ 📂[package]
   ┃ ┃ ┃ ┃ ┃ ┗ 📂model
   ┃ ┗ 📜build.gradle.kts
   ```

## Generate Use Case for Java and Kotlin

The **`generateUseCase | guc`** task will generate a class in model layer, this task has one required parameter `name`.

```shell
   gradle generateUseCase --name=[useCaseName]
   gradle guc --name [useCaseName]
 ```

   **_This task will generate something like that:_**

   ```bash
   📦domain
   ┗ 📂usecase
   ┃ ┣ 📂src
   ┃ ┃ ┣ 📂main
   ┃ ┃ ┃ ┗ 📂java
   ┃ ┃ ┃ ┃ ┗ 📂[package]
   ┃ ┃ ┃ ┃ ┃ ┗ 📂usecase
   ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂business
   ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜BusinessUseCase.java
   ┃ ┃ ┗ 📂test
   ┃ ┃ ┃ ┗ 📂java
   ┃ ┃ ┃ ┃ ┗ 📂[package]
   ┃ ┃ ┃ ┃ ┃ ┗ 📂usecase
   ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂business
   ┃ ┗ 📜build.gradle
   ```

**_This task will generate something like that for kotlin:_**

   ```bash
   📦domain
   ┗ 📂usecase
   ┃ ┣ 📂src
   ┃ ┃ ┣ 📂main
   ┃ ┃ ┃ ┗ 📂kotlin
   ┃ ┃ ┃ ┃ ┗ 📂[package]
   ┃ ┃ ┃ ┃ ┃ ┗ 📂usecase
   ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂business
   ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜BusinessUseCase.kt
   ┃ ┃ ┗ 📂test
   ┃ ┃ ┃ ┗ 📂kotlin
   ┃ ┃ ┃ ┃ ┗ 📂[package]
   ┃ ┃ ┃ ┃ ┃ ┗ 📂usecase
   ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂business
   ┃ ┗ 📜build.gradle.kts
   ```

## Generate Driven Adapter

The **`generateDrivenAdapter | gda`** task will generate a module in Infrastructure layer, this task has one required parameter `type`. <br>
   Whether you'll use generic one also parameter `name` is required.

```shell
   gradle generateDrivenAdapter --type=[drivenAdapterType]
   gradle gda --type [drivenAdapterType]
   ```

   | Reference for **drivenAdapterType** | Name                           | Additional Options                                 | Java    | Kotlin  |
   |-------------------------------------|--------------------------------|----------------------------------------------------|---------|---------|
   | generic                             | Empty Driven Adapter           | --name [name]                                      | &#9745; | &#9745; |
   | asynceventbus                       | Async Event Bus                |                                                    | &#9745; | &#9745; |
   | binstash                            | Bin Stash                      |                                                    | &#9745; | &#9745; |
   | cognitotokenprovider                | Generador de token de cognito  |                                                    | &#9745; |         |
   | dynamodb                            | Dynamo DB adapter              |                                                    | &#9745; | &#9745; |
   | jpa                                 | JPA Repository                 | --secret [true-false]                              | &#9745; | &#9745; |
   | kms                                 | AWS Key Management Service     |                                                    | &#9745; | &#9745; |
   | ktor                                | HTTP client for kotlin         |                                                    | &#9744; | &#9745; |
   | mongodb                             | Mongo Repository               | --secret [true-false]                              | &#9745; | &#9745; |
   | mq                                  | JMS MQ Client to send messages |                                                    | &#9745; | &#9745; |
   | r2dbc                               | R2dbc Postgresql Client        |                                                    | &#9745; | &#9745; |
   | redis                               | Redis                          | --mode [template-repository] --secret [true-false] | &#9745; | &#9745; |
   | restconsumer                        | Rest Client Consumer           | --url [url] --from-swagger swagger.yaml            | &#9745; | &#9745; |
   | rsocket                             | RSocket Requester              |                                                    | &#9745; | &#9745; |
   | s3                                  | AWS Simple Storage Service     |                                                    | &#9745; | &#9745; |
   | secrets                             | Secrets Manager Bancolombia    | --secrets-backend [backend] <br> Valid options for backend are "aws_secrets_manager" (default) or "vault". | &#9745; | &#9745; |
   | sqs                                 | SQS message sender             |                                                    | &#9745; | &#9745; |

   
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

## Generate Entry Point

The **`generateEntryPoint | gep`** task will generate a module in Infrastructure layer, this task has one required parameter `type`. <br>
   Whether you'll use generic one also parameter `name` is required.

   ```shell
   gradle generateEntryPoint --type=[entryPointType]
   gradle gep --type [entryPointType]
   ```

   | Reference for **entryPointType** | Name                                   | Additional Options                                                                                |Java   | Kotlin  |
   |----------------------------------|----------------------------------------|---------------------------------------------------------------------------------------------------|-------|---------|
   | generic                          | Empty Entry Point                      | --name [name]                                                                                     |&#9745;| &#9745; |
   | asynceventhandler                | Async Event Handler                    |                                                                                                   |&#9745;| &#9745; |
   | graphql                          | API GraphQL                            | --pathgql [name path] default /graphql                                                            |&#9745;| &#9745; |
   | kafka                            | Kafka Consumer                         |                                                                                                   |&#9745;|         |
   | mq                               | JMS MQ Client to listen messages       |                                                                                                   |&#9745;| &#9745; |
   | restmvc                          | API REST (Spring Boot Starter Web)     | --server [serverOption] default undertow --authorization [true-false] --from-swagger swagger.yaml |&#9745;| &#9745; |
   | rsocket                          | Rsocket Controller Entry Point         |                                                                                                   |&#9745;| &#9745; |
   | sqs                              | SQS Listener                           |                                                                                                   |&#9745;| &#9745; |
   | webflux                          | API REST (Spring Boot Starter WebFlux) | --router [true, false] default true --authorization [true-false] --from-swagger swagger.yaml      |&#9745;| &#9745; |

   Additionally, if you'll use a restmvc, you can specify the web server on which the application will run. By default, undertow.

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


## Generate Helper

The **`generateHelper | gh`** task will generate a module in Infrastructure layer, this task has one required parameter `name`. <br>

```shell
   gradle generateHelper --name=[helperName]
   gradle gh --name=[helperName]
   ```

## Generate Pipeline

The **`generatePipeline | gpl`** task will generate CI pipeline inside the folder "./deployment/", this task has one required parameter `type` and one optional parameter `monoRepo` by default is  false.

```shell
   gradle generatePipeline --type=[pipelineType] --monoRepo=[true | false]
   gradle gpl --type=[pipelineType]  --monoRepo=[true | false]
   ```

   | Reference for **pipelineType** | Name              |
   | ------------------------------ | ----------------- |
   | azure                          | Azure Pipeline    |
   | circleci                       | CircleCI Pipeline |
   | github                         | GitHub Action     |
   | jenkins                        | Jenkins Pipeline  |
   
 ## Generate Acceptance Test
 
The **`generateAcceptanceTest | gat`** task will generate subproject by [karate framework](https://github.com/intuit/karate)  inside the folder "./deployment/", this task has one optional parameter, `name`.

   - **`name`** `= NameAcceptanceTestProject`: This parameter is going to specify the name of the acceptance test project. `Default Value = acceptanceTest`
   
```shell
      gradle generateAcceptanceTest --name=[acceptanceTestProjectName]
      gradle gat --name=[acceptanceTestProjectName] 
  ```
>   Karate is an open-source tool to combine API test-automation, mocks, performance-testing and even UI automation into a single, unified framework. The BDD syntax popularized by Cucumber is language-neutral, and easy for even non-programmers. Assertions and HTML reports are built-in, and you can run tests in parallel for speed.

## Generate Performance Test

The **`generatePerformanceTest | gpt`** task will generate Performance test inside the folder "./performance-test/", this task has one required parameter `type`.

```shell
   gradle generatePerformanceTest --type=[performanceType]
   gradle gpt --type=[performanceType]
   ```

    | Reference for **performanceType** | Name                    |
    | --------------------------------- | ----------------------- |
    | jmeter                            | Jmeter Performance Test |

## Validate Structure

The **`validateStructure | vs`** Validate that project references aren't violated.

```shell
   gradle validateStructure
   gradle vs
   ```

This validation has another best practices verifications, which you can see on the generated
`ArchitectureTest` file within the unit tests of the `app-service` module. 

### Dependency Rules

One important point made by Robert C. Martin on Clean Architecture is the **Dependency Rule**, that can be summarized like 
this: source code dependencies can only point inwards. Nothing in an inner circle can know anything at all about 
something in an outer circle. In particular, the name of something declared in an outer circle must not be mentioned 
by the code in an inner circle.

Having that in mind, the **`validateStructure | vs`** task performs the following validations:

1) Model module: to have no dependencies at all.
2) UseCase module: to declare dependency to the Model module ONLY, and no other additional dependencies.
3) Infrastructure Layer modules: 
   - Allow declaration of any external dependency.
   - Allow declaration of dependency on Model and/or UseCase modules. 
   - Avoid declaration of dependency AppService module.

**Whitelisting dependencies**:

Some dependencies, specially when working with BOMs (Bills of Materials), are injected transversally to the project, 
making the validation task to flag failures to rules 1 and 2. To avoid this scenario, or other you may encounter, 
you can configure a set of whitelisted dependencies.

*Example:*

1) Let's say you have declared a BOM in your project:

   ```groovy
   dependencies {
     implementation(platform("com.myorg:my-bom:0.0.1"))
   }
   ```

2) This will make **`validateStructure | vs`** to flag a failure indicating an error like this:

   ```bash
   Validating Model Module
    --- Dependency com.myorg:some-bom is not allowed in Model Layer
   ```

3) To avoid this, you can white list your BOM like this:

   ```groovy
   // build.gradle
   cleanPlugin {
     modelProps {
        whitelistedDependencies = "my-bom, <dep2>, <depN..>"
     }
   }
   ```

   Indicating only the name of the dependencies comma-separated.


## Delete Module

The **`deleteModule | dm`** task will delete a sub project, this task has one required parameter `module`.

```shell
   gradle deleteModule --module=[name]
   gradle dm --module=[name]
   ```

   <br><br><br>
  
  ## Update Project
  
  The **`updateCleanArchitecture | u`** task will update plugin and dependencies in all sub projects, this task has one optional parameter `dependencies` 
  if you only want to update some dependencies the dependency need to contain the group, and the artifact for example for the dependency **cleanArchitecture** you will need to append **co.com.bancolombia:cleanArchitecture**.
  It also updates the spring, lombok, gradle and some other dependencies according to the plugin version that you are using, ocassionally it could make changes in other project structural files.
  We recommend that you commit your changes to git before running this command, but you can skip the verification passing `--git false`.
  
  ```shell
     gradle updateCleanArchitecture --dependencies=[dependency1, dependency2, ...]
     gradle u --dependencies=[dependency1, dependency2, ...]
   ```
  

# How can I help?

Review the issues, we hear new ideas. Read more [Contributing](https://github.com/bancolombia/scaffold-clean-architecture/wiki/Contributing)

## Analytics

You can help the Contributors Team to prioritize features and improvements by permitting the Contributors team to send
gradle tasks usage statistics to Analytics Server.
The Contributors Team collect usage statistics unless you explicitly opt in off. 

Due to the user input limitations to gradle task, when running any plugin task you will be notified about the analytics
recollection, and you have the possibility to disable this recollection. If you enable or disable analytics explicitly,
future task executions will not notify you.

To explicitly enable analytics and avoid the notification message
```shell
gradle analytics --enabled true
# o gradle a --enabled true
```

To disable analytics
```shell
gradle analytics --enabled false
# # o gradle a --enabled false
```

### What is collected?

Usage analytics include the commands and selected flags for each execution.
Usage analytics may include the following information:

- Your operating system \(macOS, Linux distribution, Windows\) and its version.
- Java vendor name and version.
- Java specification and runtime versions.
- Plugin version.
- Project language (`kotlin` or `java`)
- Task name that was run.
- Workspace information like language, user that is running the task.
- For generate use case, generate model, generate helper and delete module tasks, the name will be sent.
- For all tasks, the type and name, the time it took to run the task, and project type (reactive, imperative).

<br><br>

# Whats Next?

Read more  [About Clean Architecure](https://medium.com/bancolombia-tech/clean-architecture-aislando-los-detalles-4f9530f35d7a)
