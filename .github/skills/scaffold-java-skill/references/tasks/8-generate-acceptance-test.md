---
sidebar_position: 8
---

# Generate Acceptance Test

The **`generateAcceptanceTest | gat`** task will generate subproject
by [karate framework](https://github.com/intuit/karate)  inside the folder "./deployment/", this task has one optional
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
