
# Butterfly 3.0.0 API Changes

This page documents all API changes introduced by Butterfly 3.0.0.

Notice that it does not include additions, that is documented in [Butterfly 3.0.0 New Features](https://paypal.github.io/butterfly/major_changes/3.0.0/NEW_FEATURES.md).

### Moved classes and interfaces

| From | To | Notes |
|---|---|---|
||||

### Removed classes and interfaces

Notice that all these removed classes and interfaces were already marked as deprecated in the latest minor versions of Butterfly 2.

| Class or interface | Replacement | Notes | TO BE DEPRECATED |
|---|---|---|---|
|||||

### Removed methods

Notice that all these removed methods were already marked as deprecated in the latest minor versions of Butterfly 2.

| Method | Replacement | Notes | TO BE DEPRECATED |
|---|---|---|:---:|
|`com.paypal.butterfly.extensions.api.TransformationUtility.abortOnFailure()`|`com.paypal.butterfly.extensions.api.TransformationUtility.isAbortOnFailure()`||YES|
|`com.paypal.butterfly.extensions.api.TransformationUtility.abortOnFailure(boolean, String)`|`com.paypal.butterfly.extensions.api.TransformationUtility.abortOnFailure(String)`||YES|