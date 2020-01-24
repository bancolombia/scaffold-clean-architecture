[![Build Status](https://travis-ci.com/bancolombia/scaffold-clean-architecture.svg?branch=master)](https://travis-ci.com/bancolombia/scaffold-clean-architecture)
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
 id "co.com.bancolombia.cleanArchitecture" version "1.5.1"
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
4 The ```generateDrivenAdapter | gda``` task will generate a class in Infrastructure layer, this task have one required parameter ```value```.
```sh
gradle generateDrivenAdapter --value=[referenceNumberDrivenAdapter]
gradle gda --value [referenceNumberDrivenAdapter]
 ```

|      Reference number driven adapter        | Name       |
| ------------------ | ------------ |
| 1|JPA Repository |
| 2|Mongo Repository |
| 3|Secrets Manager Consumer |
| 4|Async Event Bus |

5 The ```generateEntryPoint | gep``` task will generate a class in Infrastructure layer, this task have one required parameter ```value```.
```sh
gradle generateEntryPoint --value=referenceNumberEntryPoint
gradle gep --value referenceNumberEntryPoint
 ```
|      Reference number entry point      | Name       |
| ------------------ | ------------ |
| 1|API REST (Spring Boot Starter Web) |
| 2|API REST (Spring Boot Starter WebFlux) |



6 The ```validateStructure | vs``` Validate that project references aren't violated.
```sh
gradle validateStructure  
gradle vs
```



How I can help?
=============
Review the issues, we hear new ideas.

