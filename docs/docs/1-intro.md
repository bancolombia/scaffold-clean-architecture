---
sidebar_position: 1
---

# Overview

![Gradle Plugin Portal Version](https://img.shields.io/gradle-plugin-portal/v/co.com.bancolombia.cleanArchitecture)
![](https://github.com/bancolombia/scaffold-clean-architecture/workflows/gradle-actions/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=bancolombia_scaffold-clean-architecture&metric=alert_status)](https://sonarcloud.io/dashboard?id=bancolombia_scaffold-clean-architecture)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=bancolombia_scaffold-clean-architecture&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=bancolombia_scaffold-clean-architecture)
[![codecov](https://codecov.io/gh/bancolombia/scaffold-clean-architecture/branch/master/graph/badge.svg)](https://codecov.io/gh/bancolombia/scaffold-clean-architecture)
[![GitHub license](https://shields.io/badge/license-Apache%202-blue)](https://github.com/bancolombia/scaffold-clean-architecture/blob/master/LICENSE)
[![Scorecards supply-chain security](https://github.com/bancolombia/scaffold-clean-architecture/actions/workflows/scorecards-analysis.yml/badge.svg)](https://github.com/bancolombia/scaffold-clean-architecture/actions/workflows/scorecards-analysis.yml)

## Scaffolding of Clean Architecture

![Clean Architecture](https://miro.medium.com/max/1400/1*ZdlHz8B0-qu9Y-QO3AXR_w.png)

The purpose of Scaffold Clean Architecture Gradle Plugin is to provide a way to create a project with a clean
architecture structure, with the necessary dependencies and configurations to start developing a project with the Clean
Architecture principles.

We have adopted the Clean Architecture principles proposed by Robert C. Martin, which are based on the separation of
concerns and the dependency rule, which states that the dependencies must always point inwards, from the outer layers to
the inner layers.

The below image shows the layers of the Clean Architecture applied, this is built on a multimodule gradle project
structure.

# Layers

## Domain

We have the Domain layer, which is the most internal layer of the architecture, it belongs to the domain layer and
encapsulates the logic and business rules through domain models and use cases.
This should define the ports as interfaces (ports) and the domain models and use cases as classes.

This layer is composed by two gradle modules called `model` and `usecase`.

`model` module contains the domain models and entities, which are the representation of the business rules and logic. It
also defines the ports (interfaces) that will be used in the `usecase` module.
`usecase` module contains the use cases of the system, defines the application logic and reacts to invocations from
the `entry points` modules, orchestrating the flows towards the `domain` module and using the defined ports.

## Infrastructure

In this layer we will have the detailed technologies and implementations of the ports defined in the domain layer. This
layer is composed by three group of modules called `entry-points`, `driven-adapters` and `helpers`.

`entry-poits` modules contains the entry points of the application or the start of the business flows. These can be REST
controllers, Kafka consumers, etc.
`driven-adapters` modules represent external implementations to our system, such as connections to rest services, soap,
databases, reading flat files, and in particular any source and data source with which we must interact.
`helpers` modules contains general utilities for the Driven Adapters and Entry Points.

## Application

This module is the most external of the architecture, it is responsible for assembling the different modules, resolving
dependencies and creating the beans of the use cases automatically, injecting into these concrete instances of the
declared dependencies. It also starts the application (it is the only module in the project where we will find the
function "public static void main(String[] args)").

# Features

Our scaffold is able to generate imperative and reactive projects. The imperative project is based on Spring Boot and
the reactive project is based on Spring WebFlux.

## Project Reactor

[Reactor](https://projectreactor.io) is a highly optimized reactive library for
building efficient, non-blocking applications on the JVM based on the
[Reactive Streams Specification](https://github.com/reactive-streams/reactive-streams-jvm).
Reactor based applications can sustain very high throughput message rates
and operate with a very low memory footprint,
making it suitable for building efficient event-driven applications using
the microservices architecture.

Reactor implements two publishers
[Flux\<T>](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html) and
[Mono\<T>](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html),
both of which support non-blocking back-pressure.
This enables exchange of data between threads with well-defined memory usage,
avoiding unnecessary intermediate buffering or blocking.

## Reactive Commons

[Reactive Commons](https://reactivecommons.org/reactive-commons-java) is a reactive API for asynchronous message driven
communication based on Reactor.
Reactive Commons API enables messages to be published over a event bus like RabbitMQ and consumed using functional APIs
with non-blocking back-pressure and low overheads.
It enables applications using Reactor to use RabbitMQ as a message bus, integrating it with other systems to provide an
end-to-end reactive system.

When we talk about asynchronous message driven communication, we can use several semantic ways to use the term "
message". So, we can talk about Events, Commands and Queries.

## Secrets Manager

Our adapters can be configured to use the Secrets Manager to retrieve the secrets needed to connect to the external
systems. This way, the secrets are not stored in the code, but are retrieved from the Secrets Manager or another service
at runtime.

You can know more about the Secrets Manager utility [here](https://github.com/bancolombia/secrets-manager).

## Versioning

Reactive Commons uses [Semantic Versioning Specification](https://semver.org)

Reactive Commons uses a MAJOR.MINOR.PATCH scheme, whereby an increment in:

MAJOR version when you make incompatible API changes,

MINOR version when you add functionality in a backwards compatible manner, and

PATCH version when you make backwards compatible bug fixes. Additional labels for pre-release and build metadata are
available as extensions to the MAJOR.MINOR.PATCH format. == New & Noteworthy

# Analytics

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
- Project language `java`
- Task name that was run.
- Workspace information like language, user that is running the task.
- For generate use case, generate model, generate helper and delete module tasks, the name will be sent.
- For all tasks, the type and name, the time it took to run the task, and project type (reactive, imperative).

<br/><br/>

# Whats Next?

Read
more [About Clean Architecture](https://medium.com/bancolombia-tech/clean-architecture-aislando-los-detalles-4f9530f35d7a)

We also have an [Elixir](https://bancolombia.github.io/scaffold-clean-architecture-ex/) variant

# How can I help?

Review the issues, we hear new ideas. Read
more [Contributing](https://github.com/bancolombia/scaffold-clean-architecture/wiki/Contributing)
