#!/bin/bash

mvn clean install
cd target
java -XX:+UseG1GC -XX:+UseStringDeduplication -Xmx8000m -jar prime-sieves-bench.jar

