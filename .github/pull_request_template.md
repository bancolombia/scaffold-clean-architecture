Before submitting a pull request, please read
https://bancolombia.github.io/scaffold-clean-architecture/docs/contributing

## Description
<!--- Describe your changes in detail -->

## Category
- [ ] Feature
- [ ] Fix
- [ ] Ci / Docs

## Checklist
- [ ] The pull request is complete according to the [guide of contributing](https://bancolombia.github.io/scaffold-clean-architecture/docs/contributing).
- [ ] The pull request has a descriptive title that describes what has changed, and provides enough context for the changelog.
- [ ] Automated tests are written.
- [ ] The documentation is up-to-date.
- [ ] If the pull request has a new driven-adapter or entry-point, you should add it to docs and `sh_generate_project.sh` files for generated code tests.
- [ ] If the pull request adds new template files, ensure they follow the naming convention: lowercase with hyphens (e.g., `handler-registry-configuration.java.mustache`), and test files with `.test` suffix (e.g., `handler-registry-configuration.test.java.mustache`). See the [guide](https://bancolombia.github.io/scaffold-clean-architecture/docs/contributing#setupfromtemplate) for details.
- [ ] If the pull request has a new Gradle Task, you should add `Analytics` according to the [guide](https://bancolombia.github.io/scaffold-clean-architecture/docs/contributing).
- [ ] If the pull request adds task parameters, ensure they follow the naming convention: multi-word parameters with hyphens (e.g., `--server-name`), single-word without hyphens (e.g., `--type`). See the [guide](https://bancolombia.github.io/scaffold-clean-architecture/docs/contributing#task-parameter-naming-convention) for details.
- [ ] If the pull request adds a new dependency constant in `Constants.java`, you should include the corresponding configuration in the `sh_dependencies.json` file.
- [ ] If the pull request has changed structural files, you have implemented an UpgradeAction according to the [guide](https://bancolombia.github.io/scaffold-clean-architecture/docs/contributing#pull-request-criteria).
