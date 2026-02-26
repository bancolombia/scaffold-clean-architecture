---
sidebar_position: 4
---

# Generate Use Case

The **`generateUseCase | guc`** task will generate a class in model layer, this task has one required parameter `name`.

```shell
   gradle generateUseCase --name=[useCaseName]
   gradle guc --name [useCaseName]
 ```

**_This task will generate something like that:_**

   ```bash
   📦domain
   ┗ 📂usecase
   ┃ ┣ 📂src
   ┃ ┃ ┣ 📂main
   ┃ ┃ ┃ ┗ 📂java
   ┃ ┃ ┃ ┃ ┗ 📂[package]
   ┃ ┃ ┃ ┃ ┃ ┗ 📂usecase
   ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂business
   ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜BusinessUseCase.java
   ┃ ┃ ┗ 📂test
   ┃ ┃ ┃ ┗ 📂java
   ┃ ┃ ┃ ┃ ┗ 📂[package]
   ┃ ┃ ┃ ┃ ┃ ┗ 📂usecase
   ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂business
   ┃ ┗ 📜build.gradle
   ```
