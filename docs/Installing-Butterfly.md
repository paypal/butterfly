
# Installing Butterfly

## Before installing Butterfly

Before installing Butterfly, make sure you have Java 7, or newer, installed (run `java -version` to check). Oracle HotSpot 7 or newer and Open JDK 8 or newer are supported. Open JDK 7 is not supported because of [this issue](https://answers.launchpad.net/ubuntu/+source/openjdk-7/+question/192941).

## Installing Butterfly

1. Download and extract [butterfly-cli-package.zip](https://oss.sonatype.org/content/repositories/snapshots/com/paypal/butterfly/butterfly-cli-package/2.0.0-SNAPSHOT/butterfly-cli-package-2.0.0-20171017.162446-1.zip) file
1. Cd to the `butterfly` folder and run `butterfly`. If you see its help, Butterfly has been installed properly
1. Run `butterfly –l` and notice that you have no Butterfly extensions at this point

## Installing an extension

1. Copy its jar file to the `lib` folder under Butterfly installation folder
1. Run `butterfly –l` and check if your extension has been installed

### If your extension needs Maven

If the extension you are going to use performs Maven operations (POM file changes for example), also make sure you have Maven installed and an environment variable `M2_HOME` set to its installation folder.
Also, if you are using Java 8, make sure you don't have `MaxPermSize` set to your Maven `MAVEN_OPTS` environment variable, otherwise you will see annoying, but harmless, warnings when running Butterfly ([more details here](http://stackoverflow.com/questions/22634644/java-hotspottm-64-bit-server-vm-warning-ignoring-option-maxpermsize))
