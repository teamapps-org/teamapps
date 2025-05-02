#!/bin/bash

export JDK_JAVA_OPTIONS='--add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.text=ALL-UNNAMED --add-opens java.desktop/java.awt.font=ALL-UNNAMED'
mvn -e -P javadoc-jar,sources-jar,license-handling,release release:prepare release:perform && cd target/checkout && mvn nexus-staging:release
