# Contributing

Thank you for considering contributing to Paris!

## Testing

Paris has an extensive suite of tests. Most of them are located in the `paris` module (for Paris classes) or in `paris-test` (for code generation). Due to the nature of this project a good share of the tests are integration tests and have to be run on an emulator or device.

This command runs all the tests across modules:
```
./gradlew test connectedAndroidTest
```

Please make sure that all tests are passing before proposing changes, and add or update tests whenever possible.

If you update the annotation processor tests you may find the `update_processor_test_resources.rb` script very useful for updating the existing tests with your changes.

## How to Submit Changes

First make sure all tests are passing, then create a Pull Request on Github. Explain why you are proposing the change, what issues it addresses, why you chose particular solutions, etc. The more context and details the easier it will be to review.

When possible/appropriate changes should include tests and be well documented.

## How to Report a Bug or Request an Enhancement

Create an issue on Github and tag it with `bug` or `enhancement` as appropriate. Please provide as much detail as possible. In the case of bugs that should include an easy way to reproduce the issue.
