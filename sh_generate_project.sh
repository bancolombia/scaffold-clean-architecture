#!/bin/bash
set -e
TYPE=$1
MY_DIR="build/toscan/$TYPE"
SETTINGS_GRADLE="settings.gradle"
MAIN_GRADLE="main.gradle"

echo "Generating project with type $TYPE in $MY_DIR"

rm -rf $MY_DIR
mkdir -p $MY_DIR
echo "plugins {
        id 'co.com.bancolombia.cleanArchitecture' version 'CURRENT_LOCAL_VERSION'
      }
      repositories {
          mavenLocal()
          mavenCentral()
      }
      " >> $MY_DIR/build.gradle

echo "pluginManagement {
          repositories {
              mavenLocal()
              gradlePluginPortal()
          }
      }" >> $MY_DIR/settings.gradle

cd $MY_DIR || exit
gradle --version
gradle ca --metrics false --example true --type $TYPE
gradle wrapper

if [ $TYPE == "reactive" ]
then
  for adapter in "asynceventbus" "binstash" "cognitotokenprovider" "dynamodb" "kms" "mongodb" "mq" "r2dbc" "redis" "restconsumer" "rsocket" "s3" "secrets" "sqs" "secretskafkastrimzi"
  do
    ./gradlew gda --type $adapter
  done

  for entry in "asynceventhandler" "graphql" "kafka" "mcp" "mq" "rsocket" "sqs" "webflux" "kafkastrimzi"
  do
    ./gradlew gep --type $entry
  done
else
  for adapter in "dynamodb" "jpa" "kms" "mongodb" "mq" "redis" "restconsumer" "s3" "secrets" "sqs"
  do
    ./gradlew gda --type $adapter
  done

  for entry in "mq" "restmvc" "sqs" "graphql"
  do
    ./gradlew gep --type $entry
  done
fi

branch="${GITHUB_REF##*/}"
branch="${branch:-main}"
git init
git checkout -b $branch
git add .
git config user.email "github@actions.bot"
git config user.name "Github Actions Bot"
git commit -am "to scan"
