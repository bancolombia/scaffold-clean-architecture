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

**Whitelisting dependencies**:

Some dependencies, specially when working with BOMs (Bills of Materials), are injected transversally to the project,
making the validation task to flag failures to rules 1 and 2. To avoid this scenario, or other you may encounter,
you can configure a set of whitelisted dependencies.

*Example:*

1) Let's say you have declared a BOM in your project:

   ```groovy
   dependencies {
     implementation(platform("com.myorg:my-bom:0.0.1"))
   }
   ```

2) This will make **`validateStructure | vs`** to flag a failure indicating an error like this:

   ```bash
   Validating Model Module
    --- Dependency com.myorg:some-bom is not allowed in Model Layer
   ```

3) To avoid this, you can white list your BOM like this:

   ```groovy
   // build.gradle
   cleanPlugin {
     modelProps {
        whitelistedDependencies = "my-bom, <dep2>, <depN..>"
     }
   }
   ```

   Indicating only the name of the dependencies comma-separated.
