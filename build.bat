@echo off

cd JVM
call mvn package
cd ..

cd NET
dotnet test
cd ..
