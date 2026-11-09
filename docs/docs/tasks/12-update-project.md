---
sidebar_position: 12
---

# Update Project

The **`updateCleanArchitecture | u`** task will update plugin and dependencies in all sub projects, this task has one
optional parameter `dependencies`
if you only want to update some dependencies the dependency need to contain the group, and the artifact for example for
the dependency **cleanArchitecture** you will need to append **co.com.bancolombia:cleanArchitecture**.
It also updates the spring, lombok, gradle and some other dependencies according to the plugin version that you are
using, ocassionally it could make changes in other project structural files.
When no `dependencies` are given, it also updates the Gradle version of the `wrapper` task, and the
`distributionSha256Sum` when your `build.gradle` already declares one (version and checksum are updated together, so
the Gradle download keeps passing validation). Run `./gradlew wrapper` afterwards to regenerate the wrapper files.
We recommend that you commit your changes to git before running this command, but you can skip the verification
passing `--git false`.

  ```shell
     gradle updateCleanArchitecture --dependencies=[dependency1, dependency2, ...]
     gradle u --dependencies=[dependency1, dependency2, ...]
   ```
  