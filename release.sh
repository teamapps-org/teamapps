#!/bin/bash

mvn -P javadoc-jar,sources-jar,license-handling,release release:prepare release:perform && cd target/checkout && mvn nexus-staging:release