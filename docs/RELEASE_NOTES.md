
# Release notes

## 3.0.0

#### Release date
March 20th, 2020

#### New features and enhancements
* [342 - Changing POM Plugin feature is not available.](https://github.com/paypal/butterfly/issues/342)
* [335 - Develop Butterfly Slack integration](https://github.com/paypal/butterfly/issues/335)
* [318 - Make Butterfly multi-threaded, asynchronous and non-blocking](https://github.com/paypal/butterfly/issues/318)
* [319 - Make ButterflyFacade API asynchronous and non-blocking](https://github.com/paypal/butterfly/issues/319)
* [316 - Remove UpgradePath class from APIs and simplify ButterflyFacade](https://github.com/paypal/butterfly/issues/316)
* [167 - Allow executing Butterfly passing a set of extension specific properties as input parameter](https://github.com/paypal/butterfly/issues/167)
* [304 - Add support for multiple extensions](https://github.com/paypal/butterfly/issues/304)
* [196 - Introduce a format based method to refer to files in TUs and TOs](https://github.com/paypal/butterfly/issues/196)
* [288 - butterfly-test should interface with façade, instead of CLI](https://github.com/paypal/butterfly/issues/288)
* [220 - Set Application name and type in TransformationResult](https://github.com/paypal/butterfly/issues/220)
* [299 - Improving butterfly-test folders comparison output](https://github.com/paypal/butterfly/issues/299)
* [296 - Add diff for single different file in butterfly-test result](https://github.com/paypal/butterfly/issues/296)
* [261 - Make Butterfly code thread-safe](https://github.com/paypal/butterfly/issues/261)
* [131 - Add operation to write pom file from given Model object](https://github.com/paypal/butterfly/issues/131)
* [130 - Modify PomModel TU to also support returning a Model object out of a local pom.xml file (present in the application)](https://github.com/paypal/butterfly/issues/130)
* [19  - Add TU to build Maven model out of a pom.xml file not present in the application folder](https://github.com/paypal/butterfly/issues/19)
* [275 - Create TU to retrieve parent of given pom model or file](https://github.com/paypal/butterfly/issues/275)
* [272 - Create TU to retrieve packaging of given pom model or file](https://github.com/paypal/butterfly/issues/272)
* [270 - Add operations to add and remove modules from POM files](https://github.com/paypal/butterfly/issues/270)
* [268 - Add PomAddProperty operation](https://github.com/paypal/butterfly/issues/268)
* [266 - Develop condition PomJavaMatch to tell if at least one Java class from that module match a given criteria](https://github.com/paypal/butterfly/issues/266)
* [258 - Improve butterfly-test by listing all different files and folders](https://github.com/paypal/butterfly/issues/258)
* [254 - Keep original scope when replacing pom dependency](https://github.com/paypal/butterfly/issues/254)
* [247 - Add a flag to all Assert.assertTransformation methods to XML equals comparison](https://github.com/paypal/butterfly/issues/247)
* [225 - Add configuration to FindFiles to let user decide between warning, error, or value if no files are found](https://github.com/paypal/butterfly/issues/225)
* [223 - Add TransformationTemplate.set(String, Object) to add or change context attributes](https://github.com/paypal/butterfly/issues/223)
* [236 - Add PomCopyDependencies, PomAddManagedDependency and PomCopyManagedDependencies](https://github.com/paypal/butterfly/issues/236)
* [30  - Metrics JSON objects should contain all fields, even the null ones](https://github.com/paypal/butterfly/issues/30)
* [155 - Provide extension and list of templates in CLI result json](https://github.com/paypal/butterfly/issues/155)
* [212 - Create API project butterfly-metrics](https://github.com/paypal/butterfly/issues/212)
* [211 - Consolidate metrics and result JSON file into a single file](https://github.com/paypal/butterfly/issues/211)
* [210 - Major Butterfly API refactoring (does NOT include extensions API)](https://github.com/paypal/butterfly/issues/210)
* [205 - Add operation to copy dependencies from one POM file to another](https://github.com/paypal/butterfly/issues/205)
* [16  - Add XmlXPathElement and deprecate XmlElement](https://github.com/paypal/butterfly/issues/16)
* [195 - Add support to blank transformations](https://github.com/paypal/butterfly/issues/195)
* [198 - Adding support to non-pom file MavenGoal execution](https://github.com/paypal/butterfly/issues/198)
* [179 - JavaMatch should automatically ignore package-info.java files](https://github.com/paypal/butterfly/issues/179)
* [177 - Replace Exception parameters by Throwable in every Butterfly exception type and TU/TO response method](https://github.com/paypal/butterfly/issues/177)
* [173 - Small API changes and improvements](https://github.com/paypal/butterfly/issues/173)
* [192 - Add "pending manual change" application validation to TE](https://github.com/paypal/butterfly/issues/192)
* [170 - Replace Cobertura by JaCoCo](https://github.com/paypal/butterfly/issues/170)
* [188 - Create Butterfly BOM (butterfly-bom)](https://github.com/paypal/butterfly/issues/188)
* [186 - Add butterfly-test, a test utility project to help extensions development and test quality](https://github.com/paypal/butterfly/issues/186)
* [75  - Add unit tests to utilities project to make sure at least 85% code coverage P1 quality](https://github.com/paypal/butterfly/issues/75)
* [12  - Create catalog of extensions sample](https://github.com/paypal/butterfly/issues/12)
* [164 - Apply Build Scans to Gradle build](https://github.com/paypal/butterfly/issues/164)
* [163 - Replace Maven by Gradle as building tool](https://github.com/paypal/butterfly/issues/163)
* [160 - Increase unit test coverage in butterfly-utilities quality](https://github.com/paypal/butterfly/issues/160)

#### Bug fixes
* [292 - Fixing bug when performing blank template on same folder](https://github.com/paypal/butterfly/issues/292)
* [290 - Fixing butterfly-cli log file naming and improving CLI integration tests](https://github.com/paypal/butterfly/issues/290)
* [284 - Fixing butterfly-cli log file naming and improving CLI integration tests](https://github.com/paypal/butterfly/issues/284)
* [235 - Fix "optional tag" bug in PomChangeDependency](https://github.com/paypal/butterfly/issues/235)
* [224 - MultipleOperations fails to set target file if not set as relative to the transformed application folder](https://github.com/paypal/butterfly/issues/224)
* [111 - AddProperty fails if properties file is empty](https://github.com/paypal/butterfly/issues/111)
* [89 - JavaMatch breaks if Java class file is invalid](https://github.com/paypal/butterfly/issues/89)

## 2.5.0

#### Release date
April 25th, 2018

#### New features and enhancements
* [133 - Need to find arbitrary xpath in xml](https://github.com/paypal/butterfly/issues/133)
* [134 - Need to do find/replace on xpath](https://github.com/paypal/butterfly/issues/134)

#### Bug fixes
* [128 - Set up TravisCI to cache its local Maven repo](https://github.com/paypal/butterfly/issues/128)

## 2.4.0

#### Release date
March 29th, 2018.

#### New features and enhancements
* [115 - Move initialization in CompareXMLFiles to static block](https://github.com/paypal/butterfly/issues/115)
* [107 - Add javadoc to api.metrics, and hide pom.sax](https://github.com/paypal/butterfly/issues/107)
* [17 - Add automated integration tests](https://github.com/paypal/butterfly/issues/17)
* [14 - Add option -f to CLI to modify the original app folder](https://github.com/paypal/butterfly/issues/14)

#### Bug fixes
None

## 2.3.0

#### Release date
February 20th, 2018.

#### New features and enhancements
* [52 - Create Butterfly brew installer](https://github.com/paypal/butterfly/issues/52)
* [87 - Add `butterfly -l` output samples to how-to install document](https://github.com/paypal/butterfly/issues/87)
* [95 - Create a TU that, given a XML file, returns the indentation used on it](https://github.com/paypal/butterfly/issues/95)
* [51 - InsertLine TU should allow insertions before the matched line](https://github.com/paypal/butterfly/issues/51)
* [84 - Add TOs to add or change pom file parent preserving file formatting and comments](https://github.com/paypal/butterfly/issues/84)

#### Bug fixes
* [82 - If an inexistent version is requested with u argument, the error is not reported properly](https://github.com/paypal/butterfly/issues/82)

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