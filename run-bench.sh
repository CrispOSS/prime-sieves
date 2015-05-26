#!/bin/bash

mvn clean install
cd target
java -Djmh.shutdownTimeout=1 -Djmh.shutdownTimeout.step=1 -Xmx8000m -jar prime-sieves-bench.jar

