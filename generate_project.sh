#!/bin/bash
set -e
rm -rf build/toscan
mkdir build/toscan
echo "buildscript {
        repositories {
          mavenLocal()
          maven {
            url 'https://plugins.gradle.org/m2/'
          }
        }
        dependencies {
          classpath 'co.com.bancolombia.cleanArchitecture:scaffold-clean-architecture:3.0.0-example'
        }
      }

      apply plugin: 'co.com.bancolombia.cleanArchitecture'" >> build/toscan/build.gradle

cd build/toscan || exit
gradle ca --metrics false --example true
gradle wrapper

for adapter in "sqs" "dynamodb" "mq" "s3" "secrets" "kms" "r2dbc" "rsocket" "redis" "restconsumer" "asynceventbus" "mongodb"
do
  ./gradlew gda --type $adapter
done

for entry in "webflux" "rsocket" "graphql" "asynceventhandler" "mq" "sqs"
do
  ./gradlew gep --type $entry
done
