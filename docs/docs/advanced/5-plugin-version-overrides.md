---
sidebar_position: 5
---

# Gradle Plugin Version Overrides

The generated root `build.gradle` declares the Spring Boot, Sonar, and (when mutation testing is
enabled) Pitest plugins in its `plugins {}` block. The Settings convention uses the version requested
there. If no version is specified, it resolves the plugin using the corresponding default in
`Constants`.

To use a different version, add or change the version directly in the root `plugins {}` block:

```groovy
plugins {
    id 'org.springframework.boot' version '4.2.0' apply false
    id 'org.sonarqube' version '7.6.0.1234'
    id 'info.solidsoft.pitest' version '1.20.0'
}
```

Keep `apply false` on Spring Boot in the root project: the scaffold applies it to `app-service`.
Sonar is applied to the root project. Pitest is applied to Java subprojects when its plugin is
present in the root plugin classpath.

The equivalent defaults are:

| Plugin ID | Default version source |
| --- | --- |
| `org.springframework.boot` | `Constants.SPRING_BOOT_VERSION` |
| `org.sonarqube` | `Constants.SONAR_VERSION` |
| `info.solidsoft.pitest` | `Constants.GRADLE_PITEST_VERSION` |

The selected version must be compatible with the project's Gradle and Java versions. The plugin
version used to generate the project (`co.com.bancolombia.cleanArchitecture`) is independent from
these third-party plugin versions.
