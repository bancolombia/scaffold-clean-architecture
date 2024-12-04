---
sidebar_position: 5
---

# Contributing

## How to contribute

### Step 1: Fork

Fork the project [on GitHub](https://github.com/bancolombia/scaffold-clean-architecture) and clone your fork
locally.

```text
$ git clone git@github.com:username/node.git
$ cd node
$ git remote add upstream https://github.com/bancolombia/scaffold-clean-architecture.git
$ git fetch upstream
```

If you haven't done it yet, Configure `git` so that it knows who you are:

```text
$ git config user.name "My name"
$ git config user.email "my personal or business email"
```

You can use any name/email address you prefer here. We only use the
metadata generated by `git` using this configuration for properly attributing
your changes to you in the `AUTHORS` file and the changelog.

If you would like for the GitHub UI to link the commit to your account
and award you the `Contributor` label after the changes have been merged,
make sure this local email is also added to your
[GitHub email list](https://github.com/settings/emails).

### Step 2: Hook
We have a pre-commit hook to format the code and warrant the unit test pass, you only need to install this with the following command.

```text
$ ./gradlew installGitHooks
```
if you will be interest to format the code in any time , execute this command

```text
$ ./gradlew goJF
```
### Step 3: Branch

As a best practice to keep your development environment as organized as
possible, create local branches to work within. These should also be created
directly off of the `master` branch.

```text
$ git checkout -b my-branch -t upstream/master
```

We suggest that the name of the branch be separated with hyphens and be concise with what you want to solve, it does not have to start with the word feature or fix.

Which branch:
* Make your pull request target master.

### Step 4: Make the changes

You could follow this [guide](https://github.com/bancolombia/scaffold-clean-architecture/wiki/Implementing-or-changing-a-module)

### Pull request criteria

* If you are attending an issue, add this id in your pull request.
* Include tests
* For new features consider adding new documentation item in Readme file
* Also, look at the GitHub's Pull Request guide

### General info
* Comment on issues or pull requests.
* Please suggest changes to documentation when you find something unclear.
* You can create a fork of Scaffolding project in no time. Go to the github project and "Create your own fork". Create a new branch, commit, ..., when you're ready let us know about your pull request so we can discuss it and merge the PR!

### More on pull requests

The  Scaffolding project has now a continuous release bot, that means that each merged pull request will be automatically released in a newer version of the plugin. For that reason each pull request has to go through a thorough review and/or discussion.

Things we pay attention in a PR :

* On pull requests, please document the change, what it brings, what is the benefit.
* Clean commit history in the topic branch in your fork of the repository, even during review. That means that commits are rebased and squashed if necessary, so that each commit clearly changes one things and there are no extraneous fix-ups.
* In the code, always test your feature / change, in unit tests.

#### UpgradeAction
From version 2.3.0 you should implement an UpgradeAction, this implementation will be required if you had changed any structural file like:
- main.gradle
- build.gradle
- applications/app-service/build.gradle
- gradle.properties
- other similar

This upgrade action is intended to keep upgraded the projects that have been created with a previous plugin version, allowing it to be able to have the new features by simple running the `gradle updateCleanArchitecture` task.

The UpgradeAction should be implemented in the package `co.com.bancolombia.factory.upgrades.actions` and should have an specific date based name, which allows that the update occurs in the order which it is created.

The standard is UpgradeY(year)M(month)D(day of month)

If your pull request have structural changes and it does not comes with an UpgradeAction, it will be rejected.

#### New Tasks
If your contribution will have new tasks, you can use one of the existing abstractions, and you should use the annotation @CATask providing a name, description and a shortcut
- [AbstractCleanArchitectureDefaultTask](https://github.com/bancolombia/scaffold-clean-architecture/blob/master/src/main/java/co/com/bancolombia/task/AbstractCleanArchitectureDefaultTask.java)
- [AbstractResolvableTypeTask](https://github.com/bancolombia/scaffold-clean-architecture/blob/master/src/main/java/co/com/bancolombia/task/AbstractCleanArchitectureDefaultTask.java)

#### New Module Factories

If your contribution has a new item of type DrivenAdapter, EntryPoint, PerformaceTest, AcceptanceTest, Helper or Pipeline these modules factories should be named and located following the next rule.

Name: Should contain the prefix of his type, for example DrivenAdapter -> DrivenAdapterR2DBC, project will infer the driven adapter type as `R2DBC`
Package Location: Should be located in the corresponding package as you can see in the next table:

| Type           | Package                                      |
|----------------|----------------------------------------------|
| DrivenAdapter  | co.com.bancolombia.factory.adapters          |
| EntryPoint     | co.com.bancolombia.factory.entrypoints       |
| AcceptanceTest | co.com.bancolombia.factory.tests.acceptance  |
| PerformaceTest | co.com.bancolombia.factory.tests.performance |
| Helper         | co.com.bancolombia.factory.helpers           |
| Pipeline       | co.com.bancolombia.factory.pipelines         |

#### Commits

Good commit messages serve at least three important purposes:

- To speed up the reviewing process
- To help us write a good release note
  To help the future maintainers (it could be you!), say five years into the future, to find out why a particular change was made to the code or why a specific feature was added.

Rules:

1. You should use conventional commits, you can find more information [here](https://www.conventionalcommits.org/en/v1.0.0/)
2. Write the summary line and description of what you have done in the imperative mood, that is as if you were commanding someone. Start the line with "Fix", "Add", "Change" instead of "Fixed", "Added", "Changed"
3. Contain a short description of the change (preferably 50 characters or less, and no more than 72 characters)
4. be entirely in lowercase with the exception of proper nouns, acronyms, and the words that refer to code, like function/variable names
5. Keep the second line blank.
6. If your commit introduces a breaking change (semver-major), it should contain an explanation about the reason of the breaking change, which situation would trigger the breaking change and what is the exact change.
7. Use the body to explain what and why vs. how

For example:

```
test: add functional test for non-standard base libraries
ci: support for Ubuntu 18 & CentOS 7
deps: upgrade express to 4.17.1
deps: standardjs as style guide, linter, and formatter
```

## Implementing or changing a module

### Introduction

Like you can research by using the project, it generates a multi-module gradle project, each module represents a
subproject that has some considerations:

- applications:
    - app-service: Main bootable application with setup configurations.
- domain:
    - model: Domain models for the application, it also has the ports definitions.
    - use-case: Domain business logic for the application processes.
- infrastructure:
    - entry-points: Some modules which exposes the use-cases capabilities through an API of any protocol
    - driven-adapter: Some modules that implements the ports defined in the domain model. Could be for example an access
      to a database.
    - helpers: Some modules that helps the adapters, could be for example a transformation module.
- other non gradle modules: Could be test projects, pipelines, etc...

### Templating

All our modules are generated based on templating using mustache, this templates are located in the resources directory
of the project.
Actually this templates should consider some variables that will impact the template, this variables are:

- lombok: Boolean variable enabled by default, that implies that if you will use any lombok annotation you should
  consider implement the vanilla version when lombok is disabled.
- metrics: Boolean variable enabled by default, that implies that you should implement some metric generator only when
  enabled.
- language: (java|kotlin) you should use this value if you need to determine when run a custom configuration depending
  on the language.
- reactive: Boolean variable disabled by default, this variable should be used to determine the resources directory of
  the templates by following the standard \<module-name> for non reactive and \<module-name>-reactive for reactive
  modules.

#### Predefined variables

- **projectName**: the project name assigned by the user when running the `cleanArchitecture` task.
- **projectNameLower**: the same value of projectName but in lower case.
- **package**: package defined by the user when the project was created, for example**: `co.com.bancolombia.sample`
- **packagePath**: package path of the package defined, for example: `co/com/bancolombia/sample`
- **language**: language of the project, could be `java` or `kotlin`
- all constants defined in [
  `Constants`](https://github.com/bancolombia/scaffold-clean-architecture/blob/master/src/main/java/co/com/bancolombia/Constants.java)
  with the same constant name, for example:
    - **PLUGIN_VERSION**: `Constants.PLUGIN_VERSION`

### Implementing a new module

When you will implement a new module related to infrastructure layer you should keep in mind the required modifications.

1. Add the resource templates in the correct location depending of your module type and the project type (reactive or
   not reactive).

   |module type|resources path|
   |----|---------------|
   |Driven Adapter|resources/driven-adapter/\<module-name>(-reactive)|
   |Entry Point|resources/entry-point/\<module-name>(-reactive)|
   |Helper|resources/helper/\<module-name>(-reactive)|
   |Pipeline|resources/pipeline/\<module-name>|
   |Test|resources/test/\<module-name>|

2. Implement the ModuleFactory for the new module type in the correct package.

   |module type|package|
   |----|---------------|
   |Driven Adapter|co.com.bancolombia.factory.adapters|
   |Entry Point|co.com.bancolombia.factory.entrypoints|
   |Helper|co.com.bancolombia.factory.helpers|
   |Pipeline|co.com.bancolombia.factory.pipelines|
   |Test|co.com.bancolombia.factory.tests|

   When you implement a new `ModuleFactory` which definition is:
    ```java
    public interface ModuleFactory {
        void buildModule(ModuleBuilder builder) throws IOException, CleanException;
    }
    ```

   You will receive a `ModuleBuilder` instance which has a lot of utilities to achieve the module generation by doing
   operation on the project files.

   Some common operations are:

- setupFromTemplate
  ```java
    public void setupFromTemplate(String resourceGroup) throws IOException, ParamNotFoundException {}
  ```

This will load the template definition from the resource directory, this definition should be a json file called
`definition.json`   which has the structure:

```json
{
  "folders": [
    "infrastructure/driven-adapters/dynamo-db/src/test/{{language}}/{{packagePath}}/dynamodb/helper"
  ],
  "files": {},
  "java": {
    "driven-adapter/dynamo-db/build.gradle.mustache": "infrastructure/driven-adapters/dynamo-db/build.gradle"
  },
  "kotlin": {
    "driven-adapter/dynamo-db/build.gradle.kts.mustache": "infrastructure/driven-adapters/dynamo-db/build.gradle.kts"
  }
}
```

Where:

- folders: new directories to be created, usually are empty test directories.
- files: file map to be created for both languages `java` and `kotlin`.
- java: file map to be created only for java projects.
- kotlin: file map to be created only for kotlin projects.

file map refers to, in the above example for java exists the key: `driven-adapter/dynamo-db/build.gradle.mustache` and
the value `infrastructure/driven-adapters/dynamo-db/build.gradle`, it means that the file with that key will be
generated in the value path. for paths value you could use variables for example
`infrastructure/driven-adapters/dynamo-db/src/main/{{language}}/{{packagePath}}/dynamodb/config/DynamoDBConfig.java`

- appendToSettings
  ```java
  public void appendToSettings(String module, String baseDir) throws IOException {}
  ```

This method will add a new module to the `setting.gradle` file, you should pass the module name and the module location.

- appendDependencyToModule
  ```java
  public void appendDependencyToModule(String module, String dependency) throws IOException {}
  ```

This method adds a new dependency for the indicated module, you could use the method `buildImplementationFromProject`
located in Utils class to create the dependency definition depending on the language. for example:

```java
    String dependency = Utils.buildImplementationFromProject(builder.isKotlin(), ":dynamodb");
    builder.appendDependencyToModule(APP_SERVICE, dependency);
```

- appendToProperties
  ```java
  public ObjectNode appendToProperties(String path) throws IOException {}
  ```

This method gets or creates a new ObjectNode of the application.yaml file, and you can add the custom properties to this
file. For example:
The next code adds the property `spring.datasource.url=jdbc:h2:mem:test` and the property
`spring.datasource.driverClassName=org.h2.Driver`

```java
    builder
        .appendToProperties("spring.datasource")
        .put("url","jdbc:h2:mem:test")
        .put("driverClassName","org.h2.Driver");
```

3. Add the type of module in the correct enum.

   |module type|enum|
   |----|---------------|
   |Driven Adapter|co.com.bancolombia.factory.adapters.ModuleFactoryDrivenAdapter.DrivenAdapterType|
   |Entry Point|co.com.bancolombia.factory.entrypoints.ModuleFactory.EntryPoint|
   |Helper|co.com.bancolombia.factory.helpers.ModuleFactoryHelpers.EntryPointType|
   |Pipeline|co.com.bancolombia.factory.pipelines.ModuleFactoryPipeline.PipelineType|
   |Test|co.com.bancolombia.factory.tests.ModuleFactoryTests|

4. Add the instantiation block in the correct class according to the new type.

   |module type|factory class|
   |----|---------------|
   |Driven Adapter|co.com.bancolombia.factory.adapters.ModuleFactoryDrivenAdapter|
   |Entry Point|co.com.bancolombia.factory.entrypoints.ModuleFactory|
   |Helper|co.com.bancolombia.factory.helpers.ModuleFactoryHelpers|
   |Pipeline|co.com.bancolombia.factory.pipelines.ModuleFactoryPipeline|
   |Test|co.com.bancolombia.factory.tests.ModuleFactoryTests|

5. Add new required parameters in the task (Optional if required)

   For example the `generic` type of the driven adapter task requires a `name` argument, so in the task there is the
   option:
    ```java
    @Option(option = "name", description = "Set driven adapter name when GENERIC type")
    public void setName(String name) {
        this.name = name;
    }
    ```

   And then adds the value to the params
    ```java
    builder.addParam("task-param-name", name);
    ```

   The use the param in code or template
   In code:
    ```java
    builder.getParam("task-param-name")
    ```

### Modifying a module

    You should keep in mind the above documentation for creating a new module, and basically modify the necessary templates and the corresponding module factory.

### Testing in local

1. Make the changes which you want to try.
2. Change the version of the plugin for your custom own version in the `gradle.properties` file, for example:
   systemProp.version=2.4.1.1
3. Publish the plugin to mavenLocal by running:
   ```shell
   gradle publishToMavenLocal
   ```
1. Generates a new clean architecture project using the local project.

To achieve it you have two options:

- Using build.gradle.

  Create a build.gradle file in an empty folder with the next content:

     ```gradle
     buildscript {
         repositories {
            mavenLocal()
         }
         dependencies {
            classpath "co.com.bancolombia.cleanArchitecture:scaffold-clean-architecture:2.4.1.1"
         }
     }

     apply plugin: "co.com.bancolombia.cleanArchitecture"
     ```

  Then run the respective `gradle ca` task with your custom arguments.
  **You should modify again the `build.gradle` to use the above syntax**

- Using `settings.gradle`

  Create a build.gradle file in an empty folder with the next content:

     ```gradle
     plugins {
         id "co.com.bancolombia.cleanArchitecture" version "2.4.1.1"
     }
     ```
  Create a settings.gradle file in an empty folder with the next content:

     ```gradle
     pluginManagement {
         repositories {
             mavenLocal()
             gradlePluginPortal()
         }
     }
     ```

  Then run the respective `gradle ca` task with your custom arguments.
  **You should modify again the `settings.gradle` adding the content on the beginning of the file**

  Then you can use the other tasks related to the plugin.


