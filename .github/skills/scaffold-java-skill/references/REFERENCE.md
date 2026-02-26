# Getting Started

## Requirements

- You need Java JRE installed (Java 17 or later).

- To use the [plugin](https://plugins.gradle.org/plugin/co.com.bancolombia.cleanArchitecture) you need Gradle version
  9.2.1 or later.

## Using the plugin

To start you need to create a directory for the project, then you should add a `build.gradle` file with following
content into your **build.gradle** file.

```groovy
plugins {
    id 'co.com.bancolombia.cleanArchitecture' version '4.1.0'
}
```

Then you can list the tasks available with the command:

```bash
gradle tasks
```

---
You can follow one of the next steps to create a quick start project with commands

# Tasks

The Scaffolding Clean Architecture plugin will allow you the next tasks:

- [Generate Project](tasks/1-generate-project.md)
- [Generate Model](tasks/2-generate-model.md)
- [Generate Use Case](tasks/3-generate-use-case.md)
- [Generate Driven Adapter](tasks/4-generate-driven-adapter.md)
- [Generate Entry Point](tasks/5-generate-entry-point.md)
- [Generate Helper](tasks/6-generate-helper.md)
- [Generate Pipeline](tasks/7-generate-pipeline.md)
- [Generate Acceptance Test](tasks/8-generate-acceptance-test.md)
- [Generate Performance Test](tasks/9-generate-performance-test.md)
- [Validate Structure](tasks/10-validate-structure.md)
- [Delete Module](tasks/11-delete-module.md)
- [Update Project](tasks/12-update-project.md)
