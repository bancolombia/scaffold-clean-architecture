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
          classpath 'co.com.bancolombia.cleanArchitecture:scaffold-clean-architecture:3.4.0'
        }
      }

      apply plugin: 'co.com.bancolombia.cleanArchitecture'" >> build/toscan/build.gradle

cd build/toscan || exit
gradle ca --metrics false --example true --type $TYPE
gradle wrapper

if [ $TYPE == "reactive" ]
then
  for adapter in "sqs" "dynamodb" "mq" "s3" "secrets" "kms" "r2dbc" "rsocket" "redis" "restconsumer" "mongodb" "asynceventbus"
  do
    ./gradlew gda --type $adapter
  done

  for entry in "webflux" "rsocket" "graphql" "mq" "sqs" "asynceventhandler"
  do
    ./gradlew gep --type $entry
  done
else
  for adapter in "sqs" "dynamodb" "mq" "s3" "secrets" "kms" "jpa" "redis" "restconsumer" "mongodb"
  do
    ./gradlew gda --type $adapter
  done

  for entry in "restmvc" "mq" "sqs"
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
