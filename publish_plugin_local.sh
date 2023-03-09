#!/bin/bash
set -e
sed -i 's/version=.*/version=3.0.0-example/g' gradle.properties
sed -i 's/PLUGIN_VERSION = .*/PLUGIN_VERSION = "3.0.0-example";/g' src/main/java/co/com/bancolombia/Constants.java
./gradlew publishToMavenLocal
