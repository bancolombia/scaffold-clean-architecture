#!/bin/bash
set -e
if ! command -v gsed &> /dev/null
then
    echo replacing with sed
    sed -i 's/version=.*/version=CURRENT_LOCAL_VERSION/g' gradle.properties
    sed -i 's/PLUGIN_VERSION = .*/PLUGIN_VERSION = "CURRENT_LOCAL_VERSION";/g' src/main/java/co/com/bancolombia/Constants.java
else
    echo replacing with gsed
    gsed -i 's/version=.*/version=CURRENT_LOCAL_VERSION/g' gradle.properties
    gsed -i 's/PLUGIN_VERSION = .*/PLUGIN_VERSION = "CURRENT_LOCAL_VERSION";/g' src/main/java/co/com/bancolombia/Constants.java
fi
./gradlew publishToMavenLocal
git restore gradle.properties
git restore src/main/java/co/com/bancolombia/Constants.java