# Release steps

1. Working from master branch:
   1. Set the new version in `build.gradle`
   1. Run `./gradlew clean build` and make sure it succeeds
   1. Run `./prepare_release.sh`
   1. If `tests/sample-apps` has changed, perform the following
      1. zip `tests/sample-apps/echo` folder
      1. replace zip under `docs/zip`
      1. update link in `QUICK_START.md` if zip file name changed
   1. Update release notes
   1. Commit `Releasing <version number>`
   1. Push your changes (`git push upstream master`)
1. Tag new release from master
   1. Release title should be the version
   1. Add sections `New Features and enhancements` and `Bug fixes` from release notes to Release description
1. Close milestone
   1. Set is due date to today
1. Deploy artifacts to Maven Central
   1. Go to TravisCI (click on its badge on butterfly repo README)
   1. Click on `More Options -> Trigger Build`
   1. Set `master` as branch
   1. Set `Publishing <version number>` in `CUSTOM COMMIT MESSAGE` field
   1. Copy and paste the content of [.travis_release.yml](.travis_release.yml) in `CUSTOM CONFIG` field
1. Manual sonatype release
   1. Staging Repositories
   1. Close (the one with sources and everything)
   1. Release
   1. Wait a couple of hours and make sure new butterfly version shows at http://search.maven.org/#search|ga|1|g:com.paypal.butterfly
1. Update [Homebrew formula](https://github.com/paypal/homebrew-butterfly/blob/master/Formula/butterfly.rb)
   1. Run the following command: `brew create https://search.maven.org/remotecontent?filepath=com/paypal/butterfly/butterfly-cli-package/${NEW_BUTTERFLY_VERSION}/butterfly-cli-package-${NEW_BUTTERFLY_VERSION}.zip`
      1. This new command will create a new brew Formula in your computer and automatically open it in a text editor
      1. If you get an error message stating that formula already exists, remove it, and then run the command again 
   1. Copy the `sha256` value from it and close the text editor
   1. Change file https://github.com/paypal/homebrew-butterfly/blob/master/Formula/butterfly.rb providing the new Butterfly `VERSION` and `sha256` value
   1. Test the new brew installation and make sure it works 
1. Working from master branch:
   1. Rev up root build.gradle file to the next SNAPSHOT version
   1. Build the whole project and make sure it builds fine
   1. Add new version empty section in release notes
   1. Commit `Preparing for version <version number>`
   1. Push your changes (`git push upstream master`)
1. Create new milestone
   1. Add issues to new milestone (if any)