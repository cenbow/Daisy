@echo off
cd ..
call mvn org.apache.maven.plugins:maven-eclipse-plugin:2.6:eclipse -X
@pause