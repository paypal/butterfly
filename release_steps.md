# Release steps

1. Working from a temp branch (out of develop) in your fork:
   1. Rev up all 9 pom files to the release version
   1. Build and test it
   1. javadoc
      1. generate javadocs for utilities and extensions-api projects: `mvn javadoc:javadoc`
      1. place javadocs files under `docs/javadocs/<version>`
      1. update links in `Extension-development-guide.md`, pointing to latest javadoc
      1. commit only javadocs `Adding javadocs for version x`
   1. doc
      1. update zip link (two places in the URL) in `Installing-Butterfly.md`
   1. sample extension
      1. update `butterfly.version` in `tests/sample-extension/pom.xml`
      1. build and test it
      1. place updated jar under `docs/jar`
      1. update link in `QUICK_START.md` if jar file name changed
   1. sample app (only if changed)
      1. build and test it
      1. place updated jar under `docs/zip`
      1. update link in `QUICK_START.md` if zip file name changed
   1. Update release notes
   1. Send PR `Releasing x` from your temp branch to upstream develop branch
1. Send and merge PR from develop to master
1. Tag new release from master
1. Close milestone
1. Run manual CI job using [.travis_release.yml](.travis_release.yml) in TravisCI against master branch
1. Manual sonatype release
   1. Staging Repositories
   1. Close (the one with sources and everything)
   1. Release
1. Update [Homebrew formula](https://github.com/paypal/homebrew-butterfly/blob/master/Formula/butterfly.rb)
   1. Update zip link
   1. Update sha256
   1. Verify brew can update or install new version
1. Working from a temp branch (out of develop) in your fork:
   1. Rev up all 9 pom files to the next SNAPSHOT version
   1. Add new version empty section in release notes
   1. Send PR `Preparing for x` from your temp branch to upstream develop branch
1. Create new milestone
1. Add issues to new milestone (if any)