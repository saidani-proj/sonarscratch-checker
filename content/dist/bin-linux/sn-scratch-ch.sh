#!/bin/bash

java -jar "$(dirname $(realpath $0))/../jar/sonarscratchchecker-1.0.0-jar-with-dependencies.jar" "$@"
