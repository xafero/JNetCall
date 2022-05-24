#!/bin/sh

echo ""
cd JVM
mvn clean package
cd ..

echo ""
cd NET
dotnet build
dotnet test
cd ..

echo ""
cd JVM
echo "" | java -jar alienator-java/target/alienator-java.jar
cd ..

echo ""
cd NET
echo "" | Alienator.Sharp/bin/Debug/net6.0/Alienator.Sharp
cd ..

echo ""
