#!/bin/bash
set -e
gsed -i 's/version=.*/version=CURRENT_LOCAL_VERSION/g' gradle.properties
gsed -i 's/PLUGIN_VERSION = .*/PLUGIN_VERSION = "CURRENT_LOCAL_VERSION";/g' src/main/java/co/com/bancolombia/Constants.java
./gradlew publishToMavenLocal
