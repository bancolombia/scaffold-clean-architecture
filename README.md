[![Build Status](https://travis-ci.com/bancolombia/scaffold-clean-architecture.svg?branch=master)](https://travis-ci.com/bancolombia/scaffold-clean-architecture)
[![codecov](https://codecov.io/gh/bancolombia/scaffold-clean-architecture/branch/master/graph/badge.svg)](https://codecov.io/gh/bancolombia/scaffold-clean-architecture)
[![GitHub license](https://img.shields.io/github/license/Naereen/StrapDown.js.svg)](https://github.com/bancolombia/scaffold-clean-architecture/blob/master/LICENSE)
# Scaffolding of Clean Architecture
Gradle plugin to create a java application based on Clean Architecture following our best practices!


Plugin Implementation  
===================
To use the plugin you need Gradle version 5 or later, to start add the following section into your 
build.gradle file.

```groovy
plugins {
 id "co.com.bancolombia.cleanArchitecture" version "0.50"
}
```



Tasks
=====
The Scaffolding Clean Architecture plugin will allow you create 1 task (more tasks are comming) :

1 The ```cleanArchitecture``` task will generate a clean architecture structure in your project, this task have three optional parameters; ```package``` , ```type``` and ```name```.

 ```package = <package.we.need>```: You can specify the main or default package of your project. ```Default Value = co.com.bancolombia```

- ```type = <imperative>```: With this parameter the task will generate a POO project. ```Default Value = imperative```

-  ```name = NameProject```: This parameter is going to specify the name of the project. ```Default Value = cleanArchitecture```


```sh
gradle cleanArchitecture --package=co.com.bancolombia --type=imperative --name=NameProject
```

2 The ```generateModel``` task will generate a class and interface in model layer, this task have one required parameter ```name```.
```sh
gradle generateModel --name=modelName
```

3 The ```validateStructure``` Validate that project references are not violated.
```sh
gradle validateStructure
```

How I can help?
=============
The following functionalities are within the road map of this script:

    - Task to generate entry points
    - Task to generate driven adapters
    - Task to generate usecase
