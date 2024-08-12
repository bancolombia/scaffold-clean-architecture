---
sidebar_position: 2
---

# Getting Started

## Requirements

- You need Java JRE installed (Java 17 or later).

- To use the [plugin](https://plugins.gradle.org/plugin/co.com.bancolombia.cleanArchitecture) you need Gradle version
  8.5 or later.

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

- [Generate Project](/scaffold-clean-architecture/docs/generate-project)
- [Generate Model](/scaffold-clean-architecture/docs/generate-model)
- [Generate Use Case](/scaffold-clean-architecture/docs/generate-use-case)
- [Generate Driven Adapter](/scaffold-clean-architecture/docs/generate-driven-adapter)
- [Generate Entry Point](/scaffold-clean-architecture/docs/generate-entry-point)
- [Generate Helper](/scaffold-clean-architecture/docs/generate-entry-point)
- [Generate Pipeline](/scaffold-clean-architecture/docs/generate-pipeline)
- [Generate Acceptance Test](/scaffold-clean-architecture/docs/generate-acceptance-test)
- [Generate Performance Test](/scaffold-clean-architecture/docs/generate-performance-test)
- [Validate Structure](/scaffold-clean-architecture/docs/validate-structure)
- [Delete Module](/scaffold-clean-architecture/docs/delete-module)
- [Update Project](/scaffold-clean-architecture/docs/update-project)