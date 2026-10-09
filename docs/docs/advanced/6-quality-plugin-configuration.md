---
sidebar_position: 6
---

# Quality Plugin Configuration

Scaffold v5 supplies root quality defaults and shared `app-service` conventions. Configure project-
specific values in the generated root or module `build.gradle` files after the plugins block.

## Sonar

The root convention configures the `sonar` task with these defaults:

| Property | Default |
| --- | --- |
| `sonar.sourceEncoding` | `UTF-8` |
| `sonar.modules` | Discovered Gradle module directories, relative to the root project |
| `sonar.sources` | `src,deployment,settings.gradle,build.gradle`, plus each module's `build.gradle` |
| `sonar.exclusions` | `**/MainApplication.java,**/**.bin,**/deployment/**` |
| `sonar.tests` | `src/test` |
| `sonar.java.binaries` | `**/build/classes/java/main` |
| `sonar.junit.reportsPath` | `**/build/test-results/test` |
| `sonar.java.coveragePlugin` | `jacoco` |
| `sonar.coverage.jacoco.xmlReportPaths` | `build/reports/jacocoMergedReport/jacocoMergedReport.xml` |
| `sonar.pitest.reportPaths` | `build/reports/pitest/mutations.xml` |
| `sonar.externalIssuesReportPaths` | `build/issues.json` |

The root `sonar` task depends on `jacocoMergedReport`. When Pitest is declared, it also depends on
`pitestReportAggregate`.

You can customize or replace properties in the root `build.gradle`:

```groovy
sonar {
    properties {
        property "sonar.exclusions", "**/generated/**,**/config/**"
        property "sonar.sources", "src/main/java"
        property "sonar.projectKey", "my-service"
    }
}
```

## Pitest

Pitest is configured by the child convention in each subproject where the Pitest plugin is applied.
The defaults are:

| Setting | Default |
| --- | --- |
| `targetClasses` | The configured project package followed by `.*` |
| `excludedClasses`, `excludedTestClasses` | Empty |
| `pitestVersion` | `Constants.PITEST_VERSION` |
| `verbose` | `false` |
| `outputFormats` | `XML`, `HTML` |
| `threads` | Available processors |
| `exportLineCoverage` | `true` |
| `useClasspathFile` | `true` |
| `withHistory` | `true` |
| `timestampedReports` | `false` |
| `junit5PluginVersion` | `Constants.PITEST_JUNIT5_VERSION` |
| `failWhenNoMutations` | `false` |
| `jvmArgs` | `-XX:+AllowRedefinitionToAddDeleteMethods` |
| `fileExtensionsToFilter` | `xml`, `orbit` |

Because Pitest is applied to child projects, configure its extension inside `subprojects` in the root
`build.gradle`. This block runs after the scaffold defaults, so values set here override them:

```groovy
subprojects {
    pitest {
        targetClasses = ['com.example.service.*']
        outputFormats = ['XML']
        threads = 4
    }
}
```

Pitest settings and aggregation are active only when `info.solidsoft.pitest` is declared in the
root `plugins {}` block. The `ca --mutation=false` generator option omits that declaration from a
new project.

## App Service Defaults

The root convention applies Spring Boot to `app-service` unless the user already applied it, then
sets up the standard packaging tasks. It also adds these dependencies when they are not already
present:

| Configuration | Dependency |
| --- | --- |
| `implementation` | `org.springframework.boot:spring-boot-starter` |
| `runtimeOnly` | `org.springframework.boot:spring-boot-devtools` |
| `testImplementation` | `tools.jackson.core:jackson-databind` |
| `testImplementation` | `com.tngtech.archunit:archunit:<Constants.ARCH_UNIT_VERSION>` |

If the developer declares ArchUnit in any `app-service` configuration, the convention leaves that
dependency and its version unchanged.

Packaging defaults:

- The standard `jar` task is disabled.
- `explodedJar` copies the plain jar contents into `build/exploded`.
- `bootJar` is named `<root-project-name>.jar`.
