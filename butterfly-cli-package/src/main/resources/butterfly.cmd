@echo off

set __BUTTERFLY_HOME__=%~dp0

if defined BUTTERFLY_HOME (
  set __BUTTERFLY_HOME__=%BUTTERFLY_HOME%
)

java -cp "%__BUTTERFLY_HOME__%\lib\*";"%__BUTTERFLY_HOME__%\extensions\*" com.paypal.butterfly.cli.ButterflyCliApp %*
