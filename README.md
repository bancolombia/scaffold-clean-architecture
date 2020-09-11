![](https://github.com/bancolombia/scaffold-clean-architecture/workflows/gradle-actions/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=bancolombia_scaffold-clean-architecture&metric=alert_status)](https://sonarcloud.io/dashboard?id=bancolombia_scaffold-clean-architecture)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=bancolombia_scaffold-clean-architecture&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=bancolombia_scaffold-clean-architecture)
[![codecov](https://codecov.io/gh/bancolombia/scaffold-clean-architecture/branch/master/graph/badge.svg)](https://codecov.io/gh/bancolombia/scaffold-clean-architecture)
[![GitHub license](https://img.shields.io/github/license/Naereen/StrapDown.js.svg)](https://github.com/bancolombia/scaffold-clean-architecture/blob/master/LICENSE)

# Scaffolding of Clean Architecture

Gradle plugin to create a java application based on Clean Architecture following our best practices!

# Plugin Implementation

To use the plugin you need Gradle version 5.6 or later, to start add the following section into your **build.gradle** file.

```groovy
plugins {
    id "co.com.bancolombia.cleanArchitecture" version "1.6.9"
}
```

# Tasks

The Scaffolding Clean Architecture plugin will allow you run 8 tasks:

## Generate Project

1. The **`cleanArchitecture | ca`** task will generate a clean architecture structure in your project, this task has four optional parameters; `package` , `type`, `name` and `coverage`.

   - **`package`** `= <package.we.need>`: You can specify the main or default package of your project. `Default Value = co.com.bancolombia`

   - **`type`** `= <imperative | reactive>`: With this parameter the task will generate a POO project. `Default Value = imperative`

   - **`name`** `= NameProject`: This parameter is going to specify the name of the project. `Default Value = cleanArchitecture`

   - **`coverage`** `= <jacoco | cobertura>`: This parameter is going to specify the coverage tool for the project. `Default Value = jacoco`

   ```shell
   gradle cleanArchitecture --package=co.com.bancolombia --type=imperative --name=NameProject --coverage=jacoco
   gradle ca --package=co.com.bancolombia --type=imperative --name=NameProject --coverage=jacoco
   ```

   ### The structure will look like this:

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

2. The **`generateModel | gm`** task will generate a class and interface in model layer, this task has one required parameter `name`.

   ```shell
   gradle generateModel --name=[modelName]
   gradle gm --name [modelName]
   ```

   ### This task will generate something like that:

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

3. The **`generateUseCase | guc`** task will generate a class in model layer, this task has one required parameter `name`.

   ```shell
   gradle generateUseCase --name=[useCaseName]
   gradle guc --name [useCaseName]
   ```

   ### This task will generate something like that:

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

4. The **`generateDrivenAdapter | gda`** task will generate a class in Infrastructure layer, this task has one required parameter `type`. <br>
   Whether you'll use generic one also parameter `name` is required.

   ```shell
   gradle generateDrivenAdapter --type=[drivenAdapterType]
   gradle gda --type [drivenAdapterType]
   ```

   When use `mongodb` type please be sure that property 'reactive' is set correctly in gradle.properties

   - For _**reactive**_ projects should be:

   ```shell
   reactive=true
   ```

   - For _**imperative**_ projects should be:

   ```shell
   reactive=false
   ```

   | Reference for **drivenAdapterType** | Name                 | Additional Options    |
   | ----------------------------------- | -------------------- | --------------------- |
   | generic                             | Empty Driven Adapter | --name [name]         |
   | jpa                                 | JPA Repository       | --secret [true-false] |
   | mongodb                             | Mongo Repository     | --secret [true-false] |
   | asynceventbus                       | Async Event Bus      |                       |

   ### This task will generate something like that:

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

5. The **`generateEntryPoint | gep`** task will generate a class in Infrastructure layer, this task has one required parameter `type`. <br>
   Whether you'll use generic one also parameter `name` is required.

   ```shell
   gradle generateEntryPoint --type=[entryPointType]
   gradle gep --type [entryPointType]
   ```

   | Reference for **entryPointType** | Name                                   | Additional Options |
   | -------------------------------- | -------------------------------------- | ------------------ |
   | generic                          | Empty Entry Point                      | --name [name]      |
   | restmvc                          | API REST (Spring Boot Starter Web)     |                    |
   | webflux                          | API REST (Spring Boot Starter WebFlux) |                    |

   ### This task will generate something like that:

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

## Validate Structure

6. The **`validateStructure | vs`** Validate that project references aren't violated.

   ```shell
   gradle validateStructure
   gradle vs
   ```

## Generate Pipeline

7. The **`generatePipeline | gpl`** task will generate CI pipeline inside the folder "./deployment/", this task has one required parameter `type`.

   ```shell
   gradle generatePipeline --type=[pipelineType]
   gradle gpl --type=[pipelineType]
   ```

   | Reference for **pipelineType** | Name           |
   | ------------------------------ | -------------- |
   | azure                          | Azure Pipeline |

## Delete Module

8. The **`deleteModule | dm`** task will delete a sub project, this task has one required parameter `module`.

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
