---
sidebar_position: 2
---

# Getting Started

## Requirements

- You need Java JRE installed (Java 17 or later).

- To use the [plugin](https://plugins.gradle.org/plugin/co.com.bancolombia.cleanArchitecture) you need Gradle version
  8.8 or later.

## Using the plugin

To start add the following section into your **build.gradle** file.

![Gradle Plugin Portal Version](https://img.shields.io/gradle-plugin-portal/v/co.com.bancolombia.cleanArchitecture)

```groovy
plugins {
  id "co.com.bancolombia.cleanArchitecture" version "3.17.17"
}
```

Or if is a new project execute this script in the root directory of your project.

```sh
echo "plugins {
    id \"co.com.bancolombia.cleanArchitecture\" version \"3.17.17\"
}" > build.gradle
```

# Tasks

The Scaffolding Clean Architecture plugin will allow you the next tasks:

- [Generate Project](tasks/generate-project)
- [Generate Model](tasks/generate-model)
- [Generate Use Case](tasks/generate-use-case)
- [Generate Driven Adapter](tasks/generate-driven-adapter)
- [Generate Entry Point](tasks/generate-entry-point)
- [Generate Helper](tasks/generate-entry-point)
- [Generate Pipeline](tasks/generate-pipeline)
- [Generate Acceptance Test](tasks/generate-acceptance-test)
- [Generate Performance Test](tasks/generate-performance-test)
- [Validate Structure](tasks/validate-structure)
- [Delete Module](tasks/delete-module)
- [Update Project](tasks/update-project)
