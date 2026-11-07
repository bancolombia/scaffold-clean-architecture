---
sidebar_position: 2
---

# Generate Model

The **`generateModel | gm`** task will generate a class and interface in model layer, this task has one of two required
parameter `name` or `from-openapi`.

```shell
   gradle generateModel --name=[modelName]
   gradle gm --name [modelName]
   gradle generateModel --from-openapi=[contractPath]
   gradle gm --from-openapi [contractPath]
  ```

**_This task will generate something like this if running parameter `name`:_**

   ```bash
   📦domain
   ┣ 📂model
   ┃ ┣ 📂src
   ┃ ┃ ┣ 📂main
   ┃ ┃ ┃ ┗ 📂java
   ┃ ┃ ┃ ┃ ┗ 📂[package]
   ┃ ┃ ┃ ┃ ┃ ┗ 📂model
   ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂gateways
   ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ModelRepository.java
   ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜Model.java
   ┃ ┃ ┗ 📂test
   ┃ ┃ ┃ ┗ 📂java
   ┃ ┃ ┃ ┃ ┗ 📂[package]
   ┃ ┃ ┃ ┃ ┃ ┗ 📂model
   ┃ ┗ 📜build.gradle
   ```
**_This task will generate something like this if running parameter `from-openapi`:_**

   ```bash
   📦domain
   ┣ 📂model
   ┃ ┣ 📂src
   ┃ ┃ ┣ 📂main
   ┃ ┃ ┃ ┗ 📂java
   ┃ ┃ ┃ ┃ ┗ 📂[package]
   ┃ ┃ ┃ ┃ ┃ ┗ 📂model
   ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂entities
   ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂common
   ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜CommonModel.java
   ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂request
   ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜RequestModel.java
   ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂response
   ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ResponseModel.java
   ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜Model.java
   ┃ ┗ 📜build.gradle
   ```

Additionally, if you use `models`, you can specify the Schema names you want to generate (default: all schemas),

```shell
gradle generateModel from-openapi=[filePath.yaml] --models=[SchemaName]   
gradle gm from-openapi=[filePath.yaml] --models=[SchemaName]   
```