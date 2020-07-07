# Thorntail OpenShift Test Suite

For running the tests, it is expected that the user is logged into an OpenShift project.
All tests will run in that project, so it should be empty.
For local usage, Minishift is recommended.

## Branching Strategy

The `master` branch is always meant for latest upstream/downstream development.
For each downstream `major.minor` version, there's a corresponding maintenance
branch:

- `2.4.x` for RHOAR Thorntail 2.4.x (corresponding upstream version: `2.4.0.Final+`)
- `2.5.x` for RHOAR Thorntail 2.5.x (corresponding upstream version: `2.5.0.Final+`)
- `2.7.x` for RHOAR Thorntail 2.7.x (corresponding upstream version: `2.7.0.Final+`)
