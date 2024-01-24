#!/bin/bash
set -e
TYPE=$1
echo "Generating project with type $TYPE"

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
          classpath 'co.com.bancolombia.cleanArchitecture:scaffold-clean-architecture:CURRENT_LOCAL_VERSION'
        }
      }

      apply plugin: 'co.com.bancolombia.cleanArchitecture'" >> build/toscan/build.gradle

cd build/toscan || exit
gradle ca --metrics false --example true --type $TYPE
gradle wrapper

if [ $TYPE == "reactive" ]
then
  for adapter in "asynceventbus" "binstash" "cognitotokenprovider" "dynamodb" "kms" "mongodb" "mq" "r2dbc" "redis" "restconsumer" "rsocket" "s3" "secrets" "sqs"
  do
    ./gradlew gda --type $adapter
  done

  for entry in "asynceventhandler" "graphql" "kafka" "mq" "rsocket" "sqs" "webflux"
  do
    ./gradlew gep --type $entry
  done
else
  for adapter in "binstash" "dynamodb" "jpa" "kms" "mongodb" "mq" "redis" "restconsumer" "s3" "secrets" "sqs"
  do
    ./gradlew gda --type $adapter
  done

  for entry in "mq" "restmvc" "sqs"
  do
    ./gradlew gep --type $entry
  done
fi

branch=${GITHUB_REF##*/}
git init
git checkout -b $branch
git add .
git config user.email "github@actions.bot"
git config user.name "Github Actions Bot"
git commit -am "to scan"
