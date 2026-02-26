---
sidebar_position: 10
---

# Generate Performance Test

The **`generatePerformanceTest | gpt`** task will generate Performance test inside the folder "./performance-test/",
this task has one required parameter `type`.

```shell
   gradle generatePerformanceTest --type=[performanceType]
   gradle gpt --type=[performanceType]
   ```

    | Reference for **performanceType** | Name                    |
    | --------------------------------- | ----------------------- |
    | jmeter                            | Jmeter Performance Test |
