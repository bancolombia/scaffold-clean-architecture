---
sidebar_position: 3
---

# Optimizing Builds with the Gradle Cache

Gradle offers powerful caching mechanisms to significantly speed up build times by reusing outputs from previous
executions. This is crucial for improving productivity, especially in large projects.

To understand how caches work, it's important to first know the Gradle build lifecycle.

## Build Lifecycle

The [Gradle Build Lifecycle](https://docs.gradle.org/current/userguide/build_lifecycle.html) consists of three phases:

1. **Initialization**
    - Detects the `settings.gradle(.kts)` file.
    - Creates a `Settings` instance.
    - Evaluates the settings file to determine which projects (and included builds) make up the build.
    - Creates a `Project` instance for every project.

2. **Configuration**
    - Evaluates the build scripts, `build.gradle(.kts)`, of every project participating in the build.
    - Creates a task graph for requested tasks.

3. **Execution**
    - Schedules and executes the selected tasks.
    - Dependencies between tasks determine execution order.
    - Execution of tasks can occur in parallel.

Gradle caches focus on optimizing the **Configuration** and **Execution** phases.

There are two main types of cache in Gradle:

1. **Build Cache:** Optimizes the **Execution** phase by storing task outputs.
2. **Configuration Cache:** Optimizes the **Configuration** phase by storing the task graph and project configuration.

## Build Cache

The [build cache](https://docs.gradle.org/current/userguide/build_cache.html) saves time by reusing task outputs from
previous builds. It works by storing output artifacts and retrieving them when it's determined that a task's inputs have
not changed. This cache can be **local** (on the developer's machine) or **remote** (shared via an HTTP server),
allowing the entire team to benefit from builds done by others.

### Advantages

- **Reduced Build Times:** Avoids re-running expensive tasks like compilation and tests.
- **Efficient Collaboration:** The local cache speeds up individual work, while the remote cache allows the entire team,
  including CI servers, to share and reuse results.
- **Simple Implementation:** It's easily enabled with a single property.

### Configure the Build Cache

To enable the build cache, add the following line to your `gradle.properties` file:

```properties
org.gradle.caching=true
```

You can also enable it for a single run using the `--build-cache` flag on the command line.

```bash
./gradlew build --build-cache
```

## Configuration Cache

The [configuration cache](https://docs.gradle.org/current/userguide/configuration_cache.html) is a more modern feature
that further improves performance by completely skipping the configuration phase of the build lifecycle. If the build
scripts and other configuration inputs have not changed, Gradle can load the task graph directly from the cache and move
on to the execution phase.

Since **Gradle 9.0**, the configuration cache has become
the [preferred mode of execution](https://blog.gradle.org/road-to-configuration-cache#preferred-mode-of-execution),
highlighting its stability and importance for accelerating builds.

### Advantages

- **Skipping the Configuration Phase:** Saves a significant amount of time, especially in complex projects with many
  modules.
- **Parallel Task Execution:** When enabled, tasks run in parallel by default (respecting dependencies).
- **Optimized Memory Usage:** Gradle can release the memory used during configuration once the task graph is cached.

### Configure the Configuration Cache

To enable the configuration cache, add the following property to your `gradle.properties` file:

```properties
org.gradle.configuration-cache=true
```

Since this is a relatively new feature, some plugins may not be fully compatible. If you encounter problems, you can
have Gradle treat them as warnings instead of errors, which facilitates migration:

```properties
org.gradle.configuration-cache.problems=warn
```

### Parallel Configuration Caching

By default, Configuration Cache storing and loading are sequential. To improve performance, you can
enable [parallel processing](https://docs.gradle.org/current/userguide/configuration_cache_enabling.html#config_cache:usage:parallel).
However, this is an **incubating feature** and may not be compatible with all builds.

A common symptom of incompatibility is `ConcurrentModificationException` errors. However, this feature is expected to
work well for decoupled multi-project builds. To enable it, add the following property to the `gradle.properties` file:

```properties
org.gradle.configuration-cache.parallel=true
```

### Cache Integrity Check

To facilitate debugging complex issues, especially those related to serialization, Gradle 8.14 introduced
a [stricter integrity check](https://docs.gradle.org/current/userguide/configuration_cache_debugging.html#config_cache:integrity_check).
This option helps identify inconsistencies earlier, although it may slow down cache operations and increase their size.

It is a useful diagnostic tool when cache corruption is suspected. To enable it, add the following property to the
`gradle.properties` file:

```properties
org.gradle.configuration-cache.integrity-check=true
```

## Cache Configuration in Scaffold Projects

Projects generated with the **Scaffold Clean Architecture** come pre-configured to take full advantage of Gradle's
caching capabilities. The `gradle.properties` file already includes the properties to enable both the Build Cache and
the Configuration Cache:

:::note[Gradle Version Recommendation]
To enable all these properties and ensure maximum compatibility, it is recommended to use [**Gradle 8.14.3 or higher
**](https://gradle.org/releases/).
:::

```properties
org.gradle.caching=true
org.gradle.configuration-cache=true
org.gradle.configuration-cache.integrity-check=true
org.gradle.configuration-cache.parallel=true
```

In addition to enabling the caches, the scaffold follows
the [best practice recommended by Gradle](https://docs.gradle.org/current/userguide/build_cache.html#sec:build_cache_configure_local)
of setting up a **project-specific local cache**. Instead of using Gradle's global cache directory (`GRADLE_USER_HOME`),
a `build-cache` directory is created at the root of the project.

This is configured in the `settings.gradle` file:

```groovy
buildCache {
    local {
        directory = new File(rootDir, 'build-cache')
    }
}
```

This setup offers several advantages: it provides **isolation**, as the cache is contained within the project
directory (`build-cache`), avoiding conflicts with other builds; it improves **portability**, because if you move the
project, the cache moves with it; and it facilitates **cleanup**, as you can simply delete the `build-cache` folder to
clear the cache.

This combination ensures that your project is optimized for maximum speed from the beginning.

### Build Performance Comparison

Below is a comparison of a project's build time with and without the Gradle cache enabled. The command executed for this
comparison was `./gradlew clean jacocoMergedReport`.

#### Without Gradle Cache

Without the cache, Gradle still performs up-to-date checks but must execute most tasks, including downloading all
necessary project dependencies, leading to a longer execution time.. As shown in the image, out of 39 actionable tasks,
33 were executed and 6 were considered `UP-TO-DATE`, resulting in a total execution time of **1 minute and 54 seconds**.

![Project without Gradle Cache](/img/gradle-cache/project-without-gradle-cache.png)

Gradle uses a [dependency cache](https://docs.gradle.org/current/userguide/dependency_caching.html) by default. The
image below shows a subsequent build where dependencies are already cached. While tasks are still executed, the build is
faster because no downloads are needed. As the image indicates, the 39 actionable tasks completed in **35.5 seconds**.

![Project with Garadle Dependency Cache](/img/gradle-cache/project-with-gradle-dependency-cache.png)

#### With Gradle Cache

With the cache enabled, Gradle can reuse the outputs of previous builds, significantly reducing build times. As shown in
the image, out of 39 actionable tasks, 23 were reused from the cache (`FROM-CACHE`), and only 16 were executed. This
resulted in a total execution time of just **4.8 seconds**, showcasing the significant performance benefits of caching.

![Project with Gradle Cache](/img/gradle-cache/project-with-gradle-cache.png)

It's important to note that these benefits persist in subsequent builds. If you run the same command again without any
code changes, Gradle's incremental build feature will identify that the tasks are `UP-TO-DATE` and skip them, leading to
near-instantaneous results. The Configuration Cache further accelerates this process by reusing the stored task graph,
completely avoiding the configuration phase on subsequent runs.

#### Performance Summary

The graph below visually summarizes the build performance across three key scenarios:

- **First Compile:** The initial build takes **114 seconds**, as it includes downloading all dependencies.
- **Without Cache:** A subsequent build with dependencies already cached but without the build cache takes **35.4
  seconds**.
- **With Cache:** With all caches enabled, the build time is reduced to just **4.5 seconds**.

This represents a build time reduction of approximately **87%** compared to the build without the cache, clearly
demonstrating the impact of Gradle's caching mechanisms.

![Graphic Gradle Cache](/img/gradle-cache/graphic-gradle-cache.png)




