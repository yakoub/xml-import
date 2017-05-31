#!/usr/bin/bash

export CLASSPATH=.:/usr/share/java/mysql-connector-java.jar
javac *.java
for input in `ls data`; do
  echo $input
  java Read data/$input
done
