#!/bin/sh

cd JVM
mvn clean package
cd ..

cd NET
dotnet test
cd ..
