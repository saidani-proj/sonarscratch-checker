#!/bin/bash

ROOT_PATH=$(dirname $(realpath $0))/../../..
sn-scratch-ch -c --sleep 10000 &&
    mvn -f "$ROOT_PATH/pom.xml" jacoco:prepare-agent test sonar:sonar &&
    sn-scratch-ch -r --path "$ROOT_PATH/target/sonarqube-issues.html" --project:org.sonarscratch.checker:sonarscratchchecker "$ROOT_PATH"
