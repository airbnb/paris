Releasing
========

1. Bump the VERSION_NAME property in `gradle.properties` based on Major.Minor.Patch naming scheme
2. Update `CHANGELOG.md` for the impending release.
3. Update the `README.md` with the new version.
4. `git commit -am "Prepare for release X.Y.Z"` (where X.Y.Z is the version you set in step 1)
5. `git push`
6. Create a new release on Github
    1. Tag version `vX.Y.Z`
    2. Release title `vX.Y.Z`
    3. Paste the content from `CHANGELOG.md` as the description
7. `./gradlew clean uploadArchives --no-daemon --no-parallel`
8. Visit [Sonatype Nexus](https://oss.sonatype.org/) and promote the artifact.
