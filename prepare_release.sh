#!/bin/bash

echo Butterfly current version:
read CURRENT_VERSION

echo Butterfly new version:
read NEW_VERSION

# Setting the version escaping the dots to use it in a regular expression to edit text files
CURRENT_VERSION_REGEX=$(echo "$CURRENT_VERSION" | sed 's/\./\\./g')

echo CURRENT_VERSION_REGEX="$CURRENT_VERSION_REGEX"

# Applying new version in document files
sed -i '' 's/'"$CURRENT_VERSION_REGEX"'/'"$NEW_VERSION"'/g' docs/QUICK_START.md docs/Installing-Butterfly.md

# Removing current version sample extension
rm -f docs/jar/butterfly-springboot-extension-"${CURRENT_VERSION}".jar

# Copying new version sample extension
cp extensions-catalog/butterfly-springboot-extension/build/libs/butterfly-springboot-extension-"${NEW_VERSION}".jar docs/jar/