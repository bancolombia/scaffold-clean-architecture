---
sidebar_position: 7
---

# Gradle Wrapper

The plugin configures the Gradle `wrapper` task for you, so generated projects do not need to declare it.

## Default configuration

Every `wrapper` task gets these values by default:

| Property | Value |
| --- | --- |
| `gradleVersion` | The Gradle version recommended by the plugin version you use. |
| `distributionSha256Sum` | The checksum of that distribution, so the Gradle download is verified. |
| `validateDistributionUrl` | `true` (Gradle default). |

The checksum is applied only for the default Gradle version and the `bin` distribution. If you pass another version
(`--gradle-version`) or `--distribution-type all`, or you set `distributionSha256Sum` yourself, the plugin does not
change it.

## Update the wrapper

After updating the plugin, regenerate the wrapper files with:

```shell
./gradlew wrapper
```

This updates `gradle/wrapper/gradle-wrapper.properties` with the Gradle version and checksum of the plugin. Gradle only
updates the properties file in the first run, so run the task a second time if you also want to update
`gradle-wrapper.jar`, `gradlew` and `gradlew.bat`. You can check the result with `./gradlew --version`.

## Override the configuration

Declare the `wrapper` task in the root `build.gradle` only if you want to use different values:

```groovy title="build.gradle"
tasks.named('wrapper') {
    gradleVersion = '9.9.1'
    distributionSha256Sum = '<sha256 of the distribution>'
    validateDistributionUrl = true
}
```

You can get the checksum of a distribution at the [Gradle distribution and wrapper JAR checksum reference](https://gradle.org/release-checksums).

## Update with the dependency updater

When you declare `tasks.named('wrapper')`, the [update project](../tasks/12-update-project.md) task
(`gradle u`) and the [dependency updater](./2-use-as-updater.md) also update its `gradleVersion` and, if declared, its
`distributionSha256Sum`. They are updated together, so the Gradle download keeps passing validation. If you do not
declare the task, there is nothing to update: run `./gradlew wrapper` after updating the plugin.
