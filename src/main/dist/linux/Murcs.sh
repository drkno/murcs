#!/bin/bash

jarPath=../${project.artifactId}-${project.version}.jar

if type -p java; then
    javaPath=java
elif [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]];  then
    javaPath="$JAVA_HOME/bin/java"
fi

if [[ "$javaPath" ]]; then
    version=$("$javaPath" -version 2>&1 | awk -F '"' '/version/ {print $2}')
    if [[ "$version" > "1.8.0_59" ]]; then
        $javaPath -jar $jarPath
    else
        echo "Your Java version ($version) is not up to date enough to run this application."
        echo "Please update to at least Java 8u60 to use this application."
        exit -2
    fi
else
    echo "Please install at least Java 8u60 to use this application."
    exit -1
fi