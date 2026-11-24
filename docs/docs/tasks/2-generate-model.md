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

`--from-swagger` receives the OpenAPI contract file (`.yaml` or `.json`) in one of these forms:

- A path relative to the project root, for example `--from-swagger=swagger.yaml` or `--from-swagger=contracts/swagger.yaml`.
- An absolute path, for example `--from-swagger=C:\contracts\swagger.yaml` or `--from-swagger=/home/user/contracts/swagger.yaml`.
- A `file:` URI, for example `--from-swagger=file:///home/user/contracts/swagger.yaml`.
- An `http(s)` URL that returns the contract file itself, for example `--from-swagger=https://example.com/api/swagger.yaml`.
  A web page such as the Swagger UI does not work; use the URL of the YAML or JSON document it loads.

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

Optional folder layout (only when you want it):

```shell
gradle generateModel --from-swagger=contract.yaml \
  --request-models=[SchemaName],[SchemaName2] \
  --response-models=[SchemaName3]
```

Schemas not listed in either option are generated under `entities` (no `request` or `response` subpackage). Schema keys are
case-sensitive and must match `components.schemas` in the contract.

```shell
gradle generateModel --from-swagger=[filePath.yaml] --models=[SchemaName]   
gradle gm --from-swagger=[filePath.yaml] --models=[SchemaName]   
```

### Class names

Domain models must not use technical suffixes, so the `Dto`, `Request` and `Response` suffixes are removed from the
class names (`bookResponse` generates `Book`, `customerRequestDto` generates `Customer`). Every reference to the schema
in other models uses the new name. The suffixes are the ones configured in `arch.unit.forbiddenDomainSuffixes`.

The task stops before writing any file when a class name is not valid:

- Two schemas end up with the same name, for example `book` and `bookResponse`. Names are compared ignoring case
  (`Book` and `BOOK` would be the same file on macOS and Windows) and across packages, so a `bookRequest` and
  `bookResponse` pair needs `rename-models` even when you split them with `request-models` and `response-models`.
- The name is a Java type the generated models use, such as `List`, `Map`, `Date`, `File` or `Timestamp` (for example
  `dateResponse` would become `Date`), or a Lombok annotation such as `Builder` when Lombok is enabled.
- The name contains a forbidden technical word (`arch.unit.forbiddenDomainClassNames`).
- The schema key is a type the generator maps to Java, such as `Date`, `File`, `UUID`, `List` or `Map`. The generator
  would use the Java type instead of generating the schema, so rename the schema in the contract (`rename-models` does
  not help in this case).

Use `rename-models` to choose the class name of any schema (`schemaKey:ClassName` pairs, comma-separated):

```shell
gradle generateModel --from-swagger=contract.yaml --rename-models=bookResponse:BookSummary,customerRequest:CustomerCriteria
```

Inline object schemas are generated as their own models. Their key is their `title` when they have one; otherwise it is
built from the parent schema and the property, for example `auditLog_request` for the `request` property of
`auditLog`. If one of them collides, the error shows that key, and you can use it in `rename-models` too.

Top-level `array` and `map` schemas (`type: object` with `additionalProperties`) are generated as classes that extend
`ArrayList` or `HashMap`, without a Lombok builder.

The task also stops when a `$ref` cannot be resolved (missing file, wrong pointer or missing schema), instead of
generating models that reference classes that do not exist.
