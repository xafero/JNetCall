# JNetCall
Java VM .NET Call abstraction

## History
In the past there was atleast one solution for .NET Framework 4.5+ and Java 8 for reusing software libraries written in Java.
But now I needed the ability to call current Java 18 code from C# 10 on .NET 6, so I created this project here.

## Summary
* Call Java from C#
(see NET/Alienator.Sharp/Program.cs) 
(Main or Native)

* Call C# from Java
(see JVM/alienator-java/src/main/java/org/example/Main.java) 
(Main or Native)

## How to build
If you are on Microsoft Windows, use "build.bat", 
otherwise "build.sh".

## Environments tested
* OpenJDK 18 and .NET 6 on Ubuntu 22.04
* Oracle's JDK 18 and .NET 6 on Windows 11

## License
Everything is licensed under GNU Affero General Public License (AGPL).
