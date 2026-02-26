---
name: scaffold-java-skill
description: Creates java components using gradle plugin scaffold-clean-architecture. Use it when a user want to create new java projects that adhere to certain folder structure to comply with the clean architecture philosophy. Key capabilities are generate a new project, generate models, use-cases, driven adapters, entry-points, helper modules, generate pipeline templates, acceptance tests and more. 
license: MIT
compatibility: Java 21, Gradle 9, Spring Boot 4.2
metadata: 
  author: gabrimar
  version: 1.0.0
---

# Scaffold Java Applications Skill

When creating a new project, the user can choose between two options: create a new project from scratch or generate components for an existing project. In both cases, the skill will ask the user for the necessary information to generate the desired output.

## Pre-requisites

Before starting, we must ensure that the user has the following installed on their machine:

- Java 21 or higher
- Gradle 9 or higher

The skill will check this by running: `java --version` and `gradle --version`.

## Instructions

### After any step completed

- The skill will ONLY run the gradle command to build the full projectt with `gradle build -x test`, if any source files were changed or generated, and show the results to the user. Partial module builds should be avoided.
- The skill should avoid the creation of `docker-compose` files unless required by the user.
- The skill should avoid the creation of README files within the modules, unless required by the user.
- The skill should update de root project `README.md` file to include the relevant information about the generated modules and components, and how to run the project. If the `README.md` file does not exist, the skill should create it.

### Step 1: Choose an option
The user will be prompted to choose between creating a new project or generating components for an existing project

### Step 2: User wants to create a new project 

1. If the user wants to create a new project from scratch. The skill will check first if the current directory is empty. If the directory is not empty, the skill will show an error message and ask the user to choose another directory or to generate components for an existing project instead.

2. The user will be prompted to provide the `project_name`, `package_name` and `java_version`. 

3. Then the skill will run the following command to create a `build.gradle` file with the necessary configuration to use the `scaffold-clean-architecture` plugin:

```bash
cat > build.gradle <<EOF
plugins {
    id 'co.com.bancolombia.cleanArchitecture' version '4.1.0'
}
EOF
```

4. After the `build.gradle` file is created, the skill will call `gradle cleanArchitecture --package=<package_name> --type=reactive --name=<project_name> --java-version=<java_version>`. All other parameters for the `cleanArchitecture` task remain the default.

Instead if the user wants to generate components for an existing project this step 2 is completely skipped.

### Step 3: User wants to generate components for an existing project
The user will be prompted to choose the type of component they want to generate. The options include

#### 3.1. Generate a model:

The user will be prompted to provide the model `name`. The skill will then generate the model by calling `gradle generateModel --name=<model_name>`. After that, ask the user if they want to generate fields for the model. 

If the answer is yes, then ask the user to provide the field names and types, and then generate code for the model according to the description. 

If the answer is no, then skip this intermediate step.

#### 3.2. Generate a usecase: 

The user will be prompted to provide the usecase `name`. The name should not contain "UseCase" at the end. The name should be something meaningful that represents the usecase. 

The skill will then generate the usecase by calling `gradle generateUseCase --name=<usecase_name>`. 

After that, ask the user if they want to generate logic for the usecase. 

If the answer is yes, then ask the user to describe what the usecase should do and then generate code for the usecase according to the description. Look for the models already present in the project, and wire them as input and/or output for the methods generated. Also look for already generated repository or gateway interfaces, present in the domain, that the use case should use for interacting with the outer world. If no interfaces are available, create the necessary gateway interfaces. For the methods return types, prefer wrapping them with the reactive abstractions Mono or Flux. If there are no models available, create the necessary models. The created use case and methods should include the javadoc that indicates their purpose. Avoid annotating the use case with @component After the usecase is generated, the skill should also generate the corresponding test class for the usecase.

If the answer is no, then skip this intermediate step.

#### 3.3. Generate a entry point:

The user will be prompted to provide the entry point adapter `type` from the available options mentioned in the reference documentation. The user will also be prompted to provide the entry point `parameters`, depending in the selected type. With that the skill procedes to create the entry point by calling `gradle generateEntryPoint --type=[selected type] [list of parameters and their values]`. 

After that, ask the user if they want to generate logic for the entry point. 

If the answer is yes, then ask the user to describe what the entry point should do and then generate code for the entry point according to the description. Look for the usecases already present in the project, and wire them as the logic to be invoked from the entry point. Entry points should define necesary DTOs (Pojos) for usage in the layer that represent input/output data from/to the outer layers, but any communication with use cases should be based in the objects defined as models and/or primitive objects like Strings, ints, longs, boolean, etc. If there are no usecases available, create the necessary usecases. After creating the entry point and methods, the skill should also generate the corresponding test class for the entry point.

For entry-point of type REST or WebFlux the skill WILL NOT generate postman collection files, but instead will generate the corresponding OpenAPI specification file with the details of the generated endpoint, including request and response body examples based on the DTOs defined for the entry point.

If the answer is no, then skip this intermediate step.

#### 3.4. Generate a driven adapter:

The user will be prompted to provide the driven adapter `type` from the available options mentioned in the reference documentation. The user will also be prompted to provide the driven adapter `parameters`, depending in the selected type. With that the skill procedes to create the driven adapter by calling `gradle generateDrivenAdapter --type=[selected type] [list of parameters and their values]`. 

After that, ask the user if they want to generate code for the driven adapter. 

If the answer is yes, then ask the user to describe what the driven adapter should do and then generate code for the driven adapter according to the description. Driven adapters always must implement gateway interfaces defined in the domain. Driven Adapters methods must only accept/return object types defined in the Domain and or Java primitives. Internally, the driven adapter may define necesary DTOs (Pojos) for usage in the transport layer that represent input/output data from/to the system or component this driven adapter is communicating with. After creating the driven adapter and methods, the skill should also generate the corresponding test class for the driven adapter.

If the answer is no, then skip this intermediate step.

#### 3.5. Generate a helper module:

The user will be prompted to provide the helper module `name`. The skill procedes to create the helper module `gradle generateHelper --name=[selected name]`. After that, ask the user if they want to generate code for the helper module. If the answer is yes, then ask the user to describe what the helper module should do and then generate code for the helper module according to the description. If the answer is no, then skip this intermediate step.

#### 3.6. Generate a pipeline template:

The user will be prompted to provide the pipeline `type`, valid types are defined in the reference documentation, and whether it operates on a mono repository `mono-repo` (true or false). The skill procedes to create the pipeline template `gradle generatePipeline --type=[selected type] --mono-repo=[true|false]`.

#### 3.7. Generate Acceptance Tests:

The user will be prompted to provide the acceptance tests subproject `name`. The skill procedes to create the acceptance tests subproject `gradle generateAcceptanceTest --name=[selected name]`.

#### 3.8. Generate Performance Tests:

The user will be prompted to provide the performance tests `type`. The skill procedes to create the performance tests with `gradle generatePerformanceTest --type=[selected type]`.

### Step 4: Validate the structure of the project (optional)

The user can run the `validateStructure` task to validate that the project structure adheres to the clean architecture principles. The skill will call `gradle validateStructure` and show the results to the user.

### Step 5: Update the project (optional)
The user can run the `updateProject` task to update the project with the latest version of the plugin. This task has an optional parameter to specify the dependency names the plugin should update as a comma separated list. Eg: `gradle updateProject --dependencies=dependency1,dependency2`. The skill will call `gradle updateProject` and show the results to the user.

