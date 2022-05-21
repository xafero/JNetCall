@echo off

cd JVM
call mvn clean package
cd ..

cd NET
dotnet test
cd ..
