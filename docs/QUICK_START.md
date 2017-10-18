
# Quick Start

In this document we will install Butterfly, and run it to transform a sample application, a REST service originally following a WAR structure, to be Spring Boot based.

Follow the steps below.

### 1- Installing Butterfly

* Install Butterfly following [this document](https://paypal.github.io/butterfly/Installing-Butterfly).
* Following the same document, install [sample-extension-1.0.0.jar](https://paypal.github.io/butterfly/jar/sample-extension-1.0.0.jar) Butterfly extension, which will be used to transform the sample app.

### 2- The sample application

* Download the sample application from [sample-app.zip](https://paypal.github.io/butterfly/zip/sample-app.zip) and unzip it in a working directory.
* Take a look at the application files. Notice that, as a typical WAR project, it has, for example, a `web.xml` file under a `webapp/WEB-INF` folder.

 ```
 sample-app
 ├── README.md
 ├── pom.xml
 └── src
     └── main
         ├── java
         │   └── com
         │       └── sample
         │           └── app
         │               ├── Echo.java
         │               ├── EchoMessage.java
         │               ├── EchoMessageCreator.java
         │               └── JaxrsApplication.java
         └── webapp
             └── WEB-INF
                 ├── applicationContext.xml
                 └── web.xml
 ```
* Optionally, build, deploy and test it. You can do so by running the command below, deploying the generated `war` file to a Servlet container (like Tomcat for example), and then following the `README.md` file in the app root folder to test it.

 ```
 mvn package
 ```

### 3- Running Butterfly

* Now, finally Butterfly will be used to transform the app. Run the command below.
 
 ```
 butterfly sample-app
 ```
 
* Check if you got an output similar to this:

 ``` 
 No active profile set, falling back to default profiles: default
 Started ButterflyCliApp in 1.097 seconds (JVM running for 1.563)
 Butterfly application transformation tool (version 2.0.0)
 
 Transformation template associated with shortcut 1: com.test.SampleTransformationTemplate
 Application to be transformed: /Users/fabio/dev/butterfly/butterfly/tests/sample-app
 Transformation template class: com.test.SampleTransformationTemplate
 Performing transformation (it might take a few seconds)
 
 ----------------------------------------------
 Application has been transformed successfully!
 ----------------------------------------------
 Transformed application folder: /Users/fabio/dev/butterfly/butterfly/transformed-apps/sample-app-transformed-20171016175315818
 Check log file for details: /Users/fabio/dev/butterfly/butterfly/logs/sample-app_20171016175311736.log
 ```

* Notice that Butterfly placed the transformed application in a new folder, with a timestamp as suffix on its name. 

 ```
 Transformed application folder: /Users/fabio/dev/butterfly/butterfly/transformed-apps/sample-app-transformed-20171016175315818
 ```

* Notice also that it differs from the original app, as expected. For example, the whole `webapp` folder has been removed.

 ```
 sample-app-transformed-20171016175315818
 ├── README.md
 ├── pom.xml
 └── src
     └── main
         └── java
             └── com
                 └── sample
                     └── app
                         ├── Application.java
                         ├── Echo.java
                         ├── EchoMessage.java
                         ├── EchoMessageCreator.java
                         └── JaxrsApplication.java

 ```
* Open the log file, its location was printed on the console.

 ```
 Check log file for details: /Users/fabio/dev/butterfly/butterfly/logs/sample-app_20171016175311736.log
 ```
* See a description of all changes that were performed to transform the app.

 ```
 [18:11:36.951] [INFO] Beginning transformation
 [18:11:36.977] [INFO] 	1	 - Packaging for POM file /pom.xml has been changed to jar
 [18:11:36.979] [INFO] 	2	 - Parent for POM file (/pom.xml) has been set to org.springframework.boot:spring-boot-starter-parent:pom:1.5.6.RELEASE
 [18:11:36.984] [INFO] 	3	 - Plugin org.springframework.boot:spring-boot-maven-plugin has been added to POM file /pom.xml
 [18:11:36.986] [INFO] 	4	 - Property spring.version has been removed from POM file /pom.xml
 [18:11:36.987] [INFO] 	5	 - Dependency org.springframework:spring-context has been removed from POM file /pom.xml
 [18:11:36.988] [INFO] 	6	 - Dependency org.springframework:spring-web has been removed from POM file /pom.xml
 [18:11:36.989] [INFO] 	7	 - Dependency org.jboss.resteasy:resteasy-servlet-initializer has been removed from POM file /pom.xml
 [18:11:36.990] [INFO] 	8	 - Dependency org.jboss.resteasy:resteasy-jackson2-provider has been removed from POM file /pom.xml
 [18:11:36.991] [INFO] 	9	 - Dependency org.jboss.resteasy:resteasy-spring has been removed from POM file /pom.xml
 [18:11:36.992] [INFO] 	10	 - Dependency org.springframework.boot:spring-boot-starter-web has been added to POM file /pom.xml
 [18:11:36.993] [INFO] 	11	 - Dependency com.paypal.springboot:resteasy-spring-boot-starter:2.3.3-RELEASE has been added to POM file /pom.xml
 [18:11:37.001] [INFO] 	12	 - Folder '/src/main/webapp' has been removed
 [18:11:37.002] [INFO] 	13	 - File 'file:/Users/fabio/dev/butterfly/butterfly/tests/sample-extension/target/classes/Application.java' has been downloaded at /src/main/java/com/sample/app
 [18:11:37.004] [INFO] 	14	 - File README.md has had 1 line(s) where text replacement was applied based on regular expression '(Spring framework)'
 [18:11:37.005] [INFO] 	15	 - File README.md has had 1 line(s) where text replacement was applied based on regular expression '(Just deploy its war file to a Servlet container and start it.)'
 [18:11:37.006] [INFO] 	16	 - Text has been inserted from file:/Users/fabio/dev/butterfly/butterfly/tests/sample-extension/target/classes/README_piece_of_text.txt to README.md after 1 line(s) that matches regular expression '(There are two ways to start the application:)'
 [18:11:37.006] [INFO] Transformation has been completed
 ```

### 4- The transformed application

* Optionally, build, run and test the transformed application. You can build and start it very easily by running: 

 ```
 mvn package spring-boot:run
 ```
* To test it, do as you did before (follow its README file), since the app functionality remains the same.

### 5- What is next?

Butterfly has a variaty of very helpful running modes. Read [Running Butterfly](https://paypal.github.io/butterfly/Running-Butterfly) for further information.

If you are interested in developing your own Butterfly extension, then read [Extension development guide](https://paypal.github.io/butterfly/Extension-development-guide).