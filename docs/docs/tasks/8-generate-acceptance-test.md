---
sidebar_position: 8
---

# Generate Acceptance Test

The **`generateAcceptanceTest | gat`** task will generate subproject
by [karate framework](https://docs.karatelabs.io) inside the folder "./deployment/", this task has one optional
parameter, `name`.

- **`name`** `= NameAcceptanceTestProject`: This parameter is going to specify the name of the acceptance test
  project. `Default Value = acceptanceTest`

```shell
      gradle generateAcceptanceTest --name=[acceptanceTestProjectName]
      gradle gat --name=[acceptanceTestProjectName] 
  ```

> Karate is an open-source tool to combine API test-automation, mocks, performance-testing and even UI automation into a
> single, unified framework. The BDD syntax popularized by Cucumber is language-neutral, and easy for even
> non-programmers. Assertions and HTML reports are built-in, and you can run tests in parallel for speed.

The generated project uses [Karate 2](https://docs.karatelabs.io/getting-started/whats-new-v2), which requires
**Java 21 or later**. It includes:

- `TestParallel`: runs the features with the Karate `Runner` and builds a Cucumber HTML report in
  `build/cucumber-html-reports` (the Karate report is in `build/karate-reports`).
- `ValidatorTestUtils`: validates responses against an OpenAPI specification with the
  [OpenAPI request validator](https://bitbucket.org/atlassian/swagger-request-validator).

Run the tests with:

```shell
      gradlew clean test
      gradlew clean test "-Dkarate.options=--tags @acceptanceTest"
  ```

:::tip[Migrating from Karate 1]
Projects generated with previous versions use Karate 1 (`com.intuit.karate`). See the
[Karate migration guide](https://docs.karatelabs.io/getting-started/migration-from-v1) to update them.
:::
