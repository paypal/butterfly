
# Installing Butterfly

## Before installing Butterfly

Before installing Butterfly, make sure you have Java 7, or newer, installed (run `java -version` to check). Oracle HotSpot 7 or newer and Open JDK 8 or newer are supported. Open JDK 7 is not supported because of [this issue](https://answers.launchpad.net/ubuntu/+source/openjdk-7/+question/192941).

## Installing Butterfly on Mac with Homebrew

* See [butterfly tap](https://github.com/paypal/homebrew-butterfly) for instructions

## Installing Butterfly

1. Download and extract [butterfly-cli-package.zip](https://repo1.maven.org/maven2/com/paypal/butterfly/butterfly-cli-package/2.2.0/butterfly-cli-package-2.2.0.zip) file
1. Cd to the `butterfly` folder and run `butterfly`. If you see its help, Butterfly has been installed properly
1. Run `butterfly –l` and notice that you have no Butterfly extensions at this point

You would get a similar output on running the above command without any Butterfly extensions.
```
Starting ButterflyCliApp on Amriteyas-Mac with PID 61757 (/Users/amriteya/dev-amriteya/butterfly/lib/butterfly-cli-2.2.0.jar started by amriteya in /Users/amriteya/dev-amriteya/butterfly)
No active profile set, falling back to default profiles: default
Started ButterflyCliApp in 0.965 seconds (JVM running for 1.427)
Butterfly application transformation tool (version 2.2.0)
There are no registered extensions
```

## Installing a Butterfly extension

If you are not familiar with "Butterfly extensions", then read [What is a Butterfly extension?](https://paypal.github.io/butterfly/Extension-development-guide).

Follow the steps bellow to install a Butterfly extension.

1. Copy its jar file to the `extensions` folder under Butterfly installation folder
1. Run `butterfly –l` and check if your extension has been installed

You would get a similar output on installing the extension listed on [Quick-Start](https://paypal.github.io/butterfly/QUICK_START) page.
```
Starting ButterflyCliApp on Amriteyas-Mac with PID 62897 (/Users/amriteya/dev-amriteya/butterfly/lib/butterfly-cli-2.2.0.jar started by amriteya in /Users/amriteya/dev-amriteya/butterfly)
No active profile set, falling back to default profiles: default
Started ButterflyCliApp in 0.909 seconds (JVM running for 1.397)
Butterfly application transformation tool (version 2.2.0)
See registered extensions below (shortcut in parenthesis)

- com.extensiontest.SampleExtension: Sample extension (version 1.0.0)
	 (1) - [TT] 	 com.extensiontest.SampleTransformationTemplate 	 Sample transformation template
```

### If your Butterfly extension needs Maven

If the extension you are going to use performs Maven executions, also make sure you have Maven installed and an environment variable `M2_HOME` set to its installation folder. Notice that POM file operations are not considered Maven executions, but running `mvn verify` in the background would be an example of Maven execution.

Also, if you are using Java 8, make sure you don't have `MaxPermSize` set to your Maven `MAVEN_OPTS` environment variable, otherwise you will see annoying, but harmless, warnings when running Butterfly ([more details here](http://stackoverflow.com/questions/22634644/java-hotspottm-64-bit-server-vm-warning-ignoring-option-maxpermsize))
