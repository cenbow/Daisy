@echo off
cd ..
call mvn -Dmaven.test.skip=true clean    buildnumber:create-timestamp compile package -X -Pdevelopment  tomcat7:deploy
@pause
