# butterfly-core 
This is a Java and Scala hybrid project.
It is built and its dependencies are managed using Maven and the `maven-scala-plugin`.

## Building it
Just use `mvn` as you would for any other Java project.

Example: `mvn clean package`

## Running it
The main class to be run is `com.paypal.butterfly.core.UpgradeMain`.
To run it from a terminal, also just do as you would for any other Java project.
To make it easier, you can actually rely on Maven exec plugin, as seen in the example below.

Example: `mvn  exec:java -Dexec.mainClass=com.paypal.butterfly.core.UpgradeMain`

### Usage

Run it with `-h` to show the usage.
