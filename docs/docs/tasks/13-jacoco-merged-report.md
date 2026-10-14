---
sidebar_position: 13
---

# JaCoCo Merged Report

The root **`jacocoMergedReport`** task combines JaCoCo execution data from the discovered Gradle
modules into one root report. Run it with:

```shell
./gradlew jacocoMergedReport
```

The task runs the `test` and `jacocoTestReport` tasks available in subprojects, then writes the
merged XML and HTML reports under `build/reports/jacocoMergedReport/`.

When Pitest is declared in the root `plugins {}` block, the merged report also depends on
`pitestReportAggregate`. That task runs each available subproject `pitest` task and creates the
consolidated PIT HTML and XML reports under `build/reports/pitest/`. The individual
`jacocoTestReport` tasks also depend on the subproject `pitest` tasks when Pitest is applied.

To run the complete build and the merged coverage report together:

```shell
./gradlew build jacocoMergedReport
```

Gradle deduplicates tasks shared by both requested tasks. The build runs compilation, validation,
tests, and other configured verification tasks. With Pitest declared, the PIT mutation analysis
and aggregate report run as part of the report dependencies; without Pitest, the build and merged
JaCoCo report still run without mutation analysis.
