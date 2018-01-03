
# Release notes

## 2.3.0

#### Release date
TBD

#### New features and enhancements
TBD

#### Bug fixes
TBD

## 2.2.0

#### Release date
January 3rd, 2018.

#### New features and enhancements
* [73 - Include support to search folders as well in FindFiles](https://github.com/paypal/butterfly/issues/73)
* [68 - Improve TU and TO clone operations](https://github.com/paypal/butterfly/issues/68)
* [64 - Improve logging](https://github.com/paypal/butterfly/issues/64)
* [53 - Implement continuous delivery through automated deployment via Travis CI](https://github.com/paypal/butterfly/issues/53)
* [37 - Leverage File.createTempFile to create temporary files](https://github.com/paypal/butterfly/issues/37)
* [24 - Address Codacy static code analysis issues](https://github.com/paypal/butterfly/issues/24)
* [15 - Add unit tests to utilities project to make sure at least 70% code coverage](https://github.com/paypal/butterfly/issues/15)

#### Bug fixes
* [67 - ReadableByteChannel in ApplyFile and ApplyZip TOs needs to be closed](https://github.com/paypal/butterfly/issues/67)
* [70 - TransformationUtility condition object doesn't refer to the correct execution file](https://github.com/paypal/butterfly/issues/70)

## 2.1.0

#### Release date
December 2nd, 2017.

#### New features and enhancements
* [49 - Add condition that checks if a regex matches any line in a file](https://github.com/paypal/butterfly/issues/49)
* [27 - Print extension name and version during Butterfly execution](https://github.com/paypal/butterfly/issues/27)

#### Bug fixes
* [2 - RelatedArtifacts TU fails to report accurately an error when a pom file is not well formed](https://github.com/paypal/butterfly/issues/2)

## 2.0.0

#### Release date
October 17th, 2017.

#### New features and enhancements
* Automated application source code transformations
  * Application upgrades
  * Application migrations
  * Source code and configuration changes
* Plugable mechanism for Butterfly extensions, containing transformation and/or upgrade templates
* Multiple types of ready-to-use transformation utilities, including for example manipulating text, properties, XML, POM and Java files
* Resilient transformation pipeline
  * Shared context among transformation utilities
  * Error handling
  * Dependency management among transformation utilities
  * Conditional transformation utilities
* Transformation utilities execution in different modes and fashions
  * Configurable utilities
  * Anonymous utilities
  * Multiple execution
  * Group execution
  * In-loop execution
* Post-transformation features
  * Manual steps Reporting
  * Metrics
* APIs
  * Extensions API
  * API for custom transformation utilities and operations
  * Facade for Java application integration
* Command-Line-Interface tool
* Logging in silent or verbose mode, in info or debug level, and on console or file

#### Bug fixes
None