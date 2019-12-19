ROOT_PATH=$(dirname $(realpath $0))/../../..
$ROOT_PATH/dist/linux/sn-scratch-ch.sh -c --sleep 10000 &&
    mvn -f "$ROOT_PATH/pom.xml" jacoco:prepare-agent test sonar:sonar &&
    $ROOT_PATH/dist/linux/sn-scratch-ch.sh -r --path "$ROOT_PATH/target/sonarqube-issues.html" --project:org.sonarscratch.checker:sonarscratchchecker "$ROOT_PATH"
