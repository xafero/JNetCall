#!/bin/sh

cd JVM
mvn package
cd ..

cd NET
dotnet test
cd ..
