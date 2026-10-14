---
sidebar_position: 2
---

# Generate Project

The **`cleanArchitecture | ca`** task will generate a clean architecture structure in your project.

**Caution**: If you run this task on an existing project it will replace generated root files such
as `settings.gradle`, `build.gradle`, and `gradle.properties`.

| Parameter   | Description                                                              | Usage                              | Default Value        |
|-------------|--------------------------------------------------------------------------|------------------------------------|----------------------|
| package     | You can specify the main or default package of your project              | `--package=<desired.base.package>` | `co.com.bancolombia` |
| type        | Define if project shoud be created around reactive or imperative aproach | `--type=<imperative or reactive>`  | `reactive`           |
| name        | Sets projects name                                                       | `--name=<Project name>`            | `cleanArchitecture`  |
| lombok      | Specify if you want to use this plugin                                   | `--lombok=<true or false>`         | `true`               |
| metrics     | Specify if you want to enable this feature with micrometer               | `--metrics=<true or false>`        | `true`               |
| mutation    | Declare Pitest in the root plugins block for mutation testing             | `--mutation=<true or false>`       | `true`               |
| java-version | Sets Java version                                                        | `--java-version=<17, 21 or 25>`    | `25`                 |

Examples:

```shell
gradle cleanArchitecture --package=co.com.bancolombia --type=reactive --name=NameProject --lombok=true
```

Short form
```shell
gradle ca --package=co.com.bancolombia --type=reactive --name=NameProject --lombok=true
```

**_The generated structure will look like this for java:_**

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
   ┣ 📜README.md
   ┗ 📜settings.gradle
   ```

The generated Settings convention plugin discovers modules with a `build.gradle` under
`applications`, `domain`, and `infrastructure`; `settings.gradle` does not need static module
includes. The v5 structure does not generate `main.gradle`. The `mutation` option controls whether
the Pitest plugin is declared in the root `plugins` block; it is not written to `gradle.properties`.
