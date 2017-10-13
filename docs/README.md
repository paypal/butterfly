
# Butterfly

Butterfly is an application code transformation tool, and commonly it is used to perform **automated application migrations**, **upgrades** and **source code and configuration changes**.

## The transformation problem

Application changes, upgrades and migrations are usually complex, time-consuming and error prone.
Therefore, they are also extremely expensive in the short term, and might even cause worse problems (and much more expensive) in long term.

## The benefits of transformation automation

By automating application source code transformations, upgrades and migrations, overall development experience and software maintenance are improved.
In addition to that, for a given organization, the number for applications in the latest version or state is increased and the upgrade and migration processes are simplified.

## Butterfly Features

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
* Logging in silent or verbose mode, in info or debug level, and on console or file
* Post-transformation features
  * Manual steps Reporting
  * Metrics
* Usage
  * Command-Line-Interface tool
  * Extensions API
  * API for custom transformation utilities and operations
  * Facade for Java application integration

## Quick start
Read [Butterfly Quick Start](https://paypal.github.io/butterfly/QUICK_START) to learn how to use Butterfly by transforming a sample application.

## Butterfly User Guide

1. [Installing Butterfly](https://paypal.github.io/butterfly/Installing-Butterfly)
1. [Running Butterfly](https://paypal.github.io/butterfly/Running-Butterfly)
1. [Extension development guide](https://paypal.github.io/butterfly/Extension-development-guide)
1. [Design documents](https://paypal.github.io/butterfly/Design-documents)
