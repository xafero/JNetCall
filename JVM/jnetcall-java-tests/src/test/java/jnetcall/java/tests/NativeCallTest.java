package jnetcall.java.tests;

import jnetcall.java.client.InProcClient;

import static jnetcall.java.client.tools.ServiceEnv.buildPath;

public final class NativeCallTest extends CallTest {

    final String Path =
        buildPath("..\\..\\..\\NET\\Alien2.Sharp\\bin\\Debug\\net6.0\\Alien2.Sharp.dll");

    @Override
    protected <T extends AutoCloseable> T create(Class<T> clazz) {
        return InProcClient.create(clazz, Path);
    }
}
