![](https://github.com/bancolombia/scaffold-clean-architecture/workflows/gradle-actions/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=bancolombia_scaffold-clean-architecture&metric=alert_status)](https://sonarcloud.io/dashboard?id=bancolombia_scaffold-clean-architecture)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=bancolombia_scaffold-clean-architecture&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=bancolombia_scaffold-clean-architecture)
[![codecov](https://codecov.io/gh/bancolombia/scaffold-clean-architecture/branch/master/graph/badge.svg)](https://codecov.io/gh/bancolombia/scaffold-clean-architecture)
[![GitHub license](https://img.shields.io/github/license/Naereen/StrapDown.js.svg)](https://github.com/bancolombia/scaffold-clean-architecture/blob/master/LICENSE)
# Scaffolding of Clean Architecture
Gradle plugin to create a java application based on Clean Architecture following our best practices!


Plugin Implementation  
===================
To use the plugin you need Gradle version 5 or later, to start add the following section into your 
**build.gradle** file.

```groovy
plugins {
 id "co.com.bancolombia.cleanArchitecture" version "1.6.0"
}
```



Tasks
=====
The Scaffolding Clean Architecture plugin will allow you create 6 task  :

1 The ```cleanArchitecture | ca``` task will generate a clean architecture structure in your project, this task have three optional parameters; ```package``` , ```type``` and ```name```.

 ```package = <package.we.need>```: You can specify the main or default package of your project. ```Default Value = co.com.bancolombia```

- ```type = <imperative | reactive>```: With this parameter the task will generate a POO project. ```Default Value = imperative```

-  ```name = NameProject```: This parameter is going to specify the name of the project. ```Default Value = cleanArchitecture```


```sh
gradle cleanArchitecture --package=co.com.bancolombia --type=imperative --name=NameProject
gradle ca 
```

2 The ```generateModel | gm``` task will generate a class and interface in model layer, this task have one required parameter ```name```.
```sh
gradle generateModel --name=[modelName]
gradle gm --name [modelName]
```
3 The ```generateUseCase | guc``` task will generate a class in model layer, this task have one required parameter ```name```.
```sh
gradle generateUseCase --name=[useCaseName]
gradle guc --name [useCaseName]
 ```
4 The ```generateDrivenAdapter | gda``` task will generate a class in Infrastructure layer, this task have one required parameter ```type```.
```sh
gradle generateDrivenAdapter --type=[drivenAdapterType]
gradle gda --type [drivenAdapterType]
 ```

|      Reference driven adapter value        | Name       |
| ------------------ | ------------ |
| GENERIC|Empty Driven Adapter |
| JPA|JPA Repository |
| MONGODB|Mongo Repository |
| ASYNCEVENTBUS|Async Event Bus |

5 The ```generateEntryPoint | gep``` task will generate a class in Infrastructure layer, this task have one required parameter ```type```.
```sh
gradle generateEntryPoint --type=[entryPointType]
gradle gep --type [entryPointType]
 ```
|      Reference entry point value      | Name       |
| ------------------ | ------------ |
| GENERIC|Empty Entry Point |
| RESTMVC|API REST (Spring Boot Starter Web) |
| WEBFLUX|API REST (Spring Boot Starter WebFlux) |



6 The ```validateStructure | vs``` Validate that project references aren't violated.
```sh
gradle validateStructure  
gradle vs
```

7 The ```generatePipeline | gpl``` task will generate CI pipeline inside the folder "./deployment/", this task have one required parameter ```type```.
```sh
gradle generatePipeline --type=[pipelineType]
gradle gpl --type=[pipelineType]
````
|      Reference pipeline value      | Name       |
| ------------------ | ------------ |
| AZURE|Azure Pipeline |


How I can help?
=============
Review the issues, we hear new ideas.

