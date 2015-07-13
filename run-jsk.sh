#!/bin/bash

JPID=$1
SJK_JAR="sjk-plus-0.3.6.jar"

cd target
wget -q --show-progress https://bintray.com/artifact/download/aragozin/generic/${SJK_JAR}

java -jar ${SJK_JAR} ttop -p ${JPID} -n 30 -f jabs-*
