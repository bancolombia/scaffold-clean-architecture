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
  - [Generate Model](#generate-model)
  - [Generate Use Case](#generate-use-case)
  - [Generate Driven Adapter](#generate-driven-adapter)
  - [Generate Entry Point](#generate-entry-point)
  - [Generate Helper](#generate-helper)
  - [Generate Pipeline](#generate-pipeline)
  - [Generate Acceptance Tests](#generate-acceptance-test)
  - [Validate Structure](#validate-structure)
  - [Delete Module](#delete-module)
  - [Update Project](#update-project)
- [How can I help?](#how-can-i-help)
- [Whats Next?](#whats-next)

# Plugin Implementation

To use the [plugin](https://plugins.gradle.org/plugin/co.com.bancolombia.cleanArchitecture) you need Gradle version 6.9 or later, to start add the following section into your **build.gradle** file.

```groovy
plugins {
    id "co.com.bancolombia.cleanArchitecture" version "2.3.2"
}
```
Or if is a new  project execute this script in the root directory of your project.
```sh
echo "plugins {
    id \"co.com.bancolombia.cleanArchitecture\" version \"2.3.2\"
}" > build.gradle
```

To use the [plugin](https://plugins.gradle.org/plugin/co.com.bancolombia.cleanArchitecture) you need Gradle version 6.9 or later, to start add the following section into your **build.gradle.kts** file.

```kotlin dls
plugins {
    id("co.com.bancolombia.cleanArchitecture") version "2.3.2"
}
```
Or if is a new  project execute this script in the root directory of your project.
```sh
echo "plugins {
    id(\"co.com.bancolombia.cleanArchitecture\") version \"2.3.2\"
}" > build.gradle.kts
```

# Tasks

The Scaffolding Clean Architecture plugin will allow you run 8 tasks:

## Generate Project

The **`cleanArchitecture | ca`** task will generate a clean architecture structure in your project, this task has four optional parameters; `package` , `type`, `name` and `coverage`.
If you run this task on an existing project it will override the `main.gradle`, `build.gradle` and `gradle.properties` files. 

   - **`package`** `= <package.we.need>`: You can specify the main or default package of your project. `Default Value = co.com.bancolombia`

   - **`type`** `= <imperative | reactive>`: With this parameter the task will generate a POO project. `Default Value = imperative`

   - **`name`** `= NameProject`: This parameter is going to specify the name of the project. `Default Value = cleanArchitecture`

   - **`coverage`** `= <jacoco | cobertura>`: This parameter is going to specify the coverage tool for the project. `Default Value = jacoco`
   
   - **`lombok`** `= <true | false>`: Specify if you want to use this plugin  . `Default Value = true`

   - **`language`** `= <JAVA | KOTLIN>`: Specify if you want to use this plugin  . `Default Value = JAVA`

   - **`javaVersion`** `= <VERSION_1_8 | VERSION_11 | VERSION_17>`: Java version  . `Default Value = VERSION_11`
   
   ```shell
   gradle cleanArchitecture --package=co.com.bancolombia --type=imperative --name=NameProject --coverage=jacoco --lombok=true
   gradle ca --package=co.com.bancolombia --type=imperative --name=NameProject --coverage=jacoco --lombok=true
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

## Generate Model for Java an Kotlin

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

   | Reference for **drivenAdapterType** | Name                                | Additional Options                  | Java    | Kotlin  |
   |-------------------------------------|-------------------------------------|-------------------------------------|---------|---------|
   | generic                             | Empty Driven Adapter                | --name [name]                       | &#9745; | &#9745; |
   | jpa                                 | JPA Repository                      | --secret [true-false]               | &#9745; | &#9745; |
   | mongodb                             | Mongo Repository                    | --secret [true-false]               | &#9745; | &#9745; |
   | asynceventbus                       | Async Event Bus                     |                                     | &#9745; | &#9745; |
   | restconsumer                        | Rest Client Consumer                | --url [url]                         | &#9745; | &#9745; |
   | redis                               | Redis                               | --mode [template-repository] --secret [true-false] | &#9745; | &#9745; |
   | rsocket                             | RSocket Requester                   |                                     | &#9745; | &#9745; |
   | r2dbc                               | R2dbc Postgresql Client             |                                     | &#9745; | &#9745; |
   | kms                                 | AWS Key Management Service          |                                     | &#9745; | &#9745; |
   | secrets                             | Secrets Manager Bancolombia         |                                     | &#9745; | &#9745; |
   | s3                                  | AWS Simple Storage Service          |                                     | &#9745; | &#9745; |
   | mq                                  | JMS MQ Client to send messages      |                                     | &#9745; | &#9745; |
   | ktor                                | HTTP client for kotlin              |                                     | &#9744; | &#9745; |
   | dynamodb                            | Dynamo DB adapter                   |                                     | &#9745; | &#9745; |

   
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

   | Reference for **entryPointType** | Name                                   | Additional Options                       |Java | Kotlin |
   |----------------------------------|----------------------------------------|------------------------------------------|------|--------|
   | generic                          | Empty Entry Point                      | --name [name]                            |&#9745;|&#9745;|
   | restmvc                          | API REST (Spring Boot Starter Web)     | --server [serverOption] default undertow |&#9745;|&#9745;|
   | webflux                          | API REST (Spring Boot Starter WebFlux) | --router [true, false] default true      |&#9745;|&#9745;|
   | rsocket                          | Rsocket Controller Entry Point         |                                          |&#9745;|&#9745;|
   | graphql                          | API GraphQL                            | --pathgql [name path] default /graphql   |&#9745;|&#9745;|
   | asynceventhandler                | Async Event Handler                    |                                          |&#9745;|&#9745;|
   | mq                               | JMS MQ Client to listen messages       |                                          |&#9745;|&#9745;|

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

<br><br>

# Whats Next?

Read more  [About Clean Architecure](https://medium.com/bancolombia-tech/clean-architecture-aislando-los-detalles-4f9530f35d7a)
