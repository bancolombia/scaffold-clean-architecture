#!/bin/bash
set -e
TYPE=$1
MY_DIR="build/toscan/$TYPE"
SETTINGS_GRADLE="settings.gradle"
MAIN_GRADLE="main.gradle"

echo "Generating project with type $TYPE in $MY_DIR"

rm -rf $MY_DIR
mkdir -p $MY_DIR
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

      apply plugin: 'co.com.bancolombia.cleanArchitecture'" >> $MY_DIR/build.gradle

cd $MY_DIR || exit
gradle --version
gradle ca --metrics false --example true --type $TYPE
gradle wrapper

echo "Replacing //mavenLocal() with mavenLocal() in $SETTINGS_GRADLE"
if sed --version >/dev/null 2>&1; then
  # GNU sed (Linux)
  sed -i 's|//mavenLocal|mavenLocal|g' "$SETTINGS_GRADLE"
else
  # BSD sed (macOS)
  sed -i '' 's|//mavenLocal|mavenLocal|g' "$SETTINGS_GRADLE"
fi

echo "Inserting mavenLocal() into the repositories block of $MAIN_GRADLE"
if sed --version >/dev/null 2>&1; then
  # GNU sed (Linux)
  sed -i '/repositories[[:space:]]*{/a\
        mavenLocal()
' "$MAIN_GRADLE"
else
  # BSD sed (macOS)
  sed -i '' '/repositories[[:space:]]*{/a\
        mavenLocal()
' "$MAIN_GRADLE"
 fi

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
  for adapter in "binstash" "dynamodb" "jpa" "kms" "mongodb" "mq" "redis" "restconsumer" "s3" "secrets" "sqs"
  do
    ./gradlew gda --type $adapter
  done

  for entry in "mq" "restmvc" "sqs" "graphql"
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
