package jnetcall.java.tests;

import static jnetcall.java.client.tools.ServiceEnv.buildPath;

import jnetcall.java.client.StdIOClient;

public final class MainCallTest extends CallTest {

    final String Path =
        buildPath("..\\..\\..\\NET\\Alien1.Sharp\\bin\\Debug\\net6.0\\Alien1.Sharp.exe");

    @Override
    protected <T extends AutoCloseable> T create(Class<T> clazz) {
        return StdIOClient.create(clazz, Path);
    }
}
