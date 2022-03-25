#!/bin/bash

echo Butterfly current version:
read CURRENT_VERSION

echo Butterfly new version:
read NEW_VERSION

# Setting the version by escaping the dots to use it in a regular expression to edit text files
CURRENT_VERSION_REGEX=$(echo "$CURRENT_VERSION" | sed 's/\./\\./g')

echo Applying new version in document files
sed -i '' 's/'"$CURRENT_VERSION_REGEX"'/'"$NEW_VERSION"'/g' docs/QUICK_START.md docs/Installing-Butterfly.md

echo Removing current version sample extension
rm -f docs/jar/butterfly-springboot-extension-"${CURRENT_VERSION}".jar

echo Copying new version sample extension
cp extensions-catalog/butterfly-springboot-extension/build/libs/butterfly-springboot-extension-"${NEW_VERSION}".jar docs/jar/

echo Done