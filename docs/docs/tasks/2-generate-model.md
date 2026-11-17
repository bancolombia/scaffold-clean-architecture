---
sidebar_position: 2
---

# Generate Model

The **`generateModel | gm`** task will generate a class and interface in model layer, this task has one of two required
parameter `name` or `from-swagger`.

```shell
   gradle generateModel --name=[modelName]
   gradle gm --name [modelName]
   gradle generateModel --from-swagger=[contractPath]
   gradle gm --from-swagger [contractPath]
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
**_This task will generate something like this if running parameter `from-swagger`:_**

   ```bash
   📦domain
   ┣ 📂model
   ┃ ┣ 📂src
   ┃ ┃ ┣ 📂main
   ┃ ┃ ┃ ┗ 📂java
   ┃ ┃ ┃ ┃ ┗ 📂[package]
   ┃ ┃ ┃ ┃ ┃ ┗ 📂model
   ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂entities
   ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂request
   ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜RequestModel.java
   ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂response
   ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ResponseModel.java
   ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜Model.java
   ┃ ┗ 📜build.gradle
   ```

Additionally, if you use `models`, you can specify the Schema names you want to generate (default: all schemas), 
`models` takes the exact schema names (comma-separated, case-sensitive).

```shell
gradle generateModel --from-swagger=[filePath.yaml] --models=[SchemaName]   
gradle gm --from-swagger=[filePath.yaml] --models=[SchemaName]   
```