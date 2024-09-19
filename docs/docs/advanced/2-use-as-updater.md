---
sidebar_position: 2
---

# Dependency Updater

We have many open source java projects, so we try to keep dependencies updated, for this purpose whe have enabled the
plugin to be used as a dependency updater. This is not the `updateCleanArchitecture` task, we have a different task for
this.

## Internal Task

This task is the created for that, and you can use it by following the next steps:

1. Add plugin to your `build.gradle` project file as described in the [Getting Started](/docs/2-getting-started.md)
   section.
2. Add the following property to your `gradle.properties` file
   like [commons-jms](https://github.com/bancolombia/commons-jms) project

```properties
onlyUpdater=true
```

3. Now you can run the task `internalTask` or alias `it` to update the dependencies of your project.

```bash
./gradlew it --action UPDATE_DEPENDENCIES
```

This tas will update gradle dependencies and gradle plugin versions.