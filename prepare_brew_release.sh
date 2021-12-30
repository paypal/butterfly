#!/bin/bash

echo
echo This script will prepare Butterfly for Homebrew release. Please make sure first to have it installed in your MacOS. More details about Homebrew at https://docs.brew.sh/.
echo
echo "<Press enter to continue>"
read foo
echo Butterfly new version:
read NEW_VERSION
echo
echo A new brew Formula will be created now in your computer and then a text editor will be automatically opened. Follow these instructions then:
echo    1\. Copy the sha256 value from it echo and close the text editor.
echo    1\. Change file https://github.com/paypal/homebrew-butterfly/blob/master/Formula/butterfly.rb providing the new Butterfly VERSION and sha256 value.
echo    1\. Set commit message to \"Updating brew formula for 3.2.2 release\"
echo    1\. Finally, test the new brew installation and make sure it works .
echo
echo "<Press enter to continue>"
read foo
echo Important notes:
echo    1\. if you get an error message stating that the formula already exists \(the formula file path will be shown\), remove it, and then run this script again.
echo    2\. if you get a \"no matches found\" error, that means the release process in Nexus is not fully syncronized yet. Wait an hour or two and try again.
echo
echo "<Press enter to continue>"
read foo

brew create https://search.maven.org/remotecontent?filepath=com/paypal/butterfly/butterfly-cli-package/"${NEW_VERSION}"/butterfly-cli-package-"${NEW_VERSION}".zip