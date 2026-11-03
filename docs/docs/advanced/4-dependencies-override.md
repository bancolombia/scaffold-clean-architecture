---
sidebar_position: 4
---

# Dependency Version Overrides

Use `dependenciesOverride` in the root `build.gradle` to force versions when addressing
vulnerabilities. A three-part coordinate targets one module; a two-part coordinate applies to every
module in the group. Overrides apply to configurations in all projects.

```groovy
dependenciesOverride {
   ensure 'com.fasterxml.jackson.core:jackson-core:2.22.3'
   ensure 'tools.jackson.core:3.2.3'
}
```
