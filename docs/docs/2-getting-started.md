---
sidebar_position: 2
---

# Getting Started

## Requirements

- You need Java JRE installed (Java 17 or later).

- To use the [plugin](https://plugins.gradle.org/plugin/co.com.bancolombia.cleanArchitecture) you need Gradle version
  8.8 or later.

:::note[Recommended Gradle Version]
For optimal performance and to enable all [Gradle cache properties](advanced/gradle-cache), it is recommended to use
Gradle [8.14.3 or higher](https://gradle.org/releases/).
:::

## Using the plugin

To start you need to create a directory for the project, then you should add a `build.gradle` file with following
content into your **build.gradle** file.

![Gradle Plugin Portal Version](https://img.shields.io/gradle-plugin-portal/v/co.com.bancolombia.cleanArchitecture)

```groovy
plugins {
    id 'co.com.bancolombia.cleanArchitecture' version '3.20.15'
}
```

Then you can list the tasks available with the command:

```bash
gradle tasks
```

---
You can follow one of the next steps to create a quick start project with commands

## Quick Start

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

<Tabs>
  <TabItem value="mac" label="Mac OS / Linux" default>

```sh
mkdir scaffold-quick-start
cd scaffold-quick-start
echo "plugins {
    id 'co.com.bancolombia.cleanArchitecture' version '3.20.15'
}" > build.gradle
gradle ca --name=ScaffoldQuickStart
./gradlew gep --type webflux
./gradlew bootRun
```

  </TabItem>
  <TabItem value="windows" label="Windows">

> PowerShell

```powershell
# Create a directory and navigate into it
New-Item -ItemType Directory -Name "scaffold-quick-start"
Set-Location -Path "scaffold-quick-start"

# Create the build.gradle file with the specified content
@"
plugins {
    id 'co.com.bancolombia.cleanArchitecture' version '3.20.15'
}
"@ | Set-Content -Path "build.gradle"

# Run the Gradle commands
gradle ca --name=ScaffoldQuickStart
.\gradlew gep --type webflux
.\gradlew bootRun
```

> CMD

```cmd
@echo off

:: Create a directory and navigate into it
mkdir scaffold-quick-start
cd scaffold-quick-start

:: Create the build.gradle file with the specified content
(
echo plugins {
echo     id 'co.com.bancolombia.cleanArchitecture' version '3.20.15'
echo }
) > build.gradle

:: Run the Gradle commands
gradle ca --name=ScaffoldQuickStart
gradlew gep --type webflux
gradlew bootRun

```

  </TabItem>
</Tabs>

Then open your browser and go to [http://localhost:8080/api/usecase/path](http://localhost:8080/api/usecase/path) and it
will reply with an empty string, to change the response you can modify the `Handler` class in the
`entry-points/reactive-web`

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
