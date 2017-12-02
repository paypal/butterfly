
# Release notes

## 2.2.0

#### Release date
TBD

#### New features and enhancements
None

#### Bug fixes
None

## 2.1.0

#### Release date
December 2nd, 2017.

#### New features and enhancements
1. [49 - Add condition that checks if a regex matches any line in a file](https://github.com/paypal/butterfly/issues/49)
1. [27 - Print extension name and version during Butterfly execution](https://github.com/paypal/butterfly/issues/27)

#### Bug fixes
1. [2 - RelatedArtifacts TU fails to report accurately an error when a pom file is not well formed](https://github.com/paypal/butterfly/issues/2)

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