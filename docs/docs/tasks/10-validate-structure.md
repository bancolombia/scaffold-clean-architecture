---
sidebar_position: 10
---

# Validate Structure

The **`validateStructure | vs`** Validate that project references aren't violated.

```shell
   gradle validateStructure
   gradle vs
   ```

This validation has another best practices verifications, which you can see on the generated
`ArchitectureTest` file within the unit tests of the `app-service` module.

### Dependency Rules

One important point made by Robert C. Martin on Clean Architecture is the **Dependency Rule**, that can be summarized
like
this: source code dependencies can only point inwards. Nothing in an inner circle can know anything at all about
something in an outer circle. In particular, the name of something declared in an outer circle must not be mentioned
by the code in an inner circle.

Having that in mind, the **`validateStructure | vs`** task performs the following validations:

1) Model module: to have no dependencies at all.
2) UseCase module: to declare dependency to the Model module ONLY, and no other additional dependencies.
3) Infrastructure Layer modules:
    - Allow declaration of any external dependency.
    - Allow declaration of dependency on Model and/or UseCase modules.
    - Avoid declaration of dependency AppService module.

**Platform dependencies**:

Dependencies declared with Gradle's `platform(...)` or `enforcedPlatform(...)` are automatically
excluded from dependency validation. These dependencies define BOM (Bill of Materials) versions
rather than technology dependencies, including when added to all modules through `subprojects`:

```groovy
subprojects {
    dependencies {
        implementation platform("com.myorg:my-bom:0.0.1")
    }
}
```

Platforms are identified by their Gradle category, not their name. Ordinary dependencies are
still validated, even if they have the same coordinates as a platform. The UseCase module must
still depend on the Model module.

**Whitelisting dependencies**:

For other dependencies that you explicitly want to allow in the domain modules, configure
`whitelistedDependencies`. Platforms do not need to be whitelisted.

```groovy
// build.gradle
cleanPlugin {
    modelProps {
        whitelistedDependencies = "allowed-library, another-library"
    }
}
```

Indicate only the dependency names, comma-separated.
