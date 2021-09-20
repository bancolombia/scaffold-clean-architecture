![](https://github.com/bancolombia/scaffold-clean-architecture/workflows/gradle-actions/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=bancolombia_scaffold-clean-architecture&metric=alert_status)](https://sonarcloud.io/dashboard?id=bancolombia_scaffold-clean-architecture)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=bancolombia_scaffold-clean-architecture&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=bancolombia_scaffold-clean-architecture)
[![codecov](https://codecov.io/gh/bancolombia/scaffold-clean-architecture/branch/master/graph/badge.svg)](https://codecov.io/gh/bancolombia/scaffold-clean-architecture)
[![GitHub license](https://img.shields.io/github/license/Naereen/StrapDown.js.svg)](https://github.com/bancolombia/scaffold-clean-architecture/blob/master/LICENSE)

# Scaffolding of Clean Architecture

Gradle plugin to create a java application based on Clean Architecture following our best practices!

- [Scaffolding of Clean Architecture](#scaffolding-of-clean-architecture)
- [Plugin Implementation](#plugin-implementation)
- [Tasks](#tasks)
  - [Generate Project](#generate-project)
  - [Generate Model](#generate-model)
  - [Generate Use Case](#generate-use-case)
  - [Generate Driven Adapter](#generate-driven-adapter)
  - [Generate Entry Point](#generate-entry-point)
  - [Generate Helper](#generate-helper)
  - [Generate Pipeline](#generate-pipeline)
  - [Generate Acceptance Tests](#generate-acceptance-test)
  - [Validate Structure](#validate-structure)
  - [Delete Module](#delete-module)
- [How can I help?](#how-can-i-help)
- [Whats Next?](#whats-next)

# Plugin Implementation

To use the [plugin](https://plugins.gradle.org/plugin/co.com.bancolombia.cleanArchitecture) you need Gradle version 5.6 or later, to start add the following section into your **build.gradle** file.

```groovy
plugins {
    id "co.com.bancolombia.cleanArchitecture" version "1.9.8"
}
```

# Tasks

The Scaffolding Clean Architecture plugin will allow you run 8 tasks:

## Generate Project

The **`cleanArchitecture | ca`** task will generate a clean architecture structure in your project, this task has four optional parameters; `package` , `type`, `name` and `coverage`.

   - **`package`** `= <package.we.need>`: You can specify the main or default package of your project. `Default Value = co.com.bancolombia`

   - **`type`** `= <imperative | reactive>`: With this parameter the task will generate a POO project. `Default Value = imperative`

   - **`name`** `= NameProject`: This parameter is going to specify the name of the project. `Default Value = cleanArchitecture`

   - **`coverage`** `= <jacoco | cobertura>`: This parameter is going to specify the coverage tool for the project. `Default Value = jacoco`
   
   - **`lombok`** `= <true | false>`: Specify if you want to use this plugin  . `Default Value = true`

   ```shell
   gradle cleanArchitecture --package=co.com.bancolombia --type=imperative --name=NameProject --coverage=jacoco --lombok=true
   gradle ca --package=co.com.bancolombia --type=imperative --name=NameProject --coverage=jacoco --lombok=true
   ```

   **_The structure will look like this:_**

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

## Generate Model

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

## Generate Use Case

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

## Generate Driven Adapter

The **`generateDrivenAdapter | gda`** task will generate a module in Infrastructure layer, this task has one required parameter `type`. <br>
   Whether you'll use generic one also parameter `name` is required.

   ```shell
   gradle generateDrivenAdapter --type=[drivenAdapterType]
   gradle gda --type [drivenAdapterType]
   ```

   | Reference for **drivenAdapterType** | Name                           | Additional Options                                 |
   |-------------------------------------|--------------------------------|----------------------------------------------------|
   | generic                             | Empty Driven Adapter           | --name [name]                                      |
   | jpa                                 | JPA Repository                 | --secret [true-false]                              |
   | mongodb                             | Mongo Repository               | --secret [true-false]                              |
   | asynceventbus                       | Async Event Bus                |                                                    |
   | restconsumer                        | Rest Client Consumer           | --url [url]                                        |
   | redis                               | Redis                          | --mode [template-repository] --secret [true-false] |
   | rsocket                             | RSocket Requester              |                                                    |
   | r2dbc                               | R2dbc Postgresql Client        |                                                    |
   | kms                                 | AWS Key Management Service     |                                                    |
   | secrets                             | Secrets Manager Bancolombia    |                                                    |
   | s3                                  | AWS Simple Storage Service     |                                                    |
   | mq                                  | JMS MQ Client to send messages |                                                    |
   
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

   | Reference for **entryPointType** | Name                                   | Additional Options                       |
   |----------------------------------|----------------------------------------|------------------------------------------|
   | generic                          | Empty Entry Point                      | --name [name]                            |
   | restmvc                          | API REST (Spring Boot Starter Web)     | --server [serverOption] default undertow |
   | webflux                          | API REST (Spring Boot Starter WebFlux) | --router [true, false] default true      |
   | rsocket                          | Rsocket Controller Entry Point         |                                          |
   | graphql                          | API GraphQL                            | --pathgql [name path] default /graphql   |
   | asynceventhandler                | Async Event Handler                    |                                          |
   | mq                               | JMS MQ Client to listen messages       |                                          |

   Additionally, if you'll use a restmvc, you can specify the web server on which the application will run. By default, undertow.

    ```shell
   gradle generateEntryPoint --type=restmvc --server=[serverOption]
   gradle gep --type=restmvc --server=[serverOption]
   ```

   | Reference for **serverOption** | Name                      |
   | ------------------------------ | ------------------------- |
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

The **`generatePipeline | gpl`** task will generate CI pipeline inside the folder "./deployment/", this task has one required parameter `type`.

   ```shell
   gradle generatePipeline --type=[pipelineType]
   gradle gpl --type=[pipelineType]
   ```

   | Reference for **pipelineType** | Name           |
   | ------------------------------ | -------------- |
   | azure                          | Azure Pipeline |
   | github                          | GitHub Action |
   
 ## Generate Acceptance Test
 
The **`generateAcceptanceTest | gat`** task will generate subproject by [karate framework](https://github.com/intuit/karate)  inside the folder "./deployment/",this task does not have  required parameters.
   
      ```shell
      gradle generateAcceptanceTest
      gradle gat 
      ```
>   Karate is an open-source tool to combine API test-automation, mocks, performance-testing and even UI automation into a single, unified framework. The BDD syntax popularized by Cucumber is language-neutral, and easy for even non-programmers. Assertions and HTML reports are built-in, and you can run tests in parallel for speed.

## Validate Structure

The **`validateStructure | vs`** Validate that project references aren't violated.

   ```shell
   gradle validateStructure
   gradle vs
   ```
## Delete Module

The **`deleteModule | dm`** task will delete a sub project, this task has one required parameter `module`.

   ```shell
   gradle deleteModule --module=[name]
   gradle dm --module=[name]
   ```

   <br><br><br>

# How can I help?

Review the issues, we hear new ideas. Read more [Contributing](https://github.com/bancolombia/scaffold-clean-architecture/wiki/Contributing)

<br><br>

# Whats Next?

Read more [About Clean Architecure](https://medium.com/bancolombia-tech/clean-architecture-aislando-los-detalles-4f9530f35d7a)
