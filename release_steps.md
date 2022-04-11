# How to release Butterfly

## Introduction

This document explains how to release a new Butterfly version.

## Pre-requirements

1. All instructions documented here are MacOS and bash specific. Adjust them accordingly if you use a different OS and/or shell.
1. You must have:
   1. Admin rights to Butterfly GitHub repo.
   1. A [Nexus Repository Manager](https://oss.sonatype.org/#welcome) account with access to PayPal artifacts.

## Release steps

1. Working from master branch
   1. Set the new version in `build.gradle`
   1. Run `./gradlew clean build` and make sure it succeeds
   1. Run `./prepare_release.sh`
   1. If `tests/sample-apps` has changed, perform the following
      1. zip `tests/sample-apps/echo` folder
      1. replace zip under `docs/zip`
      1. update link in `docs/QUICK_START.md` if zip file name changed
   1. Update `docs/RELEASE_NOTES.md` file
   1. Commit `Releasing <new version>`
   1. Push your changes (`git push upstream master`)
1. Go to butterfly repo in GitHub
   1. Create a new release and tag from master branch
      1. New release title and tag name should be the new version
      1. Add sections `New Features and enhancements` and `Bug fixes` from release notes to Release description
1. Deploy artifacts to Maven Central
   1. Go to [butterfly project in TravisCI](https://travis-ci.com/github/paypal/butterfly)
   1. Click on `More Options -> Trigger Build`
   1. Set `master` as branch
   1. Set `Publishing <new version>` in `CUSTOM COMMIT MESSAGE` field
   1. Copy and paste the content of [.travis_release.yml](.travis_release.yml) in `CUSTOM CONFIG` field
1. Manual sonatype release
   1. Go to [Nexus Repository Manager](https://oss.sonatype.org/#welcome)
   1. Go to `Staging Repositories`
   1. Close the butterfly staging repository
   1. Release the butterfly staging repository
   1. Wait a couple of hours and make sure new butterfly version shows at http://search.maven.org/#search|ga|1|g:com.paypal.butterfly
1. Working from master branch
   1. Set the new SNAPSHOT version in `build.gradle`
   1. Run `./gradlew clean build` and make sure it succeeds
   1. Add the next new version (without SNAPSHOT) empty section in release notes
   1. Commit `Preparing for version <next new version>`
   1. Push your changes (`git push upstream master`)