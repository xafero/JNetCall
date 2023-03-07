package jnetbase.java.compat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * Backports for Java 8
 */
public final class J8 {

	@SuppressWarnings("unchecked")
	public static <T> List<T> toList(Stream<T> stream) {
		Object[] array = stream.toArray();
		List<Object> list = Arrays.asList(array);
		return (List<T>) list;
	}

	public static byte[] readAllBytes(InputStream stream) throws IOException {
		final int bufLen = 1024;
		byte[] buf = new byte[bufLen];
		int readLen;
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			while ((readLen = stream.read(buf, 0, bufLen)) != -1)
				outputStream.write(buf, 0, readLen);
			return outputStream.toByteArray();
		} catch (IOException e) {
			throw e;
		}
	}

	public static <T> CompletableFuture<T> failedFuture(Throwable ex) {
		if (ex == null)
			throw new NullPointerException();
		CompletableFuture<T> future = new CompletableFuture<T>();
		future.completeExceptionally(ex);
		return future;
	}

	public static <T> T orElseThrow(Optional<T> opt) {
		T value = opt.get();
		if (value == null) {
			throw new NoSuchElementException("No value present");
		}
		return value;
	}

	public static void put(ByteBuffer buffer, Buffer input) {
		ByteBuffer subBuffer = (ByteBuffer) input;
		buffer.put(subBuffer);
	}
}
