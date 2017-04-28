@echo off
cd ..
call mvn -Dmaven.test.skip=true clean  buildnumber:create-timestamp compile   package -Pproduction,app -X
@pause