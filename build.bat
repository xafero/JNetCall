@echo off

echo.
cd NET
dotnet build
cd ..

echo.
cd JVM
call mvn clean package
cd ..

echo.
cd NET
rd /s /q JNetCall.Sharp.Tests\TestResults
rd /s /q JNetProto.Sharp.Tests\TestResults
dotnet test --collect:"XPlat Code Coverage"
reportgenerator "-reports:**/coverage.cobertura.xml" "-targetdir:coverage" -reporttypes:Html
cd ..

echo.
cd JVM
echo "" | java -jar alienator-java\target\alienator-java.jar
cd ..

echo.
cd NET
echo "" | Alienator.Sharp\bin\Debug\net6.0\Alienator.Sharp.exe
cd ..
