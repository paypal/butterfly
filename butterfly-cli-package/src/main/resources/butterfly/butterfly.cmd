@echo off

set __BUTTERFLY_HOME__=%~dp0

if defined BUTTERFLY_HOME (
  set __BUTTERFLY_HOME__=%BUTTERFLY_HOME%
)


java -cp "%__BUTTERFLY_HOME__%\lib\*";"%__BUTTERFLY_HOME__%\extensions\*" ^
    -Dloader.main=com.paypal.butterfly.cli.ButterflyCliApp ^
    com.paypal.butterfly.cli.ButterflyCliApp %*
