---
sidebar_position: 8
---

# Generate Pipeline

The **`generatePipeline | gpl`** task will generate CI pipeline inside the folder "./deployment/", this task has one
required parameter `type` and one optional parameter `monoRepo` by default is false.

```shell
   gradle generatePipeline --type=[pipelineType] --monoRepo=[true | false]
   gradle gpl --type=[pipelineType]  --monoRepo=[true | false]
   ```

| Reference for **pipelineType** | Name              |
|--------------------------------|-------------------|
| azure                          | Azure Pipeline    |
| circleci                       | CircleCI Pipeline |
| github                         | GitHub Action     |
| jenkins                        | Jenkins Pipeline  |
   