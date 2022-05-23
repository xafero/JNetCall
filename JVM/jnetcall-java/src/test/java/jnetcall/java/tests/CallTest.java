package jnetcall.java.tests;

import jnetcall.java.client.ServiceClient;
import jnetcall.java.client.ServiceEnv;
import org.example.api.IStringCache;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class CallTest {
    final String Path =
            ServiceEnv.buildPath("..\\..\\..\\NET\\Alien.Sharp\\bin\\Debug\\net6.0\\Alien.Sharp.exe");

    @Test
    public void shouldCache() throws Exception {
        var input = new String[]{"life", "on", "mars"};
        try (var client = ServiceClient.create(IStringCache.class, Path)) {

            client.set(42, input[0]);
            assertEquals(1, client.getSize());
            assertEquals(input[0], client.get(42));

            client.delete(42);
            assertEquals(0, client.getSize());

            client.set(43, input[1]);
            client.set(44, input[2]);
            assertEquals(2, client.getSize());

            assertEquals(input[1], client.get(43));
            assertEquals(input[2], client.get(44));

            client.delete(43);
            client.delete(44);
            assertEquals(0, client.getSize());
        }
    }
}
