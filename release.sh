#!/bin/bash

export JDK_JAVA_OPTIONS='--add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.text=ALL-UNNAMED --add-opens java.desktop/java.awt.font=ALL-UNNAMED'
mvn -e -P javadoc-jar,sources-jar,license-handling,release release:prepare release:perform

echo "---------"
echo "HEADS UP!"
echo "The artifact has been uploaded for staging but is not yet published. This is now a manual step!"
echo "Please visit https://central.sonatype.com/publishing/deployments to publish the artifact."
echo "---------"