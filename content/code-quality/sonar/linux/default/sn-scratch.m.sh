#!/bin/bash

sn-scratch new --name sonarqube:7.9.1-community --limit 850m "$(dirname $(realpath $0))/../../images" --debug
