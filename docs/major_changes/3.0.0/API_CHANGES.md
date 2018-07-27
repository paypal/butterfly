
# Butterfly 3.0.0 API Changes

This page documents all API changes introduced by Butterfly 3.0.0.

Notice that it does not include additions, that is documented in [Butterfly 3.0.0 New Features](https://paypal.github.io/butterfly/major_changes/3.0.0/NEW_FEATURES.md).

### Moved classes and interfaces

| From | To | Notes |
|---|---|---|
|`com.paypal.butterfly.extensions.api.metrics`|`com.paypal.butterfly.metrics`|API project `butterfly-metrics` has been created. Also, `AbortDetails` class was converted to an interface|

### Removed classes and interfaces

Notice that all these removed classes and interfaces were already marked as deprecated in the latest minor versions of Butterfly 2.

| Class or interface | Replacement | Notes | TO BE DEPRECATED |
|---|---|---|---|
|`com.paypal.butterfly.utilities.xml.XmlElement`|`com.paypal.butterfly.utilities.xml.XmlXPathElement`|||

### Removed methods

Notice that all these removed methods were already marked as deprecated in the latest minor versions of Butterfly 2.

| Method | Replacement | Notes | TO BE DEPRECATED |
|---|---|---|:---:|
|`com.paypal.butterfly.extensions.api.TransformationUtility.abortOnFailure()`|`com.paypal.butterfly.extensions.api.TransformationUtility.isAbortOnFailure()`||YES|
|`com.paypal.butterfly.extensions.api.TransformationUtility.abortOnFailure(boolean, String)`|`com.paypal.butterfly.extensions.api.TransformationUtility.abortOnFailure(String)`||YES|
|`com.paypal.butterfly.utilities.maven.MavenGoal.setFailAtEnd()`|NA|Removed after upgrading `org.apache.maven.shared:maven-invoker` from version 2.2 to 3.0.1, which removed method `org.apache.maven.shared.invoker.InvocationRequest.setFailureBehavior(String)`|YES|
|`com.paypal.butterfly.extensions.api.metrics.AbortDetails.getExceptionClass()`|`com.paypal.butterfly.metrics.AbortDetails.getExceptionClassName()`||YES|
|`com.paypal.butterfly.facade.Configuration.Configuration()`|`com.paypal.butterfly.facade.ButterflyFacade.newConfiguration()`|`Configuration` class has been converted to an interface. The factory method in the facade should be used instead to get a new configuration object|YES|
|`com.paypal.butterfly.facade.Configuration.Configuration(File, boolean)`|`com.paypal.butterfly.facade.ButterflyFacade.newConfiguration()`|`Configuration` class has been converted to an interface. The factory method in the facade should be used instead to get a new configuration object|YES|

### Changed methods

This section list methods that had their signature preserved, in terms of return type and list of parameters, but that might have had their contract changed in terms of documented behavior or exceptions they might throw.

| Method | What changed |
|---|---|
|`com.paypal.butterfly.extensions.api.Extension.getRootPomFile(File)`|Instead of returning `null` it now throws `IOException`, if pom file does not exist, or any error happens when trying to read it. Also it throws `XmlPullParserException` if any error happens when trying to parse the pom file.|
