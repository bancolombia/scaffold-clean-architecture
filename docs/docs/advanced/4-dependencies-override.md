---
sidebar_position: 4
---

# Dependency Version Overrides

Use `dependenciesOverride` in the root `build.gradle` to force versions when addressing
vulnerabilities. A three-part coordinate targets one module; a two-part coordinate applies to every
module in the group. Overrides apply to configurations in all projects.

```groovy title="build.gradle"
dependenciesOverride {
   ensure 'com.fasterxml.jackson.core:jackson-core:2.23.0'
   ensure 'tools.jackson.core:3.3.0'
}
```

## Jackson 3 and `jackson-annotations`

`jackson-databind` 3.x needs `jackson-annotations` 2.x, for example `3.2.x` needs `2.22` and `3.3.x`
needs `2.23`. Spring Boot manages `jackson-annotations` through its own BOM, so overriding only Jackson 3 can leave an
older `jackson-annotations` on the classpath, and the tests fail with errors such as
`NoClassDefFoundError: com/fasterxml/jackson/annotation/JsonApplyView`.

To avoid this, when `dependenciesOverride` contains an override for `tools.jackson.core` (the whole group,
`jackson-databind` or `jackson-core`), `com.fasterxml.jackson.core:jackson-annotations` is aligned automatically with
that Jackson 3 version. For the example above, `tools.jackson.core:3.3.0` also resolves `jackson-annotations` to `2.23`.

If you need a different version, declare it explicitly. An explicit override always takes precedence.

:::note
The generated root `build.gradle` also sets `ext['jackson-bom.version']` and `ext['jackson-2-bom.version']`. If you
change the Jackson version there, update both properties together.
:::
