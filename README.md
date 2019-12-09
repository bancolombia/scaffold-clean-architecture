# Scaffold clean Architecture
Gradle plugin to create a clean application in Java that already works, It follows our best practices!


Usage - Applying the plugin
===================
To use the plugin with Gradle 5 or later, add the following to your 
build.gradle file.

```groovy
plugins {
 id "co.com.bancolombia.scaffoldJavaCleanArchitecture" version "0.32"
}
```



Tasks
=====
The Scaffold Clean Architecture plugin will create 1 task you can use (more tasks will come) :

1. The ```cleanArchitecture``` task will cause  Scaffold Project Clean Architecture, this task have 2 parameters ```package``` and ```type``` are required to use this task.

```sh
gradle cleanArchitecture --package=co/com/bancolombia --type=imperative
```



Configuration
=============

The behavior of this plugin is controlled by setting various options in the tasks.

- ```package = <package/we/need>```: You must specify the package that will have the project.

- ```type = <reactive | imperative>```: (It's coming) The project already works, but you should know what type of project you need to generate.

How I can help?
=============
The following functionalities are within the roadMap of this script:

    - Task to generate entry points
    - Task to generate driven adapters
    - Task to generate models
    - Task to generate usecase
