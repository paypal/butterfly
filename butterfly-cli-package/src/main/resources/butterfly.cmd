@echo off
set SCRIPT_PATH=%cd%
if "%BUTTERFLY_HOME%"=="" set BUTTERFLY_HOME=%SCRIPT_PATH%
java -cp "%BUTTERFLY_HOME%\lib\*";"%BUTTERFLY_HOME%\extensions\*" com.paypal.butterfly.cli.ButterflyCliApp %*